package org.jorlib.demo.frameworks.columnGeneration.example1.cg;

import org.jorlib.demo.frameworks.columnGeneration.example1.model.CuttingStock;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblem;

public class PricingProblemImpl extends PricingProblem<CuttingStock> {

	public PricingProblemImpl(CuttingStock modelData) {
		super(modelData);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getNrColumns() {
		// TODO Auto-generated method stub
		return 0;
	}

}
