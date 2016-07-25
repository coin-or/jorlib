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
 * FinishMasterEvent.java
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
package org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling;

import java.util.EventObject;

/**
 * Event generated when the column generation procedure solved the master problem
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class FinishMasterEvent extends EventObject{

    /** Indicates which iteration we are at in the column generation procedure **/
    public final int columnGenerationIteration;
    /** Objective value of the master problem **/
    public final double objective;
    /** Cutoff value: Column Generation is terminated when the bound on the Master Objective is worse than the cutoff value **/
    public final int cutoffValue;
    /** Best available bound on the master objective **/
    public final double boundOnMasterObjective;

    /**
     * Creates a new FinishMasterEvent
     * @param source Generator of the event
     * @param columnGenerationIteration column generation iteration during which this event was fired
     * @param objective objective of master problem
     * @param cutoffValue best available integer solution
     * @param boundOnMasterObjective best available bound on master problem
     */
    public FinishMasterEvent(Object source, int columnGenerationIteration, double objective, int cutoffValue, double boundOnMasterObjective){
        super(source);
        this.columnGenerationIteration=columnGenerationIteration;
        this.objective=objective;
        this.cutoffValue=cutoffValue;
        this.boundOnMasterObjective=boundOnMasterObjective;
    }
}
