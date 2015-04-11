package geoxam.algorithms.exact.columnGeneration.branchAndPrice.branchingDecisions;

public interface BranchingDecision {

	//Execute the branching decision.
	public void executeDecision();
	
	//Revert the branching decision.
	public void revertDecision();
	
}
