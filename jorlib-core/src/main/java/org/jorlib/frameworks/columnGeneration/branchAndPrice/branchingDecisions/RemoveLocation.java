package geoxam.algorithms.exact.columnGeneration.branchAndPrice.branchingDecisions;

import java.util.EnumMap;
import java.util.List;

import geoxam.algorithms.exact.columnGeneration.pricing.PricingAlgorithm;
import geoxam.algorithms.exact.columnGeneration.pricing.PricingProblem;
import geoxam.model.Exam;
import geoxam.model.Room;
import geoxam.model.Site;

/**
 * Prevent an exam from using a particular room.
 * @author jkinable
 *
 */
public class RemoveLocation implements BranchingDecision {

	private final EnumMap<PricingSolvers, List<PricingProblem>> pricingProblems;
	private final Exam examForBranching;
	private final Site locationForBranching;
	
	public RemoveLocation(EnumMap<PricingSolvers, List<PricingProblem>> pricingProblems, Exam examForBranching, Site locationForBranching){
		this.pricingProblems=pricingProblems;
		this.examForBranching=examForBranching;
		this.locationForBranching=locationForBranching;
	}
	
	@Override
	public void executeDecision() {
		for(List<PricingProblem> pricingProblemGroup : pricingProblems.values()){
			for(Room r : locationForBranching.rooms)
				pricingProblemGroup.get(examForBranching.ID).blockRoom(r.ID);
		}
	}
	
	//@todo THIS IS DANGERIOUS, SOME ROOMS MAY NEED TO REMAIN BLOCKED DUE TO BRANCHING DECISIONS HIGHER UP IN THE TREE
	@Override
	public void revertDecision() {
		for(List<PricingProblem> pricingProblemGroup : pricingProblems.values()){
			for(Room r : locationForBranching.rooms)
				pricingProblemGroup.get(examForBranching.ID).unblockRoom(r.ID);
		}
	}

	@Override
	public String toString(){
		return "RemoveLocation - Exam: "+examForBranching.ID+" Location: "+locationForBranching.ID;
	}
}
