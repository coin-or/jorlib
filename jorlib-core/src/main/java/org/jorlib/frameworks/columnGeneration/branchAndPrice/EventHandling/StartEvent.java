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
 * Event generated when branch and price or column generation is started
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class StartEvent extends EventObject{

    /** Name of the instance being solved **/
    public final String instanceName;

    /** Initial upper bound on the solution **/
    public final int upperBound;

    /**
     * Creates a new StartEvent
     * @param source Generator of the event
     * @param instanceName Name of the instance being solved
     * @param upperBound Best available integer solution at the start of the Branch-and-Price or Column generation procedure
     */
    public StartEvent(Object source, String instanceName, int upperBound){
        super(source);
        this.instanceName=instanceName;
        this.upperBound=upperBound;
    }
}
