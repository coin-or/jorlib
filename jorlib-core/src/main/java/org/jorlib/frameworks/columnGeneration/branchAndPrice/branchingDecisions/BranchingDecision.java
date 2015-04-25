package org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions;

import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.Inequality;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblem;

public interface BranchingDecision<T,U extends AbstractColumn<T,U,? extends AbstractPricingProblem>> {

	//Execute the branching decision.
	public void executeDecision();
	
	//Revert the branching decision.
	public void rewindDecision();

	/**
	 * Determine whether a particular column from the parent node is feasible for the child node resulting from the Branching Decision
	 * and hence can be transferred.
	 *
	 * @param column
	 * @return true if the column is feasible, false otherwise
	 */
	public boolean columnIsCompatibleWithBranchingDecision(U column);

	/**
	 * Determine whether a particular inequality from the parent node is feasible for the child node resulting from the Branching Decision
	 * and hence can be transferred.
	 *
	 * @param inequality
	 * @return true if the inequality is feasible, false otherwise
	 */
	public boolean inEqualityIsCompatibleWithBranchingDecision(Inequality inequality);
	
}
