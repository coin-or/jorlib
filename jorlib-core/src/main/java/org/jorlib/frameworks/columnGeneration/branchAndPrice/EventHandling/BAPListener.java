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
 * BAPListener.java
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

import java.util.EventListener;

/**
 * Listener for Branch-and-Price events
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public interface BAPListener extends EventListener {
    /**
     * Method invoked when Branch-and-Price is started
     * @param startEvent startEvent
     */
    void startBAP(StartEvent startEvent);

    /**
     * Method invoked when Branch-and-Price is finished (either the optimal solution has been found, or the process is terminated due to a time limit)
     * @param finishEvent finishEvent
     */
    void finishBAP(FinishEvent finishEvent);

    /**
     * Method invoked when a node is pruned, for example because the node's lower bound exceeds the best known feasible integer solution (upper bound)
     * @param pruneNodeEvent pruneNodeEvent
     */
    void pruneNode(PruneNodeEvent pruneNodeEvent);

    /**
     * Method invoked when a node is infeasible, i.e no solution to its master problem exists
     * @param nodeIsInfeasibleEvent nodeIsInfeasibleEvent
     */
    void nodeIsInfeasible(NodeIsInfeasibleEvent nodeIsInfeasibleEvent);

    /**
     * Method invoked when the node is an integer node
     * @param nodeIsIntegerEvent nodeIsIntegerEvent
     */
    void nodeIsInteger(NodeIsIntegerEvent nodeIsIntegerEvent);

    /**
     * Method invoked when the node has a fractional solution (branching is required)
     * @param nodeIsFractionalEvent nodeIsFractionalEvent
     */
    void nodeIsFractional(NodeIsFractionalEvent nodeIsFractionalEvent);

    /**
     * Method invoked when the Branch-and-Price process starts processing a new node
     * @param processingNextNodeEvent processingNextNodeEvent
     */
    void processNextNode(ProcessingNextNodeEvent processingNextNodeEvent);

    /**
     * Method invoked when the Branch-and-Price process finishes processing a node
     * @param finishProcessingNodeEvent finishProcessingNodeEvent
     */
    void finishedColumnGenerationForNode(FinishProcessingNodeEvent finishProcessingNodeEvent);

    /**
     * Method invoked when the Branch-and-Price process is terminated due to a time out
     * @param timeLimitExceededEvent timeLimitExceededEvent
     */
    void timeLimitExceeded(TimeLimitExceededEvent timeLimitExceededEvent);

    /**
     * Method invoked when Branch-and-Price created new branches in the Branch-and-Price tree
     * @param branchEvent branchEvent
     */
    void branchCreated(BranchEvent branchEvent);
}
