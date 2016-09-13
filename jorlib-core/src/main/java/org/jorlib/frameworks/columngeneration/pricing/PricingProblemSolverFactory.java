/* ==========================================
 * jORLib : Java Operations Research Library
 * ==========================================
 *
 * Project Info:  http://www.coin-or.org/projects/jORLib.xml
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015-2016, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 * as published by the Free Software Foundation.
 */
package org.jorlib.frameworks.columngeneration.pricing;

import org.jorlib.frameworks.columngeneration.colgenmain.AbstractColumn;

/**
 * Interface which has to be implemented by a factory class which produces solver instances for the
 * pricing problem provided as its argument.
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 * @param <T> Model
 * @param <U> Columns
 * @param <V> PricingProblem
 */
public interface PricingProblemSolverFactory<T, U extends AbstractColumn<T, V>,
    V extends AbstractPricingProblem<T>>
{

    /**
     * Creates a new solver instance for the pricing problem provided
     * 
     * @param pricingProblem pricing problem
     * @return A new solver instance
     */
    AbstractPricingProblemSolver<T, U, V> createSolverInstance(V pricingProblem);
}
