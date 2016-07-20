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
 * GraphManipulator.java
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

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;

import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecisionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class modifies the data structures according to the branching decisions. A branching decision modifies the master problem and or pricing problems. This class
 * performs these changes. Whenever a backtrack occurs in the tree, all changes are reverted.
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class GraphManipulator {

	/** Logger for this class **/
	protected final Logger logger = LoggerFactory.getLogger(GraphManipulator.class);

	/** The previous node that has been solved. **/
	private BAPNode previousNode;
	/** Set of listeners which should be informed about the Branching Decisions which were made **/
	private final Set<BranchingDecisionListener> listeners;

	/**
	 * This stack keeps track of all the branching decisions that lead from the root node of the BAP tree to the last node for which
	 * this.next(BAPNode<?,?> nextNode) has been invoked.
	 */
	private Stack<BranchingDecision> changeHistory;
	
	public GraphManipulator(BAPNode rootNode){
		this.previousNode=rootNode;
		changeHistory=new Stack<>();
		listeners=new LinkedHashSet<>();
	}
	
	/**
	 * Prepares the data structures for the next node to be solved.
	 * @param nextNode The next node to be solved
	 */
	public void next(BAPNode<?,?> nextNode){
		logger.trace("Previous node: {}, history: {}", previousNode.nodeID, previousNode.rootPath);
		logger.trace("Next node: {}, history: {}, nrBranchingDec: {}", nextNode.nodeID, nextNode.rootPath);
		
		//1. Revert state of the data structures back to the first mutual ancestor of <previousNode> and <nextNode>
		//1a. Find the number of mutual ancestors.
		int mutualNodesOnPath=0;
		for(int i=0; i<Math.min(previousNode.rootPath.size(), nextNode.rootPath.size()); i++){
			if(previousNode.rootPath.get(i) != nextNode.rootPath.get(i))
				break;
			mutualNodesOnPath++;
		}
		logger.trace("number of mutualNodesOnPath: {}", mutualNodesOnPath);
		
		//1b. revert until the first mutual ancestor
		while(changeHistory.size() > mutualNodesOnPath-1){
			logger.trace("Reverting 1 branch lvl");
			BranchingDecision bd=changeHistory.pop();
			//Revert the branching decision!
			this.rewindBranchingDecision(bd);
		}
		// 2. Modify the data structures by performing the branching decisions which lead from the first mutual ancestor to the nextNode.
		// The Branching Decisions are stored in the changeHistory
		logger.trace("Next node nrBranchingDec: {}, changeHist.size: {}", nextNode.branchingDecisions.size(), changeHistory.size());
		for(int i=changeHistory.size(); i< nextNode.branchingDecisions.size(); i++){
			//Get the next branching decision and add it to the changeHistory
			BranchingDecision bd=nextNode.branchingDecisions.get(i);
			changeHistory.add(bd);
			//Execute the decision
			logger.trace("BAP exec branchingDecision: {}", bd);
			this.performBranchingDecision(bd);
		}
		this.previousNode=nextNode;
	}
	
	/**
	 * Revert all currently active branching decisions, thereby restoring all data structures to their original state (i.e the state they were in at the root node)
	 */
	public void restore(){
		while(!changeHistory.isEmpty()){
			BranchingDecision bd = changeHistory.pop();
			this.rewindBranchingDecision(bd);
		}
	}

	/**
	 * Add a BranchingDecisionListener
	 * @param listener listener
	 */
	protected void addBranchingDecisionListener(BranchingDecisionListener listener){
		listeners.add(listener);
	}

	/**
	 * Remove a BranchingDecisionListener
	 * @param listener listener
	 */
	protected void removeBranchingDecisionListener(BranchingDecisionListener listener){	listeners.remove(listener);	}

	/**
	 * Inform the listeners that a branching decision has been executed
	 * @param bd branching decision
	 */
	private void performBranchingDecision(BranchingDecision bd){
		for(BranchingDecisionListener listener : listeners)
			listener.branchingDecisionPerformed(bd);
	}

	/**
	 * Inform the listeners that a branching decision has been reversed due to backtracking
	 * @param bd branching decision
	 */
	private void rewindBranchingDecision(BranchingDecision bd){
		for(BranchingDecisionListener listener : listeners)
			listener.branchingDecisionReversed(bd);
	}
}
