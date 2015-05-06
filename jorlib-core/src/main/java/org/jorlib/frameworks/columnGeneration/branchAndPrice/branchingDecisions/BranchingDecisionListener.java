package org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions;

/**
 * Created by jkinable on 4/23/15.
 */
public interface BranchingDecisionListener {
    public void branchingDecisionPerformed(BranchingDecision bd);
    public void branchingDecisionRewinded(BranchingDecision bd);
}
