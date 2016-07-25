/* ==========================================
 * jORLib : a free Java OR library
 * ==========================================
 *
 * Project Info:  https://github.com/jkinable/jorlib
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
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

import java.util.*;

import org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling.*;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.master.AbstractMaster;
import org.jorlib.frameworks.columnGeneration.master.MasterData;
import org.jorlib.frameworks.columnGeneration.master.OptimizationSense;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.AbstractInequality;
import org.jorlib.frameworks.columnGeneration.model.ModelInterface;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblem;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemBundle;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemManager;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblemSolver;
import org.jorlib.frameworks.columnGeneration.pricing.DefaultPricingProblemSolverFactory;
import org.jorlib.frameworks.columnGeneration.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Main class defining the Column Generation procedure. It keeps track of all the data structures. Its {@link #solve(long timeLimit) solve} method is the core of this class.
 * Assumptions: the Master problem is a minimization problem. The optimal solution with non-fractional variable values has an integer objectiveMasterProblem value.
 *
 * @param <T> The data model
 * @param <U> Type of column
 * @param <V> Type of pricing problem
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 */
public class ColGen<T extends ModelInterface, U extends AbstractColumn<T, V>, V extends AbstractPricingProblem<T>> {

	/** Logger for this class **/
	protected final Logger logger = LoggerFactory.getLogger(ColGen.class);
	/** Configuration file for this class **/
	protected static final Configuration config=Configuration.getConfiguration();

	/** Data model **/
	protected final T dataModel;
	
	/** Master problem **/
	protected final AbstractMaster<T, U, V, ? extends MasterData> master;
	/** Pricing problems **/
	protected final List<V> pricingProblems;
	/** Solvers for the pricing problems **/
	protected final List<Class<? extends AbstractPricingProblemSolver<T, U, V>>> solvers;
	/** Manages parallel execution of pricing problems **/
	protected final PricingProblemManager<T,U, V> pricingProblemManager;
	/** Helper class which notifies {@link CGListener} **/
	protected final CGNotifier notifier;

	/** Defines whether the master problem is a minimization or a maximization problem **/
	protected final OptimizationSense optimizationSenseMaster;
	/** Objective value of column generation procedure **/
	protected double objectiveMasterProblem;
	/** The Colgen procedure is terminated if the bound on the best attainable solution to the master problem is worse than the
	 * cutoffValue. If the master is a minimization problem, the Colgen procedure is terminated if {@code ceil(boundOnMasterObjective) >= cutoffValue}. If the master is a maximization problem, the Colgen procedure is terminated if {@code floor(boundOnMasterObjective) <= cutoffValue}.
	 **/
	protected int cutoffValue;
	/** Bound on the best attainable objective value from the master problem. Assuming that the master is a minimization problem, the Colgen procedure is terminated if {@code ceil(boundOnMasterObjective) >= cutoffValue}.**/
	protected double boundOnMasterObjective =0;
	/** Total number of column generation iterations. **/
	protected int nrOfColGenIterations=0;
	/**Total time spent on the column generation procedure**/
	protected long colGenSolveTime;
	/** Total time spent on solving the master problem **/
	protected long masterSolveTime=0;
	/** Total time spent on solving the pricing problem **/
	protected long pricingSolveTime=0;
	/** Total number of columns generated and added to the master problem **/
	protected int nrGeneratedColumns=0;
	
