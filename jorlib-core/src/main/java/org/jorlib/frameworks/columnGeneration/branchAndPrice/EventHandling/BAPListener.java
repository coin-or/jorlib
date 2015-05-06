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
 * Listener for Branch and price events
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public interface BAPListener extends EventListener {
    /**
     * Method invoked when branch and price is started
     * @param startBAPEvent
     */
    public void startBAP(StartBAPEvent startBAPEvent);

    /**
     * Method invoked when branch and price is finished (either the optimal solution has been found, or the process is terminated due to a time limit)
     * @param startBAPEvent
     */
    public void stopBAP(StopBAPEvent startBAPEvent);

    /**
     * Method invoked when a node is pruned, for example because the node's lower bound exceeds the best known feasible integer solution (upper bound)
     * @param pruneNodeEvent
     */
    public void pruneNode(PruneNodeEvent pruneNodeEvent);

    /**
     * Method invoked when a node is infeasible, i.e. no solution to its master problem exists
     * @param nodeIsInfeasibleEvent
     */
    public void nodeIsInfeasible(NodeIsInfeasibleEvent nodeIsInfeasibleEvent);

    /**
     * Method invoked when the node is an integer node
     * @param nodeIsIntegerEvent
     */
    public void nodeIsInteger(NodeIsIntegerEvent nodeIsIntegerEvent);

    /**
     * Method invoked when the node has a fractional solution (branching is required)
     * @param nodeIsFractionalEvent
     */
    public void nodeIsFractional(NodeIsFractionalEvent nodeIsFractionalEvent);

    /**
     * Method invoked when the branch and price process starts processing a new node
     * @param processingNextNodeEvent
     */
    public void processNextNode(ProcessingNextNodeEvent processingNextNodeEvent);

    /**
     * Method invoked when the branch and price process finishes processing a node
     * @param finishCGEvent
     */
    public void finishedColumnGenerationForNode(FinishCGEvent finishCGEvent);

    /**
     * Method invoked when the branch and price process is terminated due to a time out
     * @param timeOutEvent
     */
    public void timeOut(TimeOutEvent timeOutEvent);
}
