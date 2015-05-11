package org.jorlib.frameworks.columnGeneration.branchAndPrice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.Inequality;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblem;

public abstract class AbstractBranchCreator<T,U extends AbstractColumn<T,U,V>,V extends AbstractPricingProblem<T>> {

	protected final T modelData;
	protected final List<V> pricingProblems;
	protected AbstractBranchAndPrice bap=null;

	public AbstractBranchCreator(T modelData, List<V> pricingProblems){
		this.modelData=modelData;
		this.pricingProblems=pricingProblems;
	}

	/**
	 * Registers the branch and price Problem for which this class creates branches
	 * @param bap
	 */
	protected void registerBAP(AbstractBranchAndPrice bap){
		if(this.bap != null)
			throw new RuntimeException("This class can only be associated with a Branch and Price problem once!");
		this.bap=bap;
	}

	public List<BAPNode<T,U>> branch(BAPNode<T,U> parentNode, List<U> solution, List<Inequality> cuts){
		//1. Decide whether we can branch, and if so, on what we can branch. 
		if(!this.canPerformBranching(solution))
			return Collections.emptyList();
		List<BAPNode<T,U>> branches=this.getBranches(parentNode, solution, cuts);
		return branches;
	}
	
	protected <B extends BranchingDecision> BAPNode<T,U> createBranch(BAPNode<T,U> parentNode, B branchingDecision, List<U> solution, List<Inequality> inequalities){
		//int childNodeID= AbstractBranchAndPrice.nodeCounter++;
		int childNodeID= bap.getUniqueNodeID();
		List<Integer> rootPath1=new ArrayList<Integer>(parentNode.rootPath);
		rootPath1.add(childNodeID);
		//Copy columns from the parent to the child. The columns need to comply with the Branching Decision. Artificial columns are ignored
		List<U> initSolution=new ArrayList<U>();
		for(U column : solution)
			if(!column.isArtificialColumn &&  branchingDecision.columnIsCompatibleWithBranchingDecision(column))
				initSolution.add(column);
		//Copy inequalities to the child node whenever applicable
		List<Inequality> initCuts=new ArrayList<>();
		for(Inequality inequality : inequalities){
			if(branchingDecision.inEqualityIsCompatibleWithBranchingDecision(inequality))
				initCuts.add(inequality);
		}
		
		List<BranchingDecision> branchingDecisions=new ArrayList<>(parentNode.branchingDecisions);
		branchingDecisions.add(branchingDecision);
		BAPNode<T,U> childNode=new BAPNode<T,U>(childNodeID, rootPath1, initSolution, initCuts, parentNode.bound, branchingDecisions);
		return childNode;
	}
	
	protected abstract boolean canPerformBranching(List<U> solution);
	protected abstract List<BAPNode<T,U>> getBranches(BAPNode<T,U> parentNode, List<U> solution, List<Inequality> cuts);

}
