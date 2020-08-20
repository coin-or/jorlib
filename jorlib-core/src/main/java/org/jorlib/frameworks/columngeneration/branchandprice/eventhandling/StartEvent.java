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
 * Event generated when Branch-and-Price or column generation is started
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class StartEvent
    extends EventObject
{

    private static final long serialVersionUID = 4130714412964979464L;

    /** Name of the instance being solved **/
    public final String instanceName;

    /**
     * Objective value of best available incumbent solution at the start of the Branch-and-Price or Column generation
     * procedure
     **/
    public final double objectiveIncumbentSolution;

    /**
     * Creates a new StartEvent
     * 
     * @param source Generator of the event
     * @param instanceName Name of the instance being solved
     * @param objectiveIncumbentSolution Objective value of best available incumbent solution at the start of the
     *        Branch-and-Price or Column generation procedure
     */
    public StartEvent(Object source, String instanceName, double objectiveIncumbentSolution)
    {
        super(source);
        this.instanceName = instanceName;
        this.objectiveIncumbentSolution = objectiveIncumbentSolution;
    }
}
