package org.jorlib.frameworks.columnGeneration.branchAndPrice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.Inequality;

public abstract class BranchCreator<T,U extends AbstractColumn<T,U,?>> {

	public List<BAPNode<T,U>> branch(List<U> solution, List<Inequality> cuts){
		//1. Decide whether we can branch, and if so, on what we can branch. 
		if(!this.canPerformBranching())
			return Collections.emptyList();
		List<BAPNode<T,U>> branches=this.getBranches(solution, cuts);
		return branches;
	}
	
	public <B extends BranchingDecision> BAPNode<T,U> createBranch(BAPNode<T,U> parentNode, B branchingDecision, List<U> solution, List<Inequality> inequalities){
		int childNodeID=BranchAndPrice.nodeCounter++;
		List<Integer> rootPath1=new ArrayList<Integer>(parentNode.rootPath);
		rootPath1.add(childNodeID);
		//Copy columns from the parent to the child. The columns need to comply with the Branching Decision. Artificial columns are ignored
		List<U> initSolution=new ArrayList<U>();
		for(U column : parentNode.columns)
			if(!column.isArtificialColumn &&  this.columnIsCompatibleWithBranchingDecision(branchingDecision, column))
				initSolution.add(column);
		//Copy inequalities to the child node whenever applicable
		List<Inequality> initCuts=new ArrayList<>();
		for(Inequality inequality : parentNode.inequalities){
			if(this.inEqualityIsCompatibleWithBranchingDecision(branchingDecision, inequality))
				initCuts.add(inequality);
		}
		
		List<BranchingDecision> branchingDecisions=new ArrayList<>(parentNode.branchingDecisions);
		branchingDecisions.add(branchingDecision);
		BAPNode<T,U> childNode=new BAPNode<T,U>(childNodeID, rootPath1, initSolution, initCuts, parentNode.bound, branchingDecisions);
		return childNode;
	}
	
	protected abstract boolean canPerformBranching();
	protected abstract List<BAPNode<T,U>> getBranches(List<U> solution, List<Inequality> cuts);
	
	/**
	 * Determine whether a particular column from the parent node is feasible for the child node resulting from the Branching Decision
	 * and hence can be transferred.
	 * 
	 * @param branchingDecision
	 * @param column
	 * @return true if the column is feasible, false otherwise
	 */
	protected abstract <B extends BranchingDecision> boolean columnIsCompatibleWithBranchingDecision(B branchingDecision, U column);
	
	/**
	 * Determine whether a particular inequality from the parent node is feasible for the child node resulting from the Branching Decision
	 * and hence can be transferred.
	 * 
	 * @param branchingDecision
	 * @param inequality
	 * @return true if the inequality is feasible, false otherwise
	 */
	protected abstract <B extends BranchingDecision> boolean inEqualityIsCompatibleWithBranchingDecision(B branchingDecision, Inequality inequality);
	
	//==============================================================================
//	Object[] o={examForBranching.ID, roomForBranching.ID, fractionalRoomValue};
//	logger.debug("Branching on Exam: {}, room: {}, value: {}",o);
//	
//	//2. Branch on Exam/Room pair. This involves creating two BAP nodes
//	
//	//2a. Branch 1: enforce that the exam uses that particular room
//	BranchingDecision bd1=new FixRoom(pricingProblems, examForBranching, roomForBranching);
//	int nodeID1=nodeCounter++;
//	List<Integer> rootPath1=new ArrayList<Integer>(parentNode.rootPath);
//	rootPath1.add(nodeID1);
//	List<List<Column>> initSolution1=new ArrayList<List<Column>>();
//	for(Exam e: geoxam.exams){
//		List<Column> schedulesForExam=new ArrayList<Column>();
//		if(e!=examForBranching){ //Copy all schedules, except the artificial ones.
//			for(Column es: solution.get(e.ID)){
//				if(!es.isArtificialColumn){
//					schedulesForExam.add(es);
//				}
//			}
//		}else{ //only copy schedules containing roomForBranching
//			for(Column es: solution.get(e.ID)){
//				if(es.roomsUsed.contains(roomForBranching) && !es.isArtificialColumn){
//					schedulesForExam.add(es);
//				}
//			}
//		}
//		initSolution1.add(schedulesForExam);
//	}
//	//Copy inequalities from parent node
//	List<Inequality> inequalities1=new ArrayList<Inequality>(inequalities); //All inequalities from the parent are valid in this node
//	BAPNode node1=new BAPNode(nodeID1, rootPath1, initSolution1, inequalities1, parentNode.bound);
//	node1.branchingDecisions.addAll(parentNode.branchingDecisions);
//	node1.branchingDecisions.add(bd1);
//	
//	//2b. Branch 2: room removed
//	BranchingDecision bd2=new RemoveRoom(pricingProblems, examForBranching, roomForBranching);
//	int nodeID2=nodeCounter++;
//	List<Integer> rootPath2=new ArrayList<Integer>(parentNode.rootPath);
//	rootPath2.add(nodeID2);
//	//Copy columns from parent node
//	List<List<Column>> initSolution2=new ArrayList<List<Column>>();
//	for(Exam e: geoxam.exams){
//		List<Column> schedulesForExam=new ArrayList<Column>();
//		if(e!=examForBranching){ //Copy schedules except artificial ones
//			for(Column es: solution.get(e.ID)){
//				if(!es.isArtificialColumn){
//					schedulesForExam.add(es);
//				}
//			}
//		}else{ //Copy matchings which do not contain roomForBranching. Also artificial columns are removed
//			for(Column es: solution.get(e.ID)){
//				if(!es.roomsUsed.contains(roomForBranching)  && !es.isArtificialColumn){
//					schedulesForExam.add(es);
//				}
//			}
//		}
//		initSolution2.add(schedulesForExam);
//	}
//	//Copy inequalities from parent node
//	List<Inequality> inequalities2=new ArrayList<Inequality>();
//	for(Inequality inequality : inequalities){
//		switch (inequality.type) {
//		case COVERINEQUALITY:
//			CoverInequality coverInequality=(CoverInequality)inequality;
//			if(coverInequality.room != roomForBranching)
//				inequalities2.add(inequality);
//			break;
//		case LIFTEDCOVERINEQUALITY:
//			LiftedCoverInequality liftedCoverInequality=(LiftedCoverInequality)inequality;
//			if(liftedCoverInequality.room != roomForBranching)
//				inequalities2.add(inequality);
//			break;
//		default:
//			break;
//		}
//	}
//	//Create child node 2
//	BAPNode node2=new BAPNode(nodeID2, rootPath2, initSolution2, inequalities2, parentNode.bound);
//	node2.branchingDecisions.addAll(parentNode.branchingDecisions);
//	node2.branchingDecisions.add(bd2);
//	
//	//Add both nodes to the stack
//	//1. BFS;
////	stack.add(node1);
////	stack.add(node2);
//	//2. DFS (put the most constraint node, node 1 on top. This node will be explorered first):
//	stack.push(node2);
//	stack.push(node1);
//	
//	logger.debug("Finished branching. Stack size: {}",stack.size());
//	
//	return true;
}
