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

import org.jorlib.frameworks.columnGeneration.branchAndPrice.BAPNode;

import java.util.EventObject;

/**
 * Event generated when Branch-and-Price node is infeasible
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class NodeIsInfeasibleEvent extends EventObject{

    /** Node which is infeasible **/
    public final BAPNode node;

    /**
     * Creates a new NodeIsInfeasibleEvent
     * @param source Generator of the event
     * @param node Node which is infeasible
     */
    public NodeIsInfeasibleEvent(Object source, BAPNode node){
        super(source);
        this.node=node;
    }
}
