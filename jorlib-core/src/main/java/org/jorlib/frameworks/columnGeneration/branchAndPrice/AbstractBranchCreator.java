package org.jorlib.frameworks.columnGeneration.branchAndPrice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.Inequality;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblem;

public abstract class AbstractBranchCreator<T,U extends AbstractColumn<T,U,V>,V extends AbstractPricingProblem<T,U,V>> {

	protected final T modelData;
	protected final List<V> pricingProblems;

	public AbstractBranchCreator(T modelData, List<V> pricingProblems){
		this.modelData=modelData;
		this.pricingProblems=pricingProblems;
	}

	public List<BAPNode<T,U>> branch(BAPNode<T,U> parentNode, List<U> solution, List<Inequality> cuts){
		//Decide whether we can branch, and if so, on what we can branch.
		if(!this.canPerformBranching(solution))
			return Collections.emptyList();
		return this.getBranches(parentNode, solution, cuts);
	}
	
	protected <B extends BranchingDecision<T,U>> BAPNode<T,U> createBranch(BAPNode<T,U> parentNode, B branchingDecision, List<U> solution, List<Inequality> inequalities){
		int childNodeID= AbstractBranchAndPrice.nodeCounter++;
		List<Integer> rootPath1=new ArrayList<>(parentNode.rootPath);
		rootPath1.add(childNodeID);

		//Copy columns from the parent to the child. The columns need to comply with the Branching Decision. Artificial columns are ignored
		List<U> initSolution= solution.stream().filter(column -> !column.isArtificialColumn && branchingDecision.columnIsCompatibleWithBranchingDecision(column)).collect(Collectors.toList());
		//Copy inequalities to the child node whenever applicable
		List<Inequality> initCuts= inequalities.stream().filter(branchingDecision::inEqualityIsCompatibleWithBranchingDecision).collect(Collectors.toList());


		List<BranchingDecision> branchingDecisions=new ArrayList<>(parentNode.branchingDecisions);
		branchingDecisions.add(branchingDecision);
		return new BAPNode<>(childNodeID, rootPath1, initSolution, initCuts, parentNode.bound, branchingDecisions);
	}
	
	protected abstract boolean canPerformBranching(List<U> solution);
	protected abstract List<BAPNode<T,U>> getBranches(BAPNode<T,U> parentNode, List<U> solution, List<Inequality> cuts);
}
