package org.jorlib.demo.frameworks.columnGeneration.example1.cg;

import org.jorlib.demo.frameworks.columnGeneration.example1.model.CuttingStock;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblem;

public class CuttingStockPricingProblem extends PricingProblem<CuttingStock, CuttingPattern> {

	public CuttingStockPricingProblem(CuttingStock modelData, String name) {
		super(modelData, name);
	}

}
