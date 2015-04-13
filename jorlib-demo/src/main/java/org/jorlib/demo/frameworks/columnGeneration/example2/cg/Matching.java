package org.jorlib.demo.frameworks.columnGeneration.example2.cg;

import java.util.Arrays;
import java.util.Set;

import org.jorlib.demo.frameworks.columnGeneration.example2.model.Edge;
import org.jorlib.demo.frameworks.columnGeneration.example2.model.TSP;
import org.jorlib.frameworks.columnGeneration.colgenMain.Column;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblem;

public class Matching extends Column<TSP, Matching, MatchingGroup> {

	public Matching(String creator, boolean isArtificial,	MatchingGroup associatedPricingProblem,
			Set<Edge> edges,
			int[] succ,
			int cost) {
		super(creator, isArtificial, associatedPricingProblem);
		this.edges=edges;
		this.succ=succ;
		this.cost=cost;
	}

	//Edges in the matching
	public final Set<Edge> edges;
	//successor array. succ[2]=4 and succ[4]=2 models that edge (2,4) is contained in the matching.
	public final int[] succ;
	//Weighted cost of the matching
	public final int cost;
		

	@Override
	public boolean equals(Object o) {
		if(this==o)
			return true;
		else if(!(o instanceof Matching))
			return false;
		Matching other=(Matching) o;			
		return Arrays.equals(this.succ, other.succ);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(succ);
	}

	@Override
	public String toString() {
		String s="Value: "+this.value+" cost: "+this.cost+" edges: "+Arrays.toString(succ);
		return s;
	}

}
