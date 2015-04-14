package org.jorlib.demo.frameworks.columnGeneration.example2.cg;

import org.jorlib.demo.frameworks.columnGeneration.example2.model.MatchingColor;
import org.jorlib.demo.frameworks.columnGeneration.example2.model.TSP;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblem;

public class PricingProblemByColor extends PricingProblem<TSP, Matching, PricingProblemByColor> {
	
	//Color of the matching group. Can be either Red or Blue
	public final MatchingColor color;
	
	public PricingProblemByColor(TSP modelData, String name, MatchingColor color) {
		super(modelData, name);
		this.color=color;
	}

}
