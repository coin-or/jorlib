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


import java.util.ArrayList;
import java.util.Collections;
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

	//Data before solving the node:

	/** Unique node ID **/
	public final int nodeID;
	/** Sequence of the IDs of the nodes encountered while walking from the root of the BAP tree to this node. rootPath[0]=0, rootPath[last(rootPath)]=this.nodeID **/
	protected final List<Integer> rootPath;
	/** List of branching decisions that lead to this node. **/
	protected final List<BranchingDecision> branchingDecisions;
	/** Columns used to initialize the master problem **/
	protected final List<U> initialColumns;
	/** Valid inequalities used to initialize the master problem of this node **/
	protected final List<AbstractInequality> initialInequalities;


	//Data after solving the node:
	/** Objective value of the master problem after solving this node. **/
	protected double objective;
	/** Bound on the optimum solution of this node. If the bound of this node exceeds the best incumbent int solution, this node will be pruned.
	 * If this node is solved to optimality, this.objective and this.bound must be equal **/
	protected double bound;
	/** List of columns constituting the solution after solving this node; Typically, only non-zero columns are stored **/
	protected List<U> solution;
	/** List of inequalities in the master problem after solving this node **/
	protected List<AbstractInequality> inequalities;

	/**
	 * Creates a new BAPNode
	 * @param nodeID ID of the Node
	 * @param rootPath Sequence of the IDs of the nodes encountered while walking from the root of the BAP tree to this node. rootPath[0]=0, rootPath[last(rootPath)]=this.nodeID
	 * @param initialColumns Columns used to initialize the master problem
	 * @param initialInequalities Valid inequalities used to initialize the master problem of this node
	 * @param bound Bound on the optimum solution of this node. If the bound of this node exceeds the best incumbent integer solution, this node will be pruned. The bound may be inherited from the parent.
	 * @param branchingDecisions List of branching decisions that lead to this node.
	 */
	public BAPNode(int nodeID, List<Integer> rootPath, List<U> initialColumns, List<AbstractInequality> initialInequalities, double bound, List<BranchingDecision> branchingDecisions){
		this.nodeID=nodeID;
		this.initialColumns = initialColumns;
		this.initialInequalities = initialInequalities;
		this.branchingDecisions=branchingDecisions;
		this.rootPath=rootPath;
		this.bound=bound;
		this.solution=new ArrayList<>();
		this.inequalities =new ArrayList<>();
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

	/**
	 * Adds initial columns to this node. When the node is solved, these columns will be added to the master problem.
	 * This method may be invoked multiple times to add additional columns.
	 * @param additionalColumns columns to add to the initial solution.
	 */
	public void addInitialColumns(List<U> additionalColumns){
		initialColumns.addAll(additionalColumns);
	}

	/**
	 * Adds initial inequalities to this node. When the node is solved, these inequalities will be added to the master problem.
	 * This method may be invoked multiple times to add additional inequalities.
	 * @param additionalInequalities columns to add to the initial solution.
	 */
	public void addInitialInequalities(List<AbstractInequality> additionalInequalities){
		inequalities.addAll(additionalInequalities);
	}

	/**
	 * After the node has been solved, this method is used to record the objective of the solution
	 * @param objective solution objective
	 */
	public void setObjective(double objective){
		this.objective=objective;
	}

	/**
	 * After the node has been solved, this method is used to record the bound on the objective value of this node.
	 * @param bound bound on the objective value of this node
	 */
	public void setBound(double bound){
		this.bound=bound;
	}

	/**
	 * After the node has been solved, this method is used to store the solution
	 * @param objective objective value of the node
	 * @param bound bound on the objective value
	 * @param solution columns constituting the solution
	 * @param inequalities inequalities generated while solving this node
	 */
	public void storeSolution(double objective, double bound, List<U> solution, List<AbstractInequality> inequalities){
		this.objective=objective;
		this.bound=bound;
		this.solution=solution;
		this.inequalities=inequalities;
	}

	/**
	 * Returns a set of columns which are used to initialize the master problem when this node is being solved.These columns are usually
	 * inherited from the parent of this node.
	 * @return a set of columns which are used to initialize the master problem when this node is being solved.
	 */
	public List<U> getInitialColumns(){
		return Collections.unmodifiableList(initialColumns);
	}

	/**
	 * Returns a set of inequalities which are used to initialize the master problem when this node is being solved. These inequalities are usually
	 * inherited from the parent of this node.
	 * @return a set of inequalities which are used to initialize the master problem when this node is being solved.
	 */
	public List<AbstractInequality> getInitialInequalities(){
		return Collections.unmodifiableList(initialInequalities);
	}

	/**
	 * Gets the objective value of this node. This method only makes sense after the node has been solved.
	 * @return the objective value of this node.
	 */
	public double getObjective(){
		return objective;
	}

	/**
	 * Gets the bound on the objective value of this node.
	 * @return the bound on the objective value of this node.
	 */
	public double getBound(){
		return bound;
	}

	/**
	 * Returns a list of columns constituting the solution of this node.
	 * @return a list of columns constituting the solution of this node.
	 */
	public List<U> getSolution(){
		return Collections.unmodifiableList(solution);
	}

	/**
	 * Returns a list of inequalities which are in the Master problem after this node has been solved.
	 * @return a list of inequalities which are in the Master problem after this node has been solved.
	 */
	public List<AbstractInequality> getInequalities(){
		return Collections.unmodifiableList(inequalities);
	}

	/**
	 * Returns the depth of the node in the Branch-and-Price tree. The depth of the root node is 0, the depth of its siblings is 1, etc.
	 * @return Depth of node in the Branch-and-Price tree
	 */
	public int getNodeDepth(){
		return rootPath.size();
	}

	/**
	 * Textual description of the node.
	 * @return Textual description of the node.
	 */
	public String toString(){
		return "BAP node: "+nodeID;
	}
}
