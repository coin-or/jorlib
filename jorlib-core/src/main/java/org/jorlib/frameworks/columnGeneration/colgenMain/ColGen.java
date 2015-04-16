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
 * ColGen.java
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
package org.jorlib.frameworks.columnGeneration.colgenMain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.master.AbstractMaster;
import org.jorlib.frameworks.columnGeneration.master.MasterData;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.Inequality;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblem;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemBundle;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemManager;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemSolver;
import org.jorlib.frameworks.columnGeneration.pricing.DefaultPricingProblemSolverFactory;
import org.jorlib.frameworks.columnGeneration.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Main class defining the Column Generation procedure. It keeps track of all the data structures. Its solve() method is the core of this class.
 * Assumptions: the Master problem is a minimization problem. The optimal solution with non-fractional variable values has an integer objective value.
 * 
 * U is reserved for columns
 * T is reserved for the model
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 */
public class ColGen<T, U extends AbstractColumn<T,U,V>, V extends AbstractPricingProblem<T,U,V>> {
	
	final Logger logger = LoggerFactory.getLogger(ColGen.class);
	static final Configuration config=Configuration.getConfiguration();

	//Define the problem
	protected final T dataModel;
	
	//Define the master problem
	protected final AbstractMaster<T, V, U, ? extends MasterData> master;
	//Define the pricing problems
	protected final List<V> pricingProblems;
	//Maintain the classes which can be used to solve the pricing problems
	protected final List<Class<? extends PricingProblemSolver<T, U, V>>> solvers;
	//For each solver, we maintain an instance for each pricing problem. This gives a |solvers|x|pricingProblems| array
	protected final List<PricingProblemBundle<T, U, V>> pricingProblemBunddles;
	//Manages parallel execution of pricing problems
	protected final PricingProblemManager<T,U, V> pricingProblemManager;
	
	//Objective value of column generation procedure
	protected double objective; 
	//Colgen is terminated if objective exceeds upperBound. Upperbound is set equal to the best incumbent integer solution
	protected int upperBound=Integer.MAX_VALUE;
	//Lower bound on the objective. If lowerbound > upperBound, this node can be pruned.
	protected double lowerBound=0;
	//Total number of iterations.
	protected int nrOfColGenIterations=0;
	//Total time spent on solving column generation problem
	protected long colGenSolveTime;
	//Total time spent on solving the master problem
	protected long masterSolveTime=0;
	//Total time spent on solving the pricing problem
	protected long pricingSolveTime=0;
	//Total number of columns generated and added to the master problem
	protected int nrGeneratedColumns=0;
	
	/**
	 * Create a new column generation instance
	 * @param dataModel data model
	 * @param master master problem
	 * @param pricingProblems pricing problems
	 * @param solvers pricing problem solvers
	 * @param initSolution initial solution
	 * @param upperBound upper bound on solution. Column generation process is terminated if lower bound exceeds upper bound
	 */
	public ColGen(T dataModel, 
					AbstractMaster<T,V,U, ? extends MasterData> master, 
					List<V> pricingProblems,
					List<Class<? extends PricingProblemSolver<T, U, V>>> solvers,
					List<U> initSolution,
					int upperBound){
		this.dataModel=dataModel;
		this.master=master;
		this.pricingProblems=pricingProblems;
		this.solvers=solvers;
		master.addColumns(initSolution);
		this.upperBound=upperBound;
		
		//Generate the pricing problem instances
		pricingProblemBunddles=new ArrayList<>();
		for(Class<? extends PricingProblemSolver<T, U, V>> solverClass : solvers){
			DefaultPricingProblemSolverFactory<T, U, V> factory=new DefaultPricingProblemSolverFactory<T, U, V>(solverClass, /*solverClass.getName(), */dataModel);
			PricingProblemBundle<T, U, V> bunddle=new PricingProblemBundle<>(solverClass, pricingProblems, factory);
			pricingProblemBunddles.add(bunddle);
		}
		
		//Create a pricing problem manager for parallel execution of the pricing problems
		pricingProblemManager=new PricingProblemManager<T,U, V>(pricingProblems, pricingProblemBunddles);
	}
	
