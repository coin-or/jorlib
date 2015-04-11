package geoxam.algorithms.exact.columnGeneration.branchAndPrice.branchingDecisions;

import java.util.EnumMap;
import java.util.List;

import geoxam.algorithms.exact.columnGeneration.pricing.PricingAlgorithm;
import geoxam.algorithms.exact.columnGeneration.pricing.PricingProblem;
import geoxam.model.Exam;
import geoxam.model.Room;
import geoxam.model.Site;

/**
 * Ensure that an exam uses a specific room.
 */
public class FixLocation implements BranchingDecision {
	
	private final EnumMap<PricingSolvers, List<PricingProblem>> pricingProblems;
	
	private final Exam examForBranching;
	private final Site locationForBranching;
	
	public FixLocation(EnumMap<PricingSolvers, List<PricingProblem>> pricingProblems, Exam examForBranching, Site locationForBranching){
		this.pricingProblems=pricingProblems;
		this.examForBranching=examForBranching;
		this.locationForBranching=locationForBranching;
	}

	@Override
	public void executeDecision() {
		for(List<PricingProblem> pricingProblemGroup : pricingProblems.values()){
			pricingProblemGroup.get(examForBranching.ID).enforceLocation(locationForBranching);
		}
	}
	
	@Override
	public void revertDecision() {
		for(List<PricingProblem> pricingProblemGroup : pricingProblems.values()){
			pricingProblemGroup.get(examForBranching.ID).unenforceLocation(locationForBranching);
		}
	}

	@Override
	public String toString(){
		return "FixLocation - Exam: "+examForBranching.ID+" Location: "+locationForBranching.ID;
	}
}
