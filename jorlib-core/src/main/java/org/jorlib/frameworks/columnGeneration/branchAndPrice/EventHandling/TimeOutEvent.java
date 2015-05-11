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
 * TimeOutEvent.java
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
 * Event generated when branch and price is terminated due to a time out
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class TimeOutEvent extends EventObject{

    /** Node which was being processed when TimeOutEvent occurred **/
    public final int  nodeID;

    /**
     * Event fired when a time out occurs due to a time limit
     * @param source Generator of the event
     * @param nodeID node which was being processed when the time out occurred.
     */
    public TimeOutEvent(Object source, int nodeID){
        super(source);
        this.nodeID=nodeID;
    }
}