	/**
	 * Create a new column generation instance
	 * @param dataModel data model
	 * @param master master problem
	 * @param pricingProblems pricing problems
	 * @param solvers pricing problem solvers
	 * @param initSolution initial solution
	 * @param upperBound upper bound on solution. Column generation process is terminated if lower bound exceeds upper bound
	 */
	public ColGen(T dataModel, 
			AbstractMaster<T,V,U,? extends MasterData> master, 
			V pricingProblem,
			List<Class<? extends PricingProblemSolver<T, U, V>>> solvers,
			List<U> initSolution,
			int upperBound){
		this(dataModel, master, Arrays.asList(pricingProblem), solvers, initSolution, upperBound);
	}
	
	
	/*public <U extends Column> ColGen(T dataModel, CutHandler cutHandler, PricingSolvers[] pricingAlgorithms, EnumMap<PricingSolvers, List<PricingProblem>> pricingProblems, PricingProblemManager pricingProblemManager, List<U> initSolution, List<Inequality> initialCuts, long timeLimit, int upperBound) throws TimeLimitExceededException{
		this.dataModel=dataModel;
		this.cutHandler=cutHandler;
		master=new MasterImplementation(geoxam, cutHandler);
		master.addCuts(initialCuts);
		if(initSolution != null){
			master.setInitialSolution(initSolution);
			this.upperBound=upperBound;
		}
		this.pricingAlgorithms=pricingAlgorithms;
		this.pricingProblems=pricingProblems;
		this.pricingProblemManager=pricingProblemManager;
		
		this.solve(timeLimit);
	}*/
	
	/**
	 * Solve the Column Generation problem
	 * @param timeLimit Future point in time (ms) by which the procedure should be finished. Should be defined as: System.currentTimeMilis()+<desired runtime>
	 * @throws TimeLimitExceededException Exception is thrown when time limit is exceeded
	 */
	public void solve(long timeLimit) throws TimeLimitExceededException{
		//set time limit pricing problems
		pricingProblemManager.setTimeLimit(timeLimit);
		colGenSolveTime=System.currentTimeMillis();
		
		List<U> newColumns=new ArrayList<>(); //List containing new columns generated by the pricing problem
		boolean foundNewColumns=false; //Identify whether the pricing problem generated new columns
		boolean hasNewCuts=false; //Identify whether the master problem violates any valid inequalities
		do{
			nrOfColGenIterations++;
			hasNewCuts=false;
			
			//Solve the master
			logger.debug("### MASTER "+master.getIterationCount()+" ################################");
			long time=System.currentTimeMillis();
			master.solve(timeLimit);
			objective=master.getObjective();
			masterSolveTime+=(System.currentTimeMillis()-time);
			logger.debug("Objective master: {}",master.getObjective());
			
			//if the objective of the master problem equals 0, we can stop generating columns as 0 is a lower bound on the optimal solution.
			//Alternatively, we can stop when the objective equals the lowerBound. We still need to check for violated inequalities though.
			if(master.getObjective() < config.PRECISION || Math.abs(objective-lowerBound)<config.PRECISION){
				//Check whether there are cuts. Otherwise potentially an infeasible integer solution (e.g. TSP solution with subtours) might be returned.
				if(config.CUTSENABLED && master.hasNewCuts()){  
					hasNewCuts=true;
					nrOfColGenIterations--;
					logger.debug("Colgen quick return canceled: found valid inequalities. Repeating solve");
					continue;
				}else
					break;
			}else if(Math.ceil(lowerBound) > upperBound){ //lower bound exceeds best feasible integer solution (upper bound) -> terminate
				break;
			}
			
			//Solve the pricing problem
			logger.debug("### PRICING ################################");
			foundNewColumns=false;
			time=System.currentTimeMillis();
			
			//Update data in pricing problems
			for(V pricingProblem : pricingProblems){
				master.initializePricingProblem(pricingProblem);
			}
			
			//Solve pricing problems in the order of the pricing algorithms
			for(int solver=0; solver<solvers.size(); solver++){
				newColumns=pricingProblemManager.solvePricingProblems(solver);
				foundNewColumns=!newColumns.isEmpty();
				
				//Calculate a lower bound on the optimal solution of the master problem
				this.lowerBound=Math.max(lowerBound,this.calculateLowerBound(solver));
				
				//Stop when we found new columns
				if(foundNewColumns){
					break;
				}
			}
			
			
			pricingSolveTime+=(System.currentTimeMillis()-time);
			nrGeneratedColumns+=newColumns.size();
			logger.debug("CG Adding columns. Found new columns: {}",foundNewColumns);
			//Add columns to the master problem
			if(foundNewColumns){
				for(U column : newColumns){
					master.addColumn(column);
					column.associatedPricingProblem.addColumn(column);
					
				}
			}
			
			//Check whether we are still within the timeLimit
			if(System.currentTimeMillis() >= timeLimit){
				this.close();
				throw new TimeLimitExceededException();
			}
			//Check for cuts. This can only be done if the master problem hasn't changed (no columns can be added).
			if(config.CUTSENABLED && !foundNewColumns && !thisNodeCanBePruned()){
				time=System.currentTimeMillis();
				hasNewCuts=master.hasNewCuts();
				masterSolveTime+=(System.currentTimeMillis()-time); //Generating cuts is considered part of the master problem
			}
			
		}while(foundNewColumns || hasNewCuts);
		colGenSolveTime=System.currentTimeMillis()-colGenSolveTime;
		
		logger.debug("Finished colGen loop");
		logger.debug("Objective: {}",objective);
		logger.debug("Number of iterations: {}",nrOfColGenIterations);
	}
	
