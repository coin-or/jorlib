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
 * Event generated when Branch-and-Price prunes a node in the search tree
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class PruneNodeEvent<T extends ModelInterface, U extends AbstractColumn<T, ? extends AbstractPricingProblem<T,U>>>
    extends EventObject
{

    private static final long serialVersionUID = 391540912011467710L;

    /** Node which is being pruned **/
    public final BAPNode<T, U> node;
    /** Bound on this node **/
    public final double nodeBound;
    /** Objective value of best incumbent solution discovered so far **/
    public final double objectiveIncumbentSolution;

    /**
     * Creates a new PruneNodeEvent
     * 
     * @param source Generator of the event
     * @param node ID of the node being pruned
     * @param nodeBound Bound on the node
     * @param objectiveIncumbentSolution Objective value of best incumbent solution discovered thus far
     */
    public PruneNodeEvent(Object source, BAPNode<T, U> node, double nodeBound, double objectiveIncumbentSolution)
    {
        super(source);
        this.node = node;
        this.nodeBound = nodeBound;
        this.objectiveIncumbentSolution = objectiveIncumbentSolution;
    }
}
