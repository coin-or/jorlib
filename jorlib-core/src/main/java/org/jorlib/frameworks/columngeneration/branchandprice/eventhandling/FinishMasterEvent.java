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

import java.util.EventObject;

/**
 * Event generated when the column generation procedure solved the master problem
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class FinishMasterEvent
    extends EventObject
{

    private static final long serialVersionUID = 8616333803187638407L;

    /** Indicates which iteration we are at in the column generation procedure **/
    public final int columnGenerationIteration;
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
     * @param objective objective of master problem
     * @param cutoffValue best available integer solution
     * @param boundOnMasterObjective best available bound on master problem
     */
    public FinishMasterEvent(
        Object source, int columnGenerationIteration, double objective, double cutoffValue,
        double boundOnMasterObjective)
    {
        super(source);
        this.columnGenerationIteration = columnGenerationIteration;
        this.objective = objective;
        this.cutoffValue = cutoffValue;
        this.boundOnMasterObjective = boundOnMasterObjective;
    }
}
