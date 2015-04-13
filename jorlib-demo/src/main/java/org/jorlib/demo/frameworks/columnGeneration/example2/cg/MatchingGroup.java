package org.jorlib.demo.frameworks.columnGeneration.example2.cg;

import org.jorlib.demo.frameworks.columnGeneration.example2.model.Color;
import org.jorlib.demo.frameworks.columnGeneration.example2.model.TSP;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblem;

public class MatchingGroup extends PricingProblem<TSP, Matching> {
	
	//Color of the matching group. Can be either Red or Blue
	public final Color color;
	
	public MatchingGroup(TSP modelData, String name, Color color) {
		super(modelData, name);
		this.color=color;
	}

}
