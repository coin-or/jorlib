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
 * AbstractBranchCreator.java
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
package org.jorlib.frameworks.columnGeneration.branchAndPrice;


import java.util.List;

import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.AbstractInequality;

/**
 * Class which models a single node in the Branch-and-Price tree
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class BAPNode<T,U extends AbstractColumn<T, ?>> {

	/** Unique node ID **/
	public final int nodeID;

	/** Sequence of the IDs of the nodes encountered while walking from the root of the BAP tree to this node. rootPath[0]=0, rootPath[last(rootPath)]=this.nodeID **/
	protected final List<Integer> rootPath;
	/** List of branching decisions that lead to this node. **/
	protected final List<BranchingDecision> branchingDecisions;
	/** Columns used to initialize the master problem **/
	protected final List<U> columns;
	/** Valid inequalities used to initialize the master problem of this node **/
	protected final List<AbstractInequality> inequalities;
	/** Bound on the optimum solution of this node. If the bound of this node exceeds the best incumbent int solution, this node will be pruned. **/
	protected double bound;

	/**
	 * Creates a new BAPNode
	 * @param nodeID ID of the Node
	 * @param rootPath Sequence of the IDs of the nodes encountered while walking from the root of the BAP tree to this node. rootPath[0]=0, rootPath[last(rootPath)]=this.nodeID
	 * @param columns Columns used to initialize the master problem
	 * @param inequalities Valid inequalities used to initialize the master problem of this node
	 * @param bound Bound on the optimum solution of this node. If the bound of this node exceeds the best incumbent int solution, this node will be pruned. The bound may be inherited from the parent.
	 * @param branchingDecisions List of branching decisions that lead to this node.
	 */
	public BAPNode(int nodeID, List<Integer> rootPath, List<U> columns, List<AbstractInequality> inequalities, double bound, List<BranchingDecision> branchingDecisions){
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
	public int getParentID(){
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
