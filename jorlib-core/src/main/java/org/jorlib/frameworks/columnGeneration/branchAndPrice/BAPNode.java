package org.jorlib.frameworks.columnGeneration.branchAndPrice;


import java.util.List;

import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.Inequality;

public class BAPNode<T,U extends AbstractColumn<T,U,?>> {
	public final int nodeID;
	
	
	protected final List<Integer> rootPath; //Sequence of nodeIDs from the root to this node. rootPath[0]=0, rootPath[last(rootPath)]=this.nodeID
	protected final List<BranchingDecision> branchingDecisions; //List of branching decisions that lead to this node.
	protected final List<U> columns; //Columns used to initialize the master problem
	protected final List<Inequality> inequalities; //Valid inequalities used to initialize the master problem
	
	protected double bound; //Best bound on the optimum solution. If best incumbent int solution has a smaller objective than this bound, this node will be pruned.
	
	
	public BAPNode(int nodeID, List<Integer> rootPath, List<U> columns, List<Inequality> inequalities, double bound, List<BranchingDecision> branchingDecisions){
		this.nodeID=nodeID;
		this.columns=columns;
		this.inequalities=inequalities;
		this.branchingDecisions=branchingDecisions;
		this.rootPath=rootPath;
		this.bound=bound;
	}

	/**
	 * Returns the ID of its parent in the Branch-and-Price tree. For memory efficiency, no pointer to the ancestor is returned. As maintaining a link to every
	 * parent may be expensive.
	 * @return ID of parent node, or -1 if this is the root node
	 */
	public int getAncestorID(){
		if(nodeID == 0)
			return -1;
		else
			return rootPath.get(rootPath.size()-2);
	}

	/**
	 * Returns the branching decision that produced this particular node.
	 * @return The branching decision that links this node to its parent, or null if this node is the root node
	 */
	public BranchingDecision getBranchingDecision(){
		if(nodeID == 0)
			return null;
		else
			return branchingDecisions.get(branchingDecisions.size()-1);
	}

	public String toString(){
		return "BAP node: "+nodeID;
	}
}
