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
 * BranchingDecisionListener.java
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
package org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions;

/**
 * Interface defining a BranchingDecision Listener. Every BranchingDecision listener is informed about branching
 * decisions which are executed.
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public interface BranchingDecisionListener {
    /**
     * This method is called when a branching decision is executed
     * @param bd branching decision
     */
    void branchingDecisionPerformed(BranchingDecision bd);

    /**
     * This method is called when a branching decision is reversed (backtracking in the Branch-and-Price tree)
     * @param bd branching decision
     */
    void branchingDecisionReversed(BranchingDecision bd);
}
