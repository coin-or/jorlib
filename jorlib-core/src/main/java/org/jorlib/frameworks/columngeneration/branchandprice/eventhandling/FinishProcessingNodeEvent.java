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
 * Event generated when Branch-and-Price finished the computations for a given node
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class FinishProcessingNodeEvent<T extends ModelInterface, U extends AbstractColumn<T, ? extends AbstractPricingProblem<T,U>>>
    extends EventObject
{

    private static final long serialVersionUID = -2346015175467711177L;

    /** Node which has been solved **/
    public final BAPNode<T, U> node;
    /** Bound on the node after it is solved **/
    public final double nodeBound;
    /** Objective value of the node **/
    public final double nodeValue;
    /** Number of column generation iterations performed to solve this node **/
    public final int numberOfCGIterations;
    /** Total time spent on solving master problems for this node **/
    public final long masterSolveTime;
    /** Total time spent on solving pricing problems for this node **/
    public final long pricingSolveTime;
    /** Total number of columns generated for this node **/
    public final int nrGeneratedColumns;

    /**
     * Creates a new FinishProcessingNodeEvent
     * 
     * @param source Generator of this event
     * @param node node which has been solved
     * @param nodeBound Bound on the node which has been solved
     * @param nodeValue Objective value of the node which has been solved. When solved to
     *        optimality, nodeBound and nodeValue should be equal
     * @param numberOfCGIterations Number of CG iterations it took to solve the node
     * @param masterSolveTime Total amount of time spent on solving the master problems
     * @param pricingSolveTime Total amount of time spent on solving the pricing problems
     * @param nrGeneratedColumns Total number of columns generated
     */
    public FinishProcessingNodeEvent(
        Object source, BAPNode<T,U> node, double nodeBound, double nodeValue, int numberOfCGIterations,
        long masterSolveTime, long pricingSolveTime, int nrGeneratedColumns)
    {
        super(source);
        this.node = node;
        this.nodeBound = nodeBound;
        this.nodeValue = nodeValue;
        this.numberOfCGIterations = numberOfCGIterations;
        this.masterSolveTime = masterSolveTime;
        this.pricingSolveTime = pricingSolveTime;
        this.nrGeneratedColumns = nrGeneratedColumns;
    }
}
