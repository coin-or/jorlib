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
 * DefaultPricingProblemSolverFactory.java
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

import java.lang.reflect.InvocationTargetException;

import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;

/**
 * Factory class which produces a solver instances for a given pricing problem
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public final class  DefaultPricingProblemSolverFactory<T,U extends AbstractColumn<T, V>, V extends AbstractPricingProblem<T>> implements PricingProblemSolverFactory<T,U,V>{

	/** The solver (class)**/
	private final Class<? extends AbstractPricingProblemSolver<T, U, V>> solverClass;

	/** Data model **/
	private final T dataModel;


	/**
	 * Creates a new factory.
	 *
	 * @param solverClass The solver for which this factory produces instances
	 * @param dataModel The data model
	 */
	public DefaultPricingProblemSolverFactory(Class<? extends AbstractPricingProblemSolver<T, U, V>> solverClass, T dataModel){
		this.solverClass=solverClass;
		this.dataModel=dataModel;
	}

	/**
	 * Creates a new instance of the solver for the given pricing problem.
	 *
	 * @param pricingProblem The pricing problem for which a new solver instance must be created
	 * @return A new solver instance
	 */
	public AbstractPricingProblemSolver<T, U, V> createSolverInstance(V pricingProblem){

		Class<?>[] cArg = new Class[2]; //Our constructor has 2 arguments
		cArg[0] = dataModel.getClass(); //First argument is of type T
		cArg[1] = pricingProblem.getClass(); //Second argument has the type of the pricing problem
		
		AbstractPricingProblemSolver<T, U, V> solverInstance=null; //Create the new instance
		try {
			solverInstance=solverClass.getDeclaredConstructor(cArg).newInstance(dataModel, pricingProblem);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

		return solverInstance;
	}
}
