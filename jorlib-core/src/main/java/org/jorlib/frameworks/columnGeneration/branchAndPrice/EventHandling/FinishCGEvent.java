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
 * FinishCGEvent.java
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
 * Event generated when branch and price finished the computations for a given node
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class FinishCGEvent extends EventObject{

    public final int nodeID; //ID of the node which has been solved
    public final double nodeBound; //Lower bound on the node
    public final double nodeValue; //Objective value of the node
    public final int numberOfCGIterations;
    public final long masterSolveTime;
    public final long pricingSolveTime;
    public final int nrGeneratedColumns;

    /**
     * Creates a new FinishCGEvent
     * @param source Generator of this event
     * @param nodeID ID of the node which has been solved
     * @param nodeBound Lower bound on the node which has been solved
     * @param nodeValue Objective value of the node which has been solved. When solved to optimality, nodeBound and nodeValue should be equal
     * @param numberOfCGIterations Number of CG iterations it took to solve the node
     * @param masterSolveTime Total amount of time spent on solving the master problems
     * @param pricingSolveTime Total amount of time spent on solving the pricing problems
     * @param nrGeneratedColumns Total number of columns generated
     */
    public FinishCGEvent(Object source,
                         int nodeID,
                         double nodeBound,
                         double nodeValue,
                         int numberOfCGIterations,
                         long masterSolveTime,
                         long pricingSolveTime,
                         int nrGeneratedColumns){
        super(source);
        this.nodeID=nodeID;
        this.nodeBound=nodeBound;
        this.nodeValue=nodeValue;
        this.numberOfCGIterations=numberOfCGIterations;
        this.masterSolveTime=masterSolveTime;
        this.pricingSolveTime=pricingSolveTime;
        this.nrGeneratedColumns=nrGeneratedColumns;
    }
}
