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
 * ProcessingNextNodeEvent.java
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
 * Event generated when branch and price starts processing a new node
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class ProcessingNextNodeEvent extends EventObject{

    public final int nodeID;
    public final int nodesInQueue;
    public final int globalUB;

    /**
     * Creates a new ProcessingNextNodeEvent
     * @param source Generator of the event
     * @param nodeID ID of the node which will be processed
     * @param nodesInQueue Number of nodes currently in the queue
     * @param globalUB Best integer solution found thus far
     */
    public ProcessingNextNodeEvent(Object source, int nodeID, int nodesInQueue, int globalUB){
        super(source);
        this.nodeID=nodeID;
        this.nodesInQueue=nodesInQueue;
        this.globalUB=globalUB;
    }
}
