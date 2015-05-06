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
 * NodeIsFractionalEvent.java
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
 * Event generated when a branch and price node is fractional
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class NodeIsFractionalEvent extends EventObject{
    public final int nodeID;
    public final double nodeBound;
    public final double nodeValue;

    /**
     * Creates a new NodeIsFractionalEvent
     * @param source Generator of the event
     * @param nodeID Node which has a fractional solution
     * @param nodeBound Lower bound on the solution
     * @param nodeValue Objective value of the node. nodeBound and nodeValue are equal when the node is solved to optimality
     */
    public NodeIsFractionalEvent(Object source, int nodeID, double nodeBound, double nodeValue){
        super(source);
        this.nodeID=nodeID;
        this.nodeBound=nodeBound;
        this.nodeValue=nodeValue;
    }
}
