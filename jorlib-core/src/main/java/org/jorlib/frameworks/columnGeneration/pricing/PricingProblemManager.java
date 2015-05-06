/* ==========================================
 * jORLib : a free Java OR library
 * ==========================================
 *
 * Project Info:  https://github.com/jkinable/jorlib
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under GPLv3
 *
 */
/* -----------------
 * PricingProblemManager.java
 * -----------------
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * Original Author:  Joris Kinable
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 *
 */
package org.jorlib.frameworks.columnGeneration.pricing;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.util.Configuration;

/**
 * Class which takes care of the parallel execution of the pricing problems.
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public class PricingProblemManager<T, U extends AbstractColumn<T,U,V>, V extends AbstractPricingProblem<T,U,V>> {
	
	private static final Configuration config=Configuration.getConfiguration();
	
	//For each PricingProblemSolver and for each Pricing Problem we have a new instance of the PricingProblemSolver. All instances corresponding to the same
	//solver are bundled together in a PricingProblemBundle. Note that the list of pricingProblemBunddles is hierarchical. The solvers are invoked in the order
	//determined by the order of the bundles. 
	private final List<PricingProblemBundle<T, U, V>> pricingProblemBundles;
	//List of tasks which can be invoked in parallel to calculate bounds on the pricing problems
	private final Map<PricingProblemSolver<T, U, V>, Callable<Double>> ppBoundTasks;
	
	private final ExecutorService executor;
	private final List<Future<Void>> futures;
	
	/**
	 * Note: the parameter is the number of pricing problems, NOT the number of algorithms used to solve the pricing problems.
	 * @param nrPricingProblemsPerGroup Number of pricing problems that have to be solved for every algorithm.
	 */
	public PricingProblemManager(List<V> pricingProblems, List<PricingProblemBundle<T, U, V>> pricingProblemBundles){
		this.pricingProblemBundles=pricingProblemBundles;
		
		//Create tasks which calculate bounds on the pricing problems
		ppBoundTasks=new HashMap<>();
		for(PricingProblemBundle<T, U, V> pricingProblemBunddle : pricingProblemBundles){
			for(PricingProblemSolver<T, U, V> solver : pricingProblemBunddle.solverInstances){
				Callable<Double> task=new Callable<Double>() {
					@Override
					public Double call() throws Exception {
						return solver.getUpperbound();  //Gets the upper bound on the pricing problem through the solver instance
					}
				};
				ppBoundTasks.put(solver, task);
			}
		}
		
		//Define workers
		executor = Executors.newFixedThreadPool(config.MAXTHREADS); //Creates a threat pool consisting of MAXTHREADS threats
		futures=new ArrayList<Future<Void>>(pricingProblems.size());
	}
	
	/**
	 * Solve the pricing problems in parallel
	 * @param solver Pricing Solver which should be invoked 
	 * @return List of columns which have been generated in the pricing problems.
	 * @throws TimeLimitExceededException
	 */
	public List<U> solvePricingProblems(int solverID) throws TimeLimitExceededException{
		PricingProblemBundle<T, U, V> bunddle=pricingProblemBundles.get(solverID);
		
		//1. schedule pricing problems
		for(PricingProblemSolver<T, U, V> solverInstance : bunddle.solverInstances){
			Future<Void> f=executor.submit(solverInstance);
        	futures.add(f);
		}
		
		//2. Wait for completion and check whether any of the threads has thrown an exception which needs to be handled upstream
		for(Future<Void> f: futures){
			try {
				f.get(); //get() is a blocking procedure
			} catch (ExecutionException e) {
				if(e.getCause() instanceof TimeLimitExceededException){
					this.shutdownAndAwaitTermination(executor); //Shut down the executor.
					throw (TimeLimitExceededException)e.getCause(); //Propagate the exception
				}else
					e.printStackTrace();
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//3. Collect and return results
		List<U> newColumns=new ArrayList<>();
		for(PricingProblemSolver<T, U, V> solverInstance : bunddle.solverInstances){
			newColumns.addAll(solverInstance.getColumns());
		}
		
		return newColumns;
	}
	
	/**
	 * Invokes pricingSolver.getUpperBound() in parallel for all pricing problems defined.
	 * @param solverID Solver on which getUpperBound() is invoked.
	 * @return array containing the bounds calculated for each pricing problem
	 */
	public double[] getBoundsOnPricingProblems(int solverID){
		//Get the bunddle of solverInstances corresponding to the solverID
		PricingProblemBundle<T, U, V> bunddle=pricingProblemBundles.get(solverID);
		double[] bounds=new double[bunddle.solverInstances.size()];
		//Submit all the relevant getUpperBound() tasks to the executor
		List<Future<Double>> futureList=new ArrayList<Future<Double>>();
		for(PricingProblemSolver<T, U, V> solverInstance : bunddle.solverInstances){
			Callable<Double> task=ppBoundTasks.get(solverInstance);
			Future<Double> f=executor.submit(task);
			futureList.add(f);
		}
		//Query the results of each task one by one
		for(int i=0; i<bounds.length; i++){
			try {
				bounds[i]=futureList.get(i).get(); //Get result, note that this is a blocking procedure!
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		return bounds;
	}
	
	/**
	 * Future point in time when the pricing problem must be finished
	 * @param timeLimit
	 */
	public void setTimeLimit(long timeLimit){
		for(PricingProblemBundle<T, U, V> bunddle : pricingProblemBundles){
			for(PricingProblemSolver<T, U, V> solverInstance : bunddle.solverInstances){
				solverInstance.setTimeLimit(timeLimit);
			}
		}
	}
	
	/**
	 * Shut down the executors
	 */
	private void shutdownAndAwaitTermination(ExecutorService pool) {
		// Prevent new tasks from being submitted
		pool.shutdownNow(); 
		try {
			// Wait a while for tasks to respond to being cancelled
			if (!pool.awaitTermination(60, TimeUnit.SECONDS))
				System.err.println("Pool did not terminate");
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt nodeStatus
			Thread.currentThread().interrupt();
		}
	}
	
	/**
	 * Close the pricing problems
	 */
	public void close(){
		executor.shutdownNow();
		//Close pricing problems
		for(PricingProblemBundle<T, U, V> bunddle : pricingProblemBundles){
			for(PricingProblemSolver<T, U, V> solverInstance : bunddle.solverInstances){
				solverInstance.close();
			}
		}
	}

}

