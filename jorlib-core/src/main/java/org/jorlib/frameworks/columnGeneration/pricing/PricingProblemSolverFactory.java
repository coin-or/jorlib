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
 * PricingProblemSolverFactory.java
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

import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;

/**
 * Interface which has to be implemented by a factory class which produces solver instances for the pricing problem
 * provided as its argument.
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 * @param <T> Model
 * @param <U> Columns
 * @param <V> PricingProblem
 */
public interface PricingProblemSolverFactory<T,U extends AbstractColumn<T, V>, V extends AbstractPricingProblem<T>> {

	/**
	 * Creates a new solver instance for the pricing problem provided
	 * @param pricingProblem pricing problem
	 * @return A new solver instance
	 */
	AbstractPricingProblemSolver<T, U, V> createSolverInstance(V pricingProblem);
}
