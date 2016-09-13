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
 * Event generated when Branch-and-Price node has an integer solution
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class NodeIsIntegerEvent
    extends EventObject
{

    /** Node which is integer **/
    public final BAPNode node;
    /** Bound on this node **/
    public final double nodeBound;
    /** Objective value of this node **/
    public final int nodeValue;

    /**
     * Creates a new NodeIsIntegerEvent
     * 
     * @param source Generator of the event
     * @param node Node which is integer
     * @param nodeBound Bound on the objective value of the node
     * @param nodeValue Objective value of the node. nodeBound and nodeValue are equal when the node
     *        is solved to optimality
     */
    public NodeIsIntegerEvent(Object source, BAPNode node, double nodeBound, int nodeValue)
    {
        super(source);
        this.node = node;
        this.nodeBound = nodeBound;
        this.nodeValue = nodeValue;
    }
}
