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

import org.jorlib.frameworks.columngeneration.colgenmain.AbstractColumn;
import org.jorlib.frameworks.columngeneration.model.ModelInterface;
import org.jorlib.frameworks.columngeneration.pricing.AbstractPricingProblem;

import java.util.EventListener;

/**
 * Listener for Branch-and-Price events
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public interface BAPListener<T extends ModelInterface, U extends AbstractColumn<T, ? extends AbstractPricingProblem<T,U>>>
    extends EventListener
{
    /**
     * Method invoked when Branch-and-Price is started
     * 
     * @param startEvent startEvent
     */
    void startBAP(StartEvent startEvent);

    /**
     * Method invoked when Branch-and-Price is finished (either the optimal solution has been found,
     * or the process is terminated due to a time limit)
     * 
     * @param finishEvent finishEvent
     */
    void finishBAP(FinishEvent finishEvent);

    /**
     * Method invoked when a node is pruned, for example because the node's lower bound exceeds the
     * best known feasible integer solution (upper bound)
     * 
     * @param pruneNodeEvent pruneNodeEvent
     */
    void pruneNode(PruneNodeEvent<T, U> pruneNodeEvent);

    /**
     * Method invoked when a node is infeasible, i.e no solution to its master problem exists
     * 
     * @param nodeIsInfeasibleEvent nodeIsInfeasibleEvent
     */
    void nodeIsInfeasible(NodeIsInfeasibleEvent<T,U> nodeIsInfeasibleEvent);

    /**
     * Method invoked when the node is an integer node
     * 
     * @param nodeIsIntegerEvent nodeIsIntegerEvent
     */
    void nodeIsInteger(NodeIsIntegerEvent<T,U> nodeIsIntegerEvent);

    /**
     * Method invoked when the node has a fractional solution (branching is required)
     * 
     * @param nodeIsFractionalEvent nodeIsFractionalEvent
     */
    void nodeIsFractional(NodeIsFractionalEvent<T,U> nodeIsFractionalEvent);

    /**
     * Method invoked when the Branch-and-Price process starts processing a new node
     * 
     * @param processingNextNodeEvent processingNextNodeEvent
     */
    void processNextNode(ProcessingNextNodeEvent<T,U> processingNextNodeEvent);

    /**
     * Method invoked when the Branch-and-Price process finishes processing a node
     * 
     * @param finishProcessingNodeEvent finishProcessingNodeEvent
     */
    void finishedColumnGenerationForNode(FinishProcessingNodeEvent<T,U> finishProcessingNodeEvent);

    /**
     * Method invoked when the Branch-and-Price process is terminated due to a time out
     * 
     * @param timeLimitExceededEvent timeLimitExceededEvent
     */
    void timeLimitExceeded(TimeLimitExceededEvent timeLimitExceededEvent);

    /**
     * Method invoked when Branch-and-Price created new branches in the Branch-and-Price tree
     * 
     * @param branchEvent branchEvent
     */
    void branchCreated(BranchEvent<T,U> branchEvent);
}
