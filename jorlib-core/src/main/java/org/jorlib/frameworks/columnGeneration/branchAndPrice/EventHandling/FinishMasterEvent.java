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

    /** Objective value of the master problem **/
    public final double objective;
    /** Integer Upper bound on the master problem (i.e. a feasible integer solution) **/
    public final int upperBound;
    /** Lower bound on the objective value **/
    public final double lowerBound;

    /**
     * Creates a new FinishMasterEvent
     * @param source Generator of the event
     */
    public FinishMasterEvent(Object source, double objective, int upperBound, double lowerBound){
        super(source);
        this.objective=objective;
        this.upperBound=upperBound;
        this.lowerBound=lowerBound;
    }
}
