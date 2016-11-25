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
package org.jorlib.frameworks.columngeneration.branchandprice.eventhandling;

import org.jorlib.frameworks.columngeneration.colgenmain.AbstractColumn;
import org.jorlib.frameworks.columngeneration.model.ModelInterface;
import org.jorlib.frameworks.columngeneration.pricing.AbstractPricingProblem;

import java.util.EventObject;
import java.util.List;

/**
 * Event generated when the column generation procedure finishes the pricing procedure
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class FinishPricingEvent<T extends ModelInterface, U extends AbstractColumn<T, ? extends AbstractPricingProblem<T,U>>>
    extends EventObject
{

    private static final long serialVersionUID = 316287891741086187L;

    /** Indicates which iteration we are at in the column generation procedure **/
    public final int columnGenerationIteration;
    /**
     * List of new columns, or empty list if no new columns could be found by the pricing problem
     **/
    public List<U> columns;
    /** Objective value of the master problem **/
    public final double objective;
    /**
     * Cutoff value: Column Generation is terminated when the bound on the Master Objective is worse
     * than the cutoff value
     **/
    public final double cutoffValue;
    /** Best available bound on the master objective **/
    public final double boundOnMasterObjective;

    /**
     * Creates a new FinishMasterEvent
     * 
     * @param source Generator of the event
     * @param columnGenerationIteration column generation iteration during which this event was
     *        fired
     * @param columns columns generated by the pricing problem
     * @param objective objective value
     * @param cutoffValue cutoff value
     * @param boundOnMasterObjective best available bound on the master objective
     */
    public FinishPricingEvent(
        Object source, int columnGenerationIteration, List<U> columns, double objective,
        double cutoffValue, double boundOnMasterObjective)
    {
        super(source);
        this.columnGenerationIteration = columnGenerationIteration;
        this.columns = columns;
        this.objective = objective;
        this.cutoffValue = cutoffValue;
        this.boundOnMasterObjective = boundOnMasterObjective;
    }
}
