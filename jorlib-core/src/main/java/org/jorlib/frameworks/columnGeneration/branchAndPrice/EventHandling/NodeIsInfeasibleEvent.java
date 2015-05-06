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
 * NodeIsInfeasibleEvent.java
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
 * Event generated when branch and price node is infeasible
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class NodeIsInfeasibleEvent extends EventObject{

    public final int nodeID;

    /**
     * Creates a new NodeIsInfeasibleEvent
     * @param source Generator of the event
     * @param nodeID Node which is infeasible
     */
    public NodeIsInfeasibleEvent(Object source, int nodeID){
        super(source);
        this.nodeID=nodeID;
    }
}
