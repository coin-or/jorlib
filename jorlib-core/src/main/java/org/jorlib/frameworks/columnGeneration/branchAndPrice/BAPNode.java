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
	
	
	public String toString(){
		return "BAP node: "+nodeID;
	}
}
