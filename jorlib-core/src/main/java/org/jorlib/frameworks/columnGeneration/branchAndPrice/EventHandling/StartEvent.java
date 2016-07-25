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
 * StartEvent.java
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
 * Event generated when Branch-and-Price or column generation is started
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class StartEvent extends EventObject{

    /** Name of the instance being solved **/
    public final String instanceName;

    /** Best available integer solution at the start of the Branch-and-Price or Column generation procedure **/
    public final int objectiveIncumbentSolution;

    /**
     * Creates a new StartEvent
     * @param source Generator of the event
     * @param instanceName Name of the instance being solved
     * @param objectiveIncumbentSolution Best available integer solution at the start of the Branch-and-Price or Column generation procedure
     */
    public StartEvent(Object source, String instanceName, int objectiveIncumbentSolution){
        super(source);
        this.instanceName=instanceName;
        this.objectiveIncumbentSolution=objectiveIncumbentSolution;
    }
}
