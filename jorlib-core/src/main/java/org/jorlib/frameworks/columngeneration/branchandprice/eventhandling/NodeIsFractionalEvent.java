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

import org.jorlib.frameworks.columngeneration.branchandprice.BAPNode;

import java.util.EventObject;

/**
 * Event generated when a Branch-and-Price node is fractional
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class NodeIsFractionalEvent
    extends EventObject
{
    /** Node which is fractional **/
    public final BAPNode node;
    /** Bound of this node **/
    public final double nodeBound;
    /** Objective value of this node **/
    public final double nodeValue;

    /**
     * Creates a new NodeIsFractionalEvent
     * 
     * @param source Generator of the event
     * @param node Node which has a fractional solution
     * @param nodeBound Bound on the solution
     * @param nodeValue Objective value of the node. nodeBound and nodeValue are equal when the node
     *        is solved to optimality
     */
    public NodeIsFractionalEvent(Object source, BAPNode node, double nodeBound, double nodeValue)
    {
        super(source);
        this.node = node;
        this.nodeBound = nodeBound;
        this.nodeValue = nodeValue;
    }
}
