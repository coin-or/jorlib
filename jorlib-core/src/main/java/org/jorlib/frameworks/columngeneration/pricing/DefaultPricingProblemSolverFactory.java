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

import java.lang.reflect.InvocationTargetException;

import org.jorlib.frameworks.columngeneration.colgenmain.AbstractColumn;
import org.jorlib.frameworks.columngeneration.master.MasterData;
import org.jorlib.frameworks.columngeneration.model.ModelInterface;

/**
 * Factory class which produces a solver instances for a given pricing problem
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public final class DefaultPricingProblemSolverFactory<T extends ModelInterface, U extends AbstractColumn<T, V>,
    V extends AbstractPricingProblem<T, U, W>,
        W extends MasterData<T, U, V, ?>>
    implements PricingProblemSolverFactory<T, U, V>
{

    /** The solver (class) **/
    private final Class<? extends AbstractPricingProblemSolver<T, U, V, W>> solverClass;

    /** Data model **/
    private final T dataModel;

    /**
     * Creates a new factory.
     *
     * @param solverClass The solver for which this factory produces instances
     * @param dataModel The data model
     */
    public DefaultPricingProblemSolverFactory(
        Class<? extends AbstractPricingProblemSolver<T, U, V, W>> solverClass, T dataModel)
    {
        this.solverClass = solverClass;
        this.dataModel = dataModel;
    }

    /**
     * Creates a new instance of the solver for the given pricing problem.
     *
     * @param pricingProblem The pricing problem for which a new solver instance must be created
     * @return A new solver instance
     */
    public AbstractPricingProblemSolver<T, U, V, W> createSolverInstance(V pricingProblem)
    {

        Class<?>[] cArg = new Class[2]; // Our constructor has 2 arguments
        cArg[0] = dataModel.getClass(); // First argument is of type T
        cArg[1] = pricingProblem.getClass(); // Second argument has the type of the pricing problem

        AbstractPricingProblemSolver<T, U, V, W> solverInstance = null; // Create the new instance
        try {
            solverInstance =
                solverClass.getDeclaredConstructor(cArg).newInstance(dataModel, pricingProblem);
        } catch (
            InstantiationException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException | NoSuchMethodException | SecurityException e)
        {
            e.printStackTrace();
        }

        return solverInstance;
    }
}