	/**
	 * Create a new column generation instance
	 * @param dataModel data model
	 * @param master master problem
	 * @param pricingProblems pricing problems
	 * @param solvers pricing problem solvers
	 * @param initSolution initial solution
	 * @param cutoffValue cutoff Value. If the master is a minimization problem, the Colgen procedure is terminated if {@code ceil(boundOnMasterObjective) >= cutoffValue}. If the master is a maximization problem, the Colgen procedure is terminated if {@code floor(boundOnMasterObjective) <= cutoffValue}.
	 * @param boundOnMasterObjective Bound on the best attainable objective value from the master problem. Assuming that the master is a minimization problem, the Colgen procedure is terminated if {@code ceil(boundOnMasterObjective) >= cutoffValue}.
	 */
	public ColGen(T dataModel, 
					AbstractMaster<T, U, V, ? extends MasterData> master,
					List<V> pricingProblems,
					List<Class<? extends AbstractPricingProblemSolver<T, U, V>>> solvers,
					List<U> initSolution,
					int cutoffValue,
				  	double boundOnMasterObjective){
		this.dataModel=dataModel;
		this.master=master;
		optimizationSenseMaster=master.getOptimizationSense();
		this.pricingProblems=pricingProblems;
		this.solvers=solvers;
		this.cutoffValue = cutoffValue;
		this.boundOnMasterObjective=boundOnMasterObjective;
		master.addColumns(initSolution);

		//Generate the pricing problem instances
		Map<Class<? extends AbstractPricingProblemSolver<T, U, V>>, PricingProblemBundle<T, U, V>> pricingProblemBundles=new HashMap<>();
		for(Class<? extends AbstractPricingProblemSolver<T, U, V>> solverClass : solvers){
			DefaultPricingProblemSolverFactory<T, U, V> factory=new DefaultPricingProblemSolverFactory<>(solverClass, dataModel);
			PricingProblemBundle<T, U, V> bundle=new PricingProblemBundle<>(solverClass, pricingProblems, factory);
			pricingProblemBundles.put(solverClass, bundle);
		}
		
		//Create a pricing problem manager for parallel execution of the pricing problems
		pricingProblemManager=new PricingProblemManager<>(pricingProblems, pricingProblemBundles);
		//Create a new notifier
		notifier=new CGNotifier();
	}
	
	/**
	 * Create a new column generation instance
	 * @param dataModel data model
	 * @param master master problem
	 * @param pricingProblem pricing problem
	 * @param solvers pricing problem solvers
	 * @param initSolution initial solution
	 * @param cutoffValue cutoff Value. If the master is a minimization problem, the Colgen procedure is terminated if {@code ceil(boundOnMasterObjective) >= cutoffValue}. If the master is a maximization problem, the Colgen procedure is terminated if {@code floor(boundOnMasterObjective) <= cutoffValue}.
	 * @param boundOnMasterObjective Bound on the best attainable objective value from the master problem. Assuming that the master is a minimization problem, the Colgen procedure is terminated if {@code ceil(boundOnMasterObjective) >= cutoffValue}.
	 */
	public ColGen(T dataModel, 
			AbstractMaster<T, U, V, ? extends MasterData> master,
			V pricingProblem,
			List<Class<? extends AbstractPricingProblemSolver<T, U, V>>> solvers,
			List<U> initSolution,
			int cutoffValue,
			double boundOnMasterObjective){
		this(dataModel, master, Collections.singletonList(pricingProblem), solvers, initSolution, cutoffValue, boundOnMasterObjective);
	}

	/**
	 * Create a new column generation instance
	 * @param dataModel data model
	 * @param master master problem
	 * @param pricingProblems pricing problems
	 * @param solvers pricing problem solvers
	 * @param pricingProblemManager pricing problem manager
	 * @param initSolution initial solution
	 * @param cutoffValue cutoff Value. If the master is a minimization problem, the Colgen procedure is terminated if {@code ceil(boundOnMasterObjective) >= cutoffValue}. If the master is a maximization problem, the Colgen procedure is terminated if {@code floor(boundOnMasterObjective) <= cutoffValue}.
	 * @param boundOnMasterObjective Bound on the best attainable objective value from the master problem. Assuming that the master is a minimization problem, the Colgen procedure is terminated if {@code ceil(boundOnMasterObjective) >= cutoffValue}.
	 */
	public ColGen(T dataModel, 
			AbstractMaster<T, U, V, ? extends MasterData> master,
			List<V> pricingProblems,
			List<Class<? extends AbstractPricingProblemSolver<T, U, V>>> solvers,
			PricingProblemManager<T,U, V> pricingProblemManager,
			List<U> initSolution,
			int cutoffValue,
			double boundOnMasterObjective){
		this.dataModel=dataModel;
		this.master=master;
		optimizationSenseMaster=master.getOptimizationSense();
		this.pricingProblems=pricingProblems;
		this.solvers=solvers;
		this.pricingProblemManager=pricingProblemManager;
		master.addColumns(initSolution);
		this.cutoffValue = cutoffValue;
		this.boundOnMasterObjective=boundOnMasterObjective;

		//Create a new notifier
		notifier=new CGNotifier();
	}
	
