package geoxam.algorithms.exact.columnGeneration.branchAndPrice.branchingDecisions;

import java.util.EnumMap;
import java.util.List;

import geoxam.algorithms.exact.columnGeneration.pricing.PricingAlgorithm;
import geoxam.algorithms.exact.columnGeneration.pricing.PricingProblem;
import geoxam.model.Exam;
import geoxam.model.Room;

/**
 * Prevent an exam from using a particular room.
 * @author jkinable
 *
 */
public class RemoveRoom implements BranchingDecision {

	private final EnumMap<PricingSolvers, List<PricingProblem>> pricingProblems;
	private final Exam examForBranching;
	private final Room roomForBranching;
	
	public RemoveRoom(EnumMap<PricingSolvers, List<PricingProblem>> pricingProblems, Exam examForBranching, Room roomForBranching){
		this.pricingProblems=pricingProblems;
		this.examForBranching=examForBranching;
		this.roomForBranching=roomForBranching;
	}
	
	@Override
	public void executeDecision() {
		for(List<PricingProblem> pricingProblemGroup : pricingProblems.values()){
			pricingProblemGroup.get(examForBranching.ID).blockRoom(roomForBranching.ID);
		}
	}
	
	@Override
	public void revertDecision() {
		for(List<PricingProblem> pricingProblemGroup : pricingProblems.values()){
			pricingProblemGroup.get(examForBranching.ID).unblockRoom(roomForBranching.ID);
		}
	}

	@Override
	public String toString(){
		return "RemoveRoom - Exam: "+examForBranching.ID+" Location: "+roomForBranching.ID;
	}
}
