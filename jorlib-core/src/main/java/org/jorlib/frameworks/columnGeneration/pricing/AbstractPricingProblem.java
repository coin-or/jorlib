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
 * AbstractPricingProblem.java
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

import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecisionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines a Pricing Problem.
 * Often, Column generation models decouple in a single Master problem and one or more Pricing problems. The pricing problems can be solved
 * independently. This class models a pricing problem and provides storage for information coming from the master problem which is required
 * to solve the pricing problems, e.g. dual values.
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public abstract class AbstractPricingProblem<T> implements BranchingDecisionListener{

	/** Logger for this class **/
	protected final Logger logger = LoggerFactory.getLogger(AbstractPricingProblem.class);
	
	/** Data object **/
	protected final T dataModel;
	
	/** Name of this pricing problem **/
	public final String name;

	/** Array containing dual information coming from the master problem **/
	public double[] dualCosts;

	/** Variable containing dual information coming from the master problem **/
	public double dualCost;

	/**
	 * Create a new Pricing Problem
	 * @param dataModel Data model
	 * @param name Name of the pricing problem
	 */
	public AbstractPricingProblem(T dataModel, String name){
		this.dataModel = dataModel;
		this.name=name;
	}

	/**
	 * Store dual information in the dualCosts array.
	 * @param dualCosts dual values
	 */
	public void initPricingProblem(double[] dualCosts){
		this.initPricingProblem(dualCosts,0);
	}

	/**
	 * Store dual information in the dualCosts array and dualCost variable. The pricing problem often looks like:
	 * {@code a_1x_1+a_2x_2+...+a_nx_n <= b}, where {@code a_i} are dual variables, and {@code b} some constant. The dualCosts array would hold the
	 * {@code a_i} values whereas {@code b} is stored in the dualCost variable
	 *
	 * @param dualCosts dual values
	 * @param dualCost dual value
	 */
	public void initPricingProblem(double[] dualCosts, double dualCost){
		this.dualCosts =dualCosts;
		this.dualCost =dualCost;
	}
	
	public String toString(){
		return name;
	}

	/**
	 * Method invoked when a branching decision is executed
	 * @param bd branching decision
	 */
	@Override
	public void branchingDecisionPerformed(BranchingDecision bd) {
		//Nothing to do here
	}

	/**
	 * Method invoked when a branching decision is reversed due to backtracking in the Branch-and-Price tree
	 * @param bd branching decision
	 */
	@Override
	public void branchingDecisionReversed(BranchingDecision bd) {
		//Nothing to do here
	}
}
