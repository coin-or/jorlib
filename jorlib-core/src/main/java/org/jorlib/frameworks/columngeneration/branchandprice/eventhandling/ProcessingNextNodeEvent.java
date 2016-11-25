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
 * Event generated when Branch-and-Price starts processing a new node
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class ProcessingNextNodeEvent<T extends ModelInterface, U extends AbstractColumn<T, ? extends AbstractPricingProblem<T,U>>>
    extends EventObject
{

    private static final long serialVersionUID = -2426047978609173833L;

    /** Node which will be processed **/
    public final BAPNode<T,U> node;
    /** Number of nodes currently waiting in the queue **/
    public final int nodesInQueue;
    /** Best integer solution obtained thus far **/
    public final double objectiveIncumbentSolution;

    /**
     * Creates a new ProcessingNextNodeEvent
     * 
     * @param source Generator of the event
     * @param node Node which will be processed
     * @param nodesInQueue Number of nodes currently in the queue
     * @param objectiveIncumbentSolution Best integer solution found thus far
     */
    public ProcessingNextNodeEvent(
        Object source, BAPNode<T,U> node, int nodesInQueue, double objectiveIncumbentSolution)
    {
        super(source);
        this.node = node;
        this.nodesInQueue = nodesInQueue;
        this.objectiveIncumbentSolution = objectiveIncumbentSolution;
    }
}
