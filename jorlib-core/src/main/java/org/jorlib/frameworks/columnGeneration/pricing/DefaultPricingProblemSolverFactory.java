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
 * Factory class for instantiating new pricing problems
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public final class  DefaultPricingProblemSolverFactory<T,U extends AbstractColumn<T,U,V>, V extends AbstractPricingProblem<T,U,V>> implements PricingProblemSolverFactory<T,U,V>{
	
	private final Class<? extends PricingProblemSolver<T, U, V>> solverClass;
	private final T dataModel;
	
	
	
	public DefaultPricingProblemSolverFactory(Class<? extends PricingProblemSolver<T, U, V>> solverClass, T dataModel){
		this.solverClass=solverClass;
		this.dataModel=dataModel;
	}
	
	public PricingProblemSolver<T, U, V> createSolverInstance(V pricingProblem){

		Class<?>[] cArg = new Class[2]; //Our constructor has 3 arguments
		cArg[0] = dataModel.getClass(); //First argument is of *object* type Long
		cArg[1] = pricingProblem.getClass(); //Third argument is of *primitive* type int
		
		PricingProblemSolver<T, U, V> solverInstance=null;
		try {
			solverInstance=solverClass.getDeclaredConstructor(cArg).newInstance(dataModel, pricingProblem);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		return solverInstance;
	}
}
