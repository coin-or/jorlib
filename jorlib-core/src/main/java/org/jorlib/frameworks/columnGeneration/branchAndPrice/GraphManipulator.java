package org.jorlib.frameworks.columnGeneration.branchAndPrice;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;

import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecisionListener;
import org.jorlib.frameworks.columnGeneration.master.AbstractMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class modifies the data structures according to the branching decisions. A branching decision modifies the master problem or pricing problems. This class
 * performs these changes. Whenever a backtrack occurs in the tree, all changes are reverted.
 *
 */
public class GraphManipulator {

	protected final Logger logger = LoggerFactory.getLogger(AbstractMaster.class);
	
	private boolean ignoreNextEvent=false; //Reverting an event triggers a new event. If this method invoked the reversal of an invent it shouldn't react on the next event. This to protect against a cascading effect.
	
	private BAPNode previousNode; //The previous node that has been solved.

	private final Set<BranchingDecisionListener> listeners;
	
	/**
	 * This Stack keeps track of all the changes that have been made to the data structures due to the execution of branching decisions.
	 * Each frame on the stack corresponds to all the changes caused by a single branching decision. The number of frames on the stack
	 * equals the depth of <previousNode> in the search tree.
	 */
	private Stack<BranchingDecision> changeHistory;
	
	public GraphManipulator(BAPNode rootNode){
		this.previousNode=rootNode;
//			btsp.addGraphListener(this);
		changeHistory=new Stack<BranchingDecision>();
		listeners=new LinkedHashSet<>();
	}
	
	/**
	 * Prepares the data structures for the next node to be solved.
	 */
	public void next(BAPNode<?,?> nextNode){
		logger.debug("Previous node: {}, history: {}", previousNode.nodeID, previousNode.rootPath);
		logger.debug("Next node: {}, history: {}, nrBranchingDec: {}", nextNode.nodeID, nextNode.rootPath);
		
		//1. Revert state of the data structures back to the first mutual ancestor of <previousNode> and <nextNode>
		//1a. Find the number of mutual ancestors.
		int mutualNodesOnPath=0;
		for(int i=0; i<Math.min(previousNode.rootPath.size(), nextNode.rootPath.size()); i++){
			if(previousNode.rootPath.get(i) != nextNode.rootPath.get(i))
				break;
			mutualNodesOnPath++;
		}
		logger.debug("mutualNodesOnPath: {}", mutualNodesOnPath);
		
		//1b. revert until the first mutual ancestor
		while(changeHistory.size() > mutualNodesOnPath-1){
			logger.debug("Reverting 1 branch lvl");
			//List<RevertibleEvent> changes= changeHistory.pop();
			BranchingDecision bd=changeHistory.pop();
			//Revert the branching decision!
			bd.rewindDecision();
		}
		/* 2. Modify the data structures by performing the branching decisions. Each branch will add a stack to the history.
		 * Each branching decision will trigger a number of modifications to the data structures. These are collected by the stacks.
		 */
		logger.debug("Next node nrBranchingDec: {}, changeHist.size: {}", nextNode.branchingDecisions.size(), changeHistory.size());
		for(int i=changeHistory.size(); i< nextNode.branchingDecisions.size(); i++){
			//Get the next branching decision and add it to the changeHistory
			BranchingDecision bd=nextNode.branchingDecisions.get(i);
			changeHistory.add(bd);
			//Execute the decision
			logger.debug("BAP exec branchingDecision: {}",bd);
			bd.executeDecision();
		}
		this.previousNode=nextNode;
	}
	
	/**
	 * Revert all currently active branching decisions, thereby restoring all data structures to their original state (i.e. the state they were in at the root node)
	 */
	public void restore(){
		while(!changeHistory.isEmpty()){
			BranchingDecision bd = changeHistory.pop();
			bd.rewindDecision();
		}
	}

	protected void addBranchingDecisionListener(BranchingDecisionListener listener){
		listeners.add(listener);
	}
	protected void removeBranchingDecisionListener(BranchingDecisionListener listener){
		listeners.remove(listener);
	}
}