	/**
	 * Solve the Column Generation problem. First the master problem is solved. Next the pricing problems(s) is (are) solved. To solve the pricing problems, the pricing
	 * solvers are invoked one by one in a hierarchical fashion. First the first solver is invoked to solve the pricing problems. Any new columns generated are immediately returned.
	 * If it fails to find columns, the next solver is invoked and so on. If the pricing problem discovers new columns, they are added to the master problem and the method continues
	 * with the next column generation iteration.<br>
	 * If no new columns are found, the method checks for violated inequalities. If there are violated inequalities, they are added to the master problem and the method continues with the
	 * next column generation iteration.<br>
	 * The solve procedure terminates under any of the following conditions:
	 * <ol>
	 * <li>the solver could not identify new columns</li>
	 * <li>Time limit exceeded</li>
	 * <li>The bound on the best attainable solution to the master problem is worse than the cutoff value. Assuming that the master is a minimization problem, the Colgen procedure is terminated if {@code ceil(boundOnMasterObjective) >= cutoffValue}</li>
	 * <li>The solution to the master problem is provable optimal, i.e the bound on the best attainable solution to the master problem equals the solution of the master problem.</li>
	 * </ol>
	 * @param timeLimit Future point in time (ms) by which the procedure should be finished. Should be defined as: {@code System.currentTimeMilis()+<desired runtime>}
	 * @throws TimeLimitExceededException Exception is thrown when time limit is exceeded
	 */
	public void solve(long timeLimit) throws TimeLimitExceededException{
		//set time limit pricing problems
		pricingProblemManager.setTimeLimit(timeLimit);
		colGenSolveTime=System.currentTimeMillis();
		
		boolean foundNewColumns=false; //Identify whether the pricing problem generated new columns
		boolean hasNewCuts; //Identify whether the master problem violates any valid inequalities
		notifier.fireStartCGEvent();
		do{
			nrOfColGenIterations++;
			hasNewCuts=false;
			
			//Solve the master
			this.invokeMaster(timeLimit);

			//We can stop when the optimality gap is closed. We still need to check for violated inequalities though.
			if(Math.abs(objectiveMasterProblem - boundOnMasterObjective)<config.PRECISION){
				//Check whether there are inequalities. Otherwise potentially an infeasible integer solution (e.g. TSP solution with subtours) might be returned.
				if(config.CUTSENABLED){
					long time=System.currentTimeMillis();
					hasNewCuts=master.hasNewCuts();
					masterSolveTime+=(System.currentTimeMillis()-time); //Generating inequalities is considered part of the master problem
					if(hasNewCuts)
						continue;
					else
						break;
				}else
					break;
			}
			
			//Solve the pricing problem and possibly update the bound on the master problem objective
			List<U> newColumns=this.invokePricingProblems(timeLimit); //List containing new columns generated by the pricing problem
			foundNewColumns=!newColumns.isEmpty();

			//Check whether the boundOnMasterObjective exceeds the cutoff value
			if(boundOnMasterExceedsCutoffValue())
				break;
			else if(System.currentTimeMillis() >= timeLimit){ //Check whether we are still within the timeLimit
				notifier.fireTimeLimitExceededEvent();
				throw new TimeLimitExceededException();
			}else if(config.CUTSENABLED && !foundNewColumns){ //Check for inequalities. This can only be done if the master problem hasn't changed (no columns can be added).
				long time=System.currentTimeMillis();
				hasNewCuts=master.hasNewCuts();
				masterSolveTime+=(System.currentTimeMillis()-time); //Generating inequalities is considered part of the master problem
			}
			
		}while(foundNewColumns || hasNewCuts);
		this.boundOnMasterObjective = (optimizationSenseMaster == OptimizationSense.MINIMIZE ? Math.max(this.boundOnMasterObjective, this.objectiveMasterProblem) : Math.min(this.boundOnMasterObjective, this.objectiveMasterProblem)); //When solved to optimality, the bound on the master problem objective equals the objective value.
		colGenSolveTime=System.currentTimeMillis()-colGenSolveTime;
		notifier.fireFinishCGEvent();
	}

