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
 * PricingProblemBunddle.java
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

import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;

/**
 * Each pricing problem (PricingProblem) is solved by some algorithm (AbstractPricingProblemSolver). This class is a container which holds
 * all instance of a particular AbstractPricingProblemSolver. Typically, there exists an instance for each pricing problem.
 * The instances are produced by a PricingProblemSolverFactory. This class takes a solver, list of pricing problems and a solverFactory
 * and it produces the necessary solver instances.
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public final class PricingProblemBundle<T, U extends AbstractColumn<T, V>, V extends AbstractPricingProblem<T>> {

	/** The solver (class) **/
	public final Class<? extends AbstractPricingProblemSolver<T, U, V>> pricingSolver;

	/** The solver instances. The number of instances equals the number of pricing problems **/
	public final List<AbstractPricingProblemSolver<T, U, V>> solverInstances;
	
	/**
	 * Each pricing problem ({@link AbstractPricingProblem}) is solved by some algorithm ({@link AbstractPricingProblemSolver}). This class is a container which holds
	 * a single instance of a AbstractPricingProblemSolver for *each* PricingProblem. The instances are produced by a PricingProblemSolverFactory
	 * @param pricingSolver The solver
	 * @param pricingProblems List of pricing problems
	 * @param solverFactory Factory to produce Solver Instances of the type of the pricingSolver.
	 */
	public PricingProblemBundle(Class<? extends AbstractPricingProblemSolver<T, U, V>> pricingSolver,
			List<V> pricingProblems, 
			PricingProblemSolverFactory<T, U, V> solverFactory){
		this.pricingSolver=pricingSolver;
		//Create the solver instances
		solverInstances=new ArrayList<>(pricingProblems.size());
		for(V pricingProblem : pricingProblems){
			solverInstances.add(solverFactory.createSolverInstance(pricingProblem));
		}
	}
}
