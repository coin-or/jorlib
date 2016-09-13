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
 * Event generated when Branch-and-Price node is infeasible
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class NodeIsInfeasibleEvent
    extends EventObject
{

    /** Node which is infeasible **/
    public final BAPNode node;

    /**
     * Creates a new NodeIsInfeasibleEvent
     * 
     * @param source Generator of the event
     * @param node Node which is infeasible
     */
    public NodeIsInfeasibleEvent(Object source, BAPNode node)
    {
        super(source);
        this.node = node;
    }
}