	/**
	 * Invokes the solve method of the Master Problem, fires corresponding events and queries the results.
	 * @param timeLimit Future point in time by which the Master Problem must be finished
	 * @throws TimeLimitExceededException TimeLimitExceededException
	 */
	protected void invokeMaster(long timeLimit) throws TimeLimitExceededException {
		notifier.fireStartMasterEvent();
		long time=System.currentTimeMillis();
		master.solve(timeLimit);
		objectiveMasterProblem =master.getObjective();
		masterSolveTime+=(System.currentTimeMillis()-time);
		notifier.fireFinishMasterEvent();
	}

	/**
	 * Invokes the solve methods of the algorithms which solve the Pricing Problem. In addition, after solving the Pricing Problems
	 * and before any new columns are added to the Master Problem, this method invokes the {@link #calculateBoundOnMasterObjective(Class solver) calculateBoundOnMasterObjective} method.
	 * @param timeLimit Future point in time by which the Pricing Problem must be finished
	 * @return list of new columns which have to be added to the Master Problem, or an empty list if no columns could be identified
	 * @throws TimeLimitExceededException TimeLimitExceededException
	 */
	protected List<U> invokePricingProblems(long timeLimit) throws TimeLimitExceededException {
		//Solve the pricing problem
		List<U> newColumns=new ArrayList<>();
		long time=System.currentTimeMillis();

		//Update data in pricing problems
		for(V pricingProblem : pricingProblems){
			master.initializePricingProblem(pricingProblem);
		}

		//Solve pricing problems in the order of the pricing algorithms
		notifier.fireStartPricingEvent();
		pricingProblemManager.setTimeLimit(timeLimit);
		for(Class<? extends AbstractPricingProblemSolver<T, U, V>> solver : solvers){
			newColumns=pricingProblemManager.solvePricingProblems(solver);

			//Calculate a bound on the optimal solution of the master problem
			this.boundOnMasterObjective =(optimizationSenseMaster == OptimizationSense.MINIMIZE ? Math.max(boundOnMasterObjective,this.calculateBoundOnMasterObjective(solver)) : Math.min(boundOnMasterObjective,this.calculateBoundOnMasterObjective(solver)));

			//Stop when we found new columns
			if(!newColumns.isEmpty()){
				break;
			}
		}
		notifier.fireFinishPricingEvent(newColumns);

		pricingSolveTime+=(System.currentTimeMillis()-time);
		nrGeneratedColumns+=newColumns.size();
		//Add columns to the master problem
		if(!newColumns.isEmpty()){
			for(U column : newColumns){
				master.addColumn(column);
			}
		}
		return newColumns;
	}

