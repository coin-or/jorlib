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

import java.util.*;

import org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling.*;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.master.AbstractMaster;
import org.jorlib.frameworks.columnGeneration.master.MasterData;
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
 * Main class defining the Column Generation procedure. It keeps track of all the data structures. Its solve() method is the core of this class.
 * Assumptions: the Master problem is a minimization problem. The optimal solution with non-fractional variable values has an integer objective value.
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
	final Logger logger = LoggerFactory.getLogger(ColGen.class);
	/** Configuration file for this class **/
	static final Configuration config=Configuration.getConfiguration();

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
	/** Helper class which notifies CGListeners **/
	protected final CGNotifier notifier;
	
	/** Objective value of column generation procedure **/
	protected double objective;
	/** The Colgen procedure is terminated if objective exceeds upperBound. The Upperbound is set equal to the best incumbent INTEGER solution. Do NOT confuse with the objective field
	 * which is a DOUBLE **/
	protected int upperBound=Integer.MAX_VALUE;
	/** Lower bound on the objective value. If {@code lowerbound > upperBound}, this node can be pruned.**/
	protected double lowerBound=0;
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
	 * @param upperBound upper bound on solution. Column generation process is terminated if lower bound exceeds upper bound
	 */
	public ColGen(T dataModel, 
					AbstractMaster<T, U, V, ? extends MasterData> master,
					List<V> pricingProblems,
					List<Class<? extends AbstractPricingProblemSolver<T, U, V>>> solvers,
					List<U> initSolution,
					int upperBound){
		this.dataModel=dataModel;
		this.master=master;
		this.pricingProblems=pricingProblems;
		this.solvers=solvers;
		this.upperBound=upperBound;
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
	 * @param upperBound upper bound on solution. Column generation process is terminated if lower bound exceeds upper bound
	 */
	public ColGen(T dataModel, 
			AbstractMaster<T, U, V, ? extends MasterData> master,
			V pricingProblem,
			List<Class<? extends AbstractPricingProblemSolver<T, U, V>>> solvers,
			List<U> initSolution,
			int upperBound){
		this(dataModel, master, Collections.singletonList(pricingProblem), solvers, initSolution, upperBound);
	}

	/**
	 * Create a new column generation instance
	 * @param dataModel data model
	 * @param master master problem
	 * @param pricingProblems pricing problems
	 * @param solvers pricing problem solvers
	 * @param pricingProblemManager pricing problem manager
	 * @param initSolution initial solution
	 * @param upperBound upper bound on solution. Column generation process is terminated if lower bound exceeds upper bound
	 */
	public ColGen(T dataModel, 
			AbstractMaster<T, U, V, ? extends MasterData> master,
			List<V> pricingProblems,
			List<Class<? extends AbstractPricingProblemSolver<T, U, V>>> solvers,
			PricingProblemManager<T,U, V> pricingProblemManager,
			List<U> initSolution,
			int upperBound){
		this.dataModel=dataModel;
		this.master=master;
		this.pricingProblems=pricingProblems;
		this.solvers=solvers;
		this.pricingProblemManager=pricingProblemManager;
		master.addColumns(initSolution);
		this.upperBound=upperBound;

		//Create a new notifier
		notifier=new CGNotifier();
	}
	
	/**
	 * Solve the Column Generation problem. First the master problem is solved. Next the pricing problems(s) is (are) solved. To solve the pricing problems, the pricing
	 * solvers are invoked one by one in a hierarchical fashion. First the first solver is invoked to solve the pricing problems. Any new columns generated are immediately returned.
	 * If it fails to find columns, the next solver is invoked and so on. If the pricing problem discovers new columns, they are added to the master problem and the method continues
	 * with the next column generation iteration.
	 * If no new columns are found, the method checks for violated inequalities. If there are violated inequalities, they are added to the master problem and the method continues with the
	 * next column generation iteration.
	 * The solve procedure terminates under any of the following conditions:
	 * 1. the solvers could not identify additional columns
	 * 2. Time limit exceeded
	 * 3. The lower bound (rounded up) exceeds the upper bound (best integer solution provided in the Constructor).
	 * 4. The objective of the master problem is zero (zero is assumed to be a natural lower bound)
	 * 5. The lower bound exceeds the objective of the master problem.
	 * @param timeLimit Future point in time (ms) by which the procedure should be finished. Should be defined as: {@code System.currentTimeMilis()+<desired runtime>}
	 * @throws TimeLimitExceededException Exception is thrown when time limit is exceeded
	 */
	public void solve(long timeLimit) throws TimeLimitExceededException{
		//set time limit pricing problems
		pricingProblemManager.setTimeLimit(timeLimit);
		colGenSolveTime=System.currentTimeMillis();
		
		List<U> newColumns=new ArrayList<>(); //List containing new columns generated by the pricing problem
		boolean foundNewColumns=false; //Identify whether the pricing problem generated new columns
		boolean hasNewCuts; //Identify whether the master problem violates any valid inequalities
		notifier.fireStartCGEvent();
		do{
			nrOfColGenIterations++;
			hasNewCuts=false;
			
			//Solve the master
			notifier.fireStartMasterEvent();
			long time=System.currentTimeMillis();
			master.solve(timeLimit);
			objective=master.getObjective();
			masterSolveTime+=(System.currentTimeMillis()-time);
			notifier.fireFinishMasterEvent();

			//if the objective of the master problem equals 0, we can stop generating columns as 0 is a lower bound on the optimal solution.
			//Alternatively, we can stop when the objective equals the lowerBound. We still need to check for violated inequalities though.
			if(objective < config.PRECISION || Math.abs(objective-lowerBound)<config.PRECISION){
				//Check whether there are cuts. Otherwise potentially an infeasible integer solution (e.g. TSP solution with subtours) might be returned.
				if(config.CUTSENABLED && master.hasNewCuts()){  
					hasNewCuts=true;
					continue;
				}else
					break;
			}else if(Math.ceil(lowerBound) > upperBound){ //lower bound exceeds best feasible integer solution (upper bound) -> terminate
				break;
			}
			
			//Solve the pricing problem
			foundNewColumns=false;
			time=System.currentTimeMillis();
			
			//Update data in pricing problems
			for(V pricingProblem : pricingProblems){
				master.initializePricingProblem(pricingProblem);
			}
			
			//Solve pricing problems in the order of the pricing algorithms
			notifier.fireStartPricingEvent();
			for(Class<? extends AbstractPricingProblemSolver<T, U, V>> solver : solvers){
				newColumns=pricingProblemManager.solvePricingProblems(solver);
				foundNewColumns=!newColumns.isEmpty();
				
				//Calculate a lower bound on the optimal solution of the master problem
				this.lowerBound=Math.max(lowerBound,this.calculateLowerBound(solver));
				
				//Stop when we found new columns
				if(foundNewColumns){
					break;
				}
			}
			notifier.fireFinishPricingEvent(newColumns);
			
			pricingSolveTime+=(System.currentTimeMillis()-time);
			nrGeneratedColumns+=newColumns.size();
			//Add columns to the master problem
			if(foundNewColumns){
				for(U column : newColumns){
					master.addColumn(column);
				}
			}
			
			//Check whether we are still within the timeLimit
			if(System.currentTimeMillis() >= timeLimit){
				notifier.fireTimeLimitExceededEvent();
				throw new TimeLimitExceededException();
			}
			//Check for cuts. This can only be done if the master problem hasn't changed (no columns can be added).
			if(config.CUTSENABLED && !foundNewColumns && !thisNodeCanBePruned()){
				time=System.currentTimeMillis();
				hasNewCuts=master.hasNewCuts();
				masterSolveTime+=(System.currentTimeMillis()-time); //Generating cuts is considered part of the master problem
			}
			
		}while(foundNewColumns || hasNewCuts);
		this.lowerBound=this.objective; //When solved to optimality, the lower bound equals the objective
		colGenSolveTime=System.currentTimeMillis()-colGenSolveTime;
		notifier.fireFinishCGEvent();
	}
	
	/**
	 * Compute lower bound on the optimal objective value attainable by the the current master problem. The bound is based on both dual variables from the master,
	 * as well as the optimal pricing problem solutions.
	 * The parameter specifies which solver was last invoked to solve the pricing problems. This method is invoked immediately after solving the pricing problem.
	 * Returns the best lower bound for the current master.
	 * NOTE: This method is not implemented by default.
	 * NOTE2: When calling this method, it is guaranteed that the master problem has not been changed (no columns or inequalities are added) since the last time its
	 * solve() method was invoked!
	 * 
	 * @param solver solver which was used to solve the pricing problem during the last invocation
	 * @return lower bound on the optimal master problem solution
	 */
	protected double calculateLowerBound(Class<? extends AbstractPricingProblemSolver<T, U, V>> solver){
		//This method is not implemented as it is problem dependent. Override this method.
		//The following methods are at your disposal (see documentation):
		//double master.getLowerboundComponent()
		//double[] pricingProblemManager.getUpperBound(solver)
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
	 * @return Returns the number of column generation iterations performed
	 */
	public int getNumberOfIterations(){
		return nrOfColGenIterations;
	}

	/**
	 * Returns the total runtime
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
	public List<AbstractInequality> getCuts(){
		return master.getCuts();
	}
	
	/**
	 * Returns true if the lower bound exceeds the upper bound
	 * @return true if the lower bound exceeds the upper bound
	 */
	protected boolean thisNodeCanBePruned(){
		return Math.ceil(lowerBound) > upperBound;
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
		protected void fireStartCGEvent(){
			StartEvent startEvent =null;
			for(CGListener listener : listeners){
				if(startEvent ==null)
					startEvent =new StartEvent(ColGen.this, dataModel.getName(), upperBound);
				listener.startCG(startEvent);
			}
		}

		/**
		 * Fires a FinishEvent to indicate that the column generation procedure is finished
		 */
		protected void fireFinishCGEvent(){
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
		protected void fireStartMasterEvent(){
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
		protected void fireFinishMasterEvent(){
			FinishMasterEvent finishMasterEvent =null;
			for(CGListener listener : listeners){
				if(finishMasterEvent ==null)
					finishMasterEvent =new FinishMasterEvent(ColGen.this, nrOfColGenIterations, objective, upperBound, lowerBound);
				listener.finishMaster(finishMasterEvent);
			}
		}

		/**
		 * Fires a StartPricingEvent to indicate the start of pricing solve procedure
		 */
		protected void fireStartPricingEvent(){
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
		protected void fireFinishPricingEvent(List<U> newColumns){
			FinishPricingEvent finishPricingEvent =null;
			for(CGListener listener : listeners){
				if(finishPricingEvent ==null)
					finishPricingEvent =new FinishPricingEvent(ColGen.this, nrOfColGenIterations, Collections.unmodifiableList(newColumns), objective, upperBound, lowerBound);
				listener.finishPricing(finishPricingEvent);
			}
		}

		/**
		 * Fires a TimeLimitExceededEvent
		 */
		protected  void fireTimeLimitExceededEvent(){
			TimeLimitExceededEvent timeLimitExceededEvent =null;
			for(CGListener listener : listeners){
				if(timeLimitExceededEvent ==null)
					timeLimitExceededEvent =new TimeLimitExceededEvent(ColGen.this);
				listener.timeLimitExceeded(timeLimitExceededEvent);
			}
		}
	}
}