	/**
	 * Compute lower bound on the optimal objective value attainable by the the current master problem. The bound is based on both dual variables from the master,
	 * as well as the optimal pricing problem solutions.
	 * The parameter specifies which solver was last invoked to solve the pricing problems.
	 * Returns the best lower bound for the current master.
	 * NOTE: This method is not implemented by default.
	 * 
	 * @param solverID id of last invoked solver: solvers.get(solverID)
	 */
	protected double calculateLowerBound(int solverID){
		//This method is not implemented as it is problem dependent. Override this method.
		//The following methods are at your disposal (see documentation):
		//double master.getLowerboundComponent()
		//double[] pricingProblemManager.getUpperBound()
		return 0;
	}
	
	/**
	 * @return Returns the objective value of the column generation procedure
	 */
	public double getObjective(){
		return objective;
	}
	/**
	 * Returns a lower bound on the objective of the column generation procedure. When the column generation procedure is solved to optimality,
	 * getObjective() and getLowerBound() should return the same value. However, if the column generation procedure terminates for example due to
	 * a time limit exceeded exception, there may be a gap in between those values.
	 * 
	 * @return Returns a lower bound on the column generation objective
	 */
	public double getLowerBound(){
		return lowerBound;
	}
	/**
	 * @return Returns the number of column generation iterations
	 */
	public int getNumberOfIterations(){
		return nrOfColGenIterations;
	}
	/**
	 * @return Returns how much time it took to solve the column generation problem. This time equals:
	 * getMasterSolveTime()+getPricingSolveTime()+(small amount of overhead).
	 */
	public long getRuntime(){
		return colGenSolveTime;
	}
	/**
	 * @return Returns how much time was spent on solving the master problem
	 */
	public long getMasterSolveTime(){
		return masterSolveTime;
	}
	/**
	 * @return Returns how much time was spent on solving the pricing problems
	 */
	public long getPricingSolveTime(){
		return pricingSolveTime;
	}
	/**
	 * @return Returns how many columns have been generated in total
	 */
	public int getNrGeneratedColumns(){
		return nrGeneratedColumns;
	}
	/**
	 * @return Returns the solution maintained by the master problem
	 */
	public List<U> getSolution(){
		return master.getSolution();
	}
	
	/**
	 * @return Returns all cuts generated for the master problem
	 */
	public List<Inequality> getCuts(){
		return master.getCuts();
	}
	
	/**
	 * Returns true if the lower bound exceeds the upper bound
	 */
	private boolean thisNodeCanBePruned(){
		return Math.ceil(lowerBound) > upperBound;
	}
	
	/**
	 * Destroy the master problem
	 */
	public void closeMaster(){
		master.close();
	}
	
	/**
	 * Destroy both the master problem and pricing problems
	 */
	public void close(){
		master.close();
		pricingProblemManager.close();
	}
}
