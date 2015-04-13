package org.jorlib.frameworks.columnGeneration.master;

import java.util.Collection;
import java.util.List;

import org.jorlib.frameworks.columnGeneration.colgenMain.Column;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutHandler;
import org.jorlib.frameworks.columnGeneration.master.cuts.Inequality;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblem;
import org.jorlib.frameworks.columnGeneration.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Master<T, V extends PricingProblem<T, U>, U extends Column<T,U>> {
	protected final Logger logger = LoggerFactory.getLogger(Master.class);
	protected final Configuration config=Configuration.getConfiguration();

	//Data object describing the problem at hand
	protected final T modelData;
	//Data object containing information for the master problem
	protected MasterData masterData;
	//Handle to a cutHandler which performs separation
	protected CutHandler cutHandler;
	
	public Master(T modelData){
		this.modelData=modelData;
		this.masterData=new MasterData();
		cutHandler=new CutHandler();
	}
	public Master(T modelData, MasterData masterData, CutHandler cutHandler){
		this.modelData=modelData;
		this.cutHandler=cutHandler;
	}
	
	/**
	 * Solve the master problem
	 * @param timeLimit Future point in time by which this method must be finished
	 * @throws TimeLimitExceededException
	 */
	public void solve(long timeLimit) throws TimeLimitExceededException{
		masterData.iterations++;
		masterData.optimal=this.solveMasterProblem(timeLimit);
	}
	
	/**
	 * Solve the master problem
	 * @param timeLimit
	 * @return Returns true if successfull (and optimal)
	 * @throws TimeLimitExceededException 
	 */
	protected abstract boolean solveMasterProblem(long timeLimit) throws TimeLimitExceededException;
	
	/**
	 * Get the reduced cost information required for a particular pricingProblem. The pricing problem often looks like:
	 * a_1x_1+a_2x_2+...+a_nx_n <= b, where a_i are dual variables, and b some constant. this method retuns the a_i values.
	 * @return reduced cost information
	 */
//	public abstract double[] getReducedCostVector(V pricingProblem);

	/**
	 * Get the reduced cost information required for a particular pricingProblem. The pricing problem often looks like:
	 * a_1x_1+a_2x_2+...+a_nx_n <= b, where a_i are dual variables, and b some constant. this method retuns the a_i values.
	 * @param pricingProblem
	 * @return Returns the dual constant
	 */
//	public abstract double getDualConstant(V pricingProblem);
	public abstract void initializePricingProblem(V pricingProblem);
	
	/**
	 * Returns the objective value of the current master problem.
	 */
	public double getObjective(){
		return masterData.objectiveValue;
	}
	
	/**
	 * @return Returns the number of times the master problem has been solved
	 */
	public int getIterationCount(){
		return masterData.iterations;
	}
	/**
	 * @return Returns true if the master problem has been solved to optimality
	 */
	public boolean isOptimal(){
		return masterData.optimal;
	}
	
	/**
	* Method which can be invoked externally to check whether the current master problem solution violates any cuts.
	* Obviously, a handle to a cutHandler must have been provided
	* @return true if cuts were added to the master problem, false otherwise
	*/
	public boolean hasNewCuts(){
		logger.debug("Checking for cuts");
		boolean hasNewCuts=false;
		if(cutHandler != null){
			hasNewCuts=cutHandler.generateCuts();
		}
		logger.debug("Cuts found: {}",hasNewCuts);
		return hasNewCuts;
	}
	
	/**
	 * Adds cuts to this master.
	 * Obviously, a handle to a cutHandler must have been provided
	 * @param cuts cuts to be added
	 */
	public void addCuts(Collection<Inequality> cuts){
		cutHandler.addCuts(cuts);
	}
	
	/**
	 * Returns all the cuts in the master model
	 * Obviously, a handle to a cutHandler must have been provided
	 */
	public List<Inequality> getCuts(){
		return cutHandler.getCuts();
	}
	
	/**
	 * Build the master problem
	 */
	protected abstract void buildModel();
	
	/**
	 * Add a column to the model
	 * @param column column to add
	 */
	public abstract void addColumn(U column);

	/**
	 * Add a initial solution (list of columns)
	 * @param columns
	 */
	public void addColumns(List<U> columns){
		for(U column : columns){
			this.addColumn(column);
		}
	}	
	/**
	 * Calculates lower bound on the optimal solution (assuming that the master problem is a minimization problem
	 */
	public double getLowerBound(){
		throw new UnsupportedOperationException("Not implemented");
	}
	
	/**
	 * After the master problem has been solved, a solution has to be returned, consisting of a set of columns selected by the master problem
	 * @return 
	 */
	public abstract List<U> getSolution();
	
	/**
	 * @return Return true if the solution derived by the master problem is integer
	 */
	public boolean solutionIsInteger(){
		throw new UnsupportedOperationException("Not implemented");
	}
	
	/**
	 * To compute a lower bound on the optimal solution of the relaxed master problem (assuming that the master problem is a minimization problem), multiple components
	 * are required, including information from the master problem. This function returns that information.
	 */
	public double getLowerBoundComponent(){
		throw new UnsupportedOperationException("Not implemented");
	}
	/**
	 * Export the master problem to a .lp file
	 * @param name
	 */
	public void exportModel(String name){
		throw new UnsupportedOperationException("Not implemented");
	}
	
	/**
	 * Give a textual representation of the solution
	 */
	public abstract void printSolution();
	
	/**
	 * Close the master problem
	 */
	public abstract void close();
}

