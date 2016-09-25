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
import org.jorlib.frameworks.columngeneration.colgenmain.AbstractColumn;
import org.jorlib.frameworks.columngeneration.model.ModelInterface;
import org.jorlib.frameworks.columngeneration.pricing.AbstractPricingProblem;

import java.util.EventObject;

/**
 * Event generated when a Branch-and-Price node is fractional
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class NodeIsFractionalEvent<T extends ModelInterface, U extends AbstractColumn<T, ? extends AbstractPricingProblem<T,U>>>
    extends EventObject
{
    private static final long serialVersionUID = -2425443687514518307L;

    /** Node which is fractional **/
    public final BAPNode<T,U> node;
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
    public NodeIsFractionalEvent(Object source, BAPNode<T,U> node, double nodeBound, double nodeValue)
    {
        super(source);
        this.node = node;
        this.nodeBound = nodeBound;
        this.nodeValue = nodeValue;
    }
}