	/**
	 * Compute bound on the optimal objective value attainable by the the current master problem. The bound may be based on both information from the master,
	 * as well as information from the pricing problem solutions.<br>
	 * The parameter specifies which solver was last invoked to solve the pricing problems. This method is invoked immediately after solving the pricing problem.
	 * This method is not implemented as it is problem dependent. Override this method. The following methods are at your disposal (see documentation):
	 * <ul>
	 * <li>{@link AbstractMaster#getBoundComponent()} for the master problem</li>
	 * <li>{@link PricingProblemManager#getBoundsOnPricingProblems(Class)}  method for the pricing problems</li>
	 * </ul>
	 * NOTE: This method is not implemented by default.
	 * NOTE2: When calling this method, it is guaranteed that the master problem has not been changed (no columns or inequalities are added) since the last time its
	 * {@link #solve(long timeLimit) solve} method was invoked!
	 * 
	 * @param solver solver which was used to solve the pricing problem during the last invocation
	 * @return bound on the optimal master problem solution
	 */
	protected double calculateBoundOnMasterObjective(Class<? extends AbstractPricingProblemSolver<T, U, V>> solver){
		//This method is not implemented as it is problem dependent. Override this method.
		//The following methods are at your disposal (see documentation):
		//double master.getLowerboundComponent()
		//double[] pricingProblemManager.getUpperBound(solver)
		return (optimizationSenseMaster == OptimizationSense.MINIMIZE ? -Double.MAX_VALUE : Double.MAX_VALUE);
	}
	
	/**
	 * Returns the objective value of the column generation procedure
	 * @return Returns the objective value of the column generation procedure
	 */
	public double getObjective(){
		return objectiveMasterProblem;
	}

	/**
	 * Returns a bound on the objective of the column generation procedure, i.e the strongest available bound on the optimal solution to the master problem.
	 * When the column generation procedure is solved to optimality, getObjective() and getBound() should return the same value. However, if the column generation procedure terminates for example due to
	 * a time limit exceeded exception, there may be a gap in between those values.
	 * 
	 * @return strongest available bound on the objective of the column generation procedure, i.e the strongest available bound on the optimal solution to the master problem.
	 */
	public double getBound(){
		return boundOnMasterObjective;
	}

	/**
	 * @return Returns the number of column generation iterations performed
	 */
	public int getNumberOfIterations(){
		return nrOfColGenIterations;
	}

	/**
	 * Returns the total runtime
	 * @return Returns how much time it took to solve the column generation problem. This time equals:
	 * {@link #getMasterSolveTime()}+{@link #getPricingSolveTime()}+(small amount of overhead).
	 */
	public long getRuntime(){
		return colGenSolveTime;
	}

	/**
	 * Returns how much time was spent on solving the master problem
	 * @return Returns how much time was spent on solving the master problem
	 */
	public long getMasterSolveTime(){
		return masterSolveTime;
	}

	/**
	 * Returns how much time was spent on solving the pricing problems
	 * @return Returns how much time was spent on solving the pricing problems
	 */
	public long getPricingSolveTime(){
		return pricingSolveTime;
	}

	/**
	 * Returns how many columns have been generated in total
	 * @return Returns how many columns have been generated in total
	 */
	public int getNrGeneratedColumns(){
		return nrGeneratedColumns;
	}

	/**
	 * Returns the solution maintained by the master problem
	 * @return Returns the solution maintained by the master problem
	 */
	public List<U> getSolution(){
		return master.getSolution();
	}
	
	/**
	 * Returns all inequalities generated for the master problem
	 * @return Returns all inequalities generated for the master problem
	 */
	public List<AbstractInequality> getCuts(){
		return master.getCuts();
	}
	
	/**
	 * Returns true if the bound on the master problem is worse than the cutoff value. More precisely, if the master problem is a minimization problem, this method
	 * returns true if {@code ceil(boundOnMasterObjective) >= cutoffValue}.  Alternatively, if the master problem is a maximization problem, this method returns true if
	 * {@code floor(boundOnMasterObjective) <= cutoffValue}.
	 * @return true if the lower bound exceeds the upper bound
	 */
	protected boolean boundOnMasterExceedsCutoffValue(){
		if(optimizationSenseMaster == OptimizationSense.MINIMIZE)
			return Math.ceil(boundOnMasterObjective-config.PRECISION) >= cutoffValue;
		else
			return Math.floor(boundOnMasterObjective+config.PRECISION) <= cutoffValue;
	}
	
	/**
	 * Destroy both the master problem and pricing problems
	 */
	public void close(){
		master.close();
		pricingProblemManager.close();
	}

	/**
	 * Adds a CGlistener
	 * @param listener listener
	 */
	public void addCGEventListener(CGListener listener) {
		notifier.addListener(listener);
	}

