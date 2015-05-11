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
 * StartBAPEvent.java
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
 * Event generated when branch and price is started
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class StartBAPEvent extends EventObject{

    /** Name of the instance being solved **/
    public final String instanceName; //Instance being solved

    /**
     * Creates a new StartBAPEvent
     * @param source Generator of the event
     * @param instanceName Name of the instance being solved
     */
    public StartBAPEvent(Object source, String instanceName){
        super(source);
        this.instanceName=instanceName;
    }
}
