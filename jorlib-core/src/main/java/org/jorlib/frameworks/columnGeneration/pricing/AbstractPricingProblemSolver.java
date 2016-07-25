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
 * AbstractPricingProblemSolver.java
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
import java.util.List;
import java.util.concurrent.Callable;

import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecisionListener;
import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class representing a solver for a particular pricing problem
 *
 * @author Joris Kinable
 * @version 13-4-2015
 *
 * @param <T> type of model data
 * @param <U> type of column
 * @param <V> type of pricing problem
 */
public abstract class AbstractPricingProblemSolver<T,U extends AbstractColumn<T, V>, V extends AbstractPricingProblem<T>> implements Callable<Void>, BranchingDecisionListener{

	/** Logger for this class **/
	protected final Logger logger = LoggerFactory.getLogger(AbstractPricingProblemSolver.class);
	/** Configuration file **/
	protected final Configuration config=Configuration.getConfiguration();

	/** Name of the pricing problem solver **/
	protected String name="solverName";
	/** Data model **/
	protected final T dataModel;
	/** Pricing problem **/
	protected final V pricingProblem;
	/** Time by which the algorithm needs to be finished. **/
	protected long timeLimit;
	/** Objective of pricing problem (best column) **/
	protected double objective;
	/** Columns generated **/
	protected List<U> columns;
	/** Boolean indicating whether the pricing problem could be solved. Certain branching decisions may render the pricing problem infeasible **/
	protected boolean pricingProblemInfeasible;

	/**
	 * Creates a new solver instance for a particular pricing problem
	 * @param dataModel data model
	 * @param pricingProblem pricing problem
	 */
	public AbstractPricingProblemSolver(T dataModel, V pricingProblem){
		this.dataModel=dataModel;
		this.pricingProblem=pricingProblem;
		this.columns=new ArrayList<>();
	}
	
	/**
	 * Method needed for parallelization. Solves the pricing problem in a separate thread
	 * @return Nothing
	 */
	@Override
	public Void call() throws Exception {
		columns.clear();
		this.setObjective();
		this.solve();
		return null;
	}
	
	/**
	 * Solves the pricing problem. The method invokes generateNewColumns internally and stores the generated columns in an internal
	 * list.
	 * @throws TimeLimitExceededException TimeLimitExceededException
	 */
	protected void solve() throws TimeLimitExceededException{
		columns.addAll(this.generateNewColumns());
	}

	/**
	 * Generates one or more new columns with negative reduced cost. Every solver must implement this method.
	 * @return List of columns with negative reduced cost
	 * @throws TimeLimitExceededException thrown when timelimit is exceeded
	 */
	protected abstract List<U> generateNewColumns() throws TimeLimitExceededException;
	
	/**
	 * Returns the name of the pricing problem
	 * @return name of the solver
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * Set time limit (future point in time). A TimeLimitExceededException is thrown when this time limit is exceeded.
	 * @param timeLimit future point in time ({@code System.currentTimeMillis() + runtime})
	 */
	public void setTimeLimit(long timeLimit){
		this.timeLimit=timeLimit;
	}
	
	/**
	 * Returns the cost of the most negative reduced cost column. If the pricing problem is an maximization problem, then any feasible solution is
	 * a lower bound.
	 * @return Objective value of the pricing problem.
	 */
	public double getObjective(){
		return objective;
	}

	/**
	 * Method which sets the objective of the Pricing Problem. This method is invoked directly *before* the generatenewColumns() method is solved. It allows the user to update the pricing solver with fresh
	 * dual information coming from the master problem. Typically this information is stored inside the PricingProblem objects.
	 */
	protected abstract void setObjective();
	
	/**
	 * Returns a bound on the objective of the pricing problem. If the pricing problem is solved to optimality, this function would typically return the objective value.
	 * Alternatively, the objective value of a relaxation of the Pricing Problem may be returned, e.g. the LP relaxation when the Pricing Problem is implemented as a MIP, or the value of a Lagrangian Relaxation.
	 * @return a bound on the objective of the pricing problem)
	 */
	public double getBound(){
		throw new UnsupportedOperationException("Not implemented");
	}

	/**
	 * Returns the list of negative reduced cost columns generated by the solver
	 * @return List of columns with negative reduced cost, or an empty list when no such column exists.
	 */
	public List<U> getColumns(){
		return columns;
	}
	
	/**
	 * Returns whether the pricing problem is feasible. For example due to branching decisions, no feasible solution may exist for a particular pricing problem.
	 * @return true if a feasible solution exists to the pricing problem, or false if there is none, or none could be found in case of a heuristic pricing solver.
	 */
	public boolean pricingProblemIsFeasible(){
		return !pricingProblemInfeasible;
	}

	/**
	 * Close the pricing problem and perform cleanup
	 */
	public abstract void close();

	/**
	 * Method invoked when a branching decision is executed.
	 * @param bd branching decision
	 */
	@Override
	public void branchingDecisionPerformed(BranchingDecision bd) {
	}

	/**
	 * Method invoked when a branching decision is reversed due to backtracking in the Branch-and-Price tree
	 * @param bd branching decision
	 */
	@Override
	public void branchingDecisionReversed(BranchingDecision bd) {
	}
	
}