	/**
	 * Removes a listener
	 * @param listener listener
	 */
	public void removeCGEventListener(CGListener listener) {
		notifier.removeListener(listener);
	}

	/**
	 * Inner Class which notifies CGListeners
	 */
	protected class CGNotifier {
		/**
		 * Listeners
		 */
		private Set<CGListener> listeners;

		/**
		 * Creates a new BAPNotifier
		 */
		public CGNotifier() {
			listeners = new LinkedHashSet<>();
		}

		/**
		 * Adds a listener
		 * @param listener listener
		 */
		public void addListener(CGListener listener) {
			this.listeners.add(listener);
		}

		/**
		 * Removes a listener
		 * @param listener listener
		 */
		public void removeListener(CGListener listener) {
			this.listeners.remove(listener);
		}

		/**
		 * Fires a StartEvent to indicate the start of the column generation procedure
		 */
		public void fireStartCGEvent(){
			StartEvent startEvent =null;
			for(CGListener listener : listeners){
				if(startEvent ==null)
					startEvent =new StartEvent(ColGen.this, dataModel.getName(), cutoffValue);
				listener.startCG(startEvent);
			}
		}

		/**
		 * Fires a FinishEvent to indicate that the column generation procedure is finished
		 */
		public void fireFinishCGEvent(){
			FinishEvent finishEvent =null;
			for(CGListener listener : listeners){
				if(finishEvent ==null)
					finishEvent =new FinishEvent(ColGen.this);
				listener.finishCG(finishEvent);
			}
		}

		/**
		 * Fires a StartMasterEvent to indicate the start of master solve procedure
		 */
		public void fireStartMasterEvent(){
			StartMasterEvent startMasterEvent =null;
			for(CGListener listener : listeners){
				if(startMasterEvent ==null)
					startMasterEvent =new StartMasterEvent(ColGen.this, nrOfColGenIterations);
				listener.startMaster(startMasterEvent);
			}
		}

		/**
		 * Fires a FinishMasterEvent to indicate that the master problem has been solved
		 */
		public void fireFinishMasterEvent(){
			FinishMasterEvent finishMasterEvent =null;
			for(CGListener listener : listeners){
				if(finishMasterEvent ==null)
					finishMasterEvent =new FinishMasterEvent(ColGen.this, nrOfColGenIterations, objectiveMasterProblem, cutoffValue, boundOnMasterObjective);
				listener.finishMaster(finishMasterEvent);
			}
		}

		/**
		 * Fires a StartPricingEvent to indicate the start of pricing solve procedure
		 */
		public void fireStartPricingEvent(){
			StartPricingEvent startPricingEvent =null;
			for(CGListener listener : listeners){
				if(startPricingEvent ==null)
					startPricingEvent =new StartPricingEvent(ColGen.this, nrOfColGenIterations);
				listener.startPricing(startPricingEvent);
			}
		}

		/**
		 * Fires a FinishPricingEvent to indicate that the pricing problem has been solved
		 * @param newColumns List of columns which have been generated by the pricing problems
		 */
		public void fireFinishPricingEvent(List<U> newColumns){
			FinishPricingEvent finishPricingEvent =null;
			for(CGListener listener : listeners){
				if(finishPricingEvent ==null)
					finishPricingEvent =new FinishPricingEvent(ColGen.this, nrOfColGenIterations, Collections.unmodifiableList(newColumns), objectiveMasterProblem, cutoffValue, boundOnMasterObjective);
				listener.finishPricing(finishPricingEvent);
			}
		}

		/**
		 * Fires a TimeLimitExceededEvent
		 */
		public  void fireTimeLimitExceededEvent(){
			TimeLimitExceededEvent timeLimitExceededEvent =null;
			for(CGListener listener : listeners){
				if(timeLimitExceededEvent ==null)
					timeLimitExceededEvent =new TimeLimitExceededEvent(ColGen.this);
				listener.timeLimitExceeded(timeLimitExceededEvent);
			}
		}
	}
}
