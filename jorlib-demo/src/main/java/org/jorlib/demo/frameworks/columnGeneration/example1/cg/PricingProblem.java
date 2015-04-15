package org.jorlib.demo.frameworks.columnGeneration.example1.cg;

import org.jorlib.demo.frameworks.columnGeneration.example1.model.CuttingStock;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblem;

/**
 * Definition of the pricing problem. Since there's only 1 pricing problem in the cutting stock,
 * we can simply extend the pricing problem included in the framework with no further modifications.
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 */
public class PricingProblem extends AbstractPricingProblem<CuttingStock, CuttingPattern, PricingProblem> {

	public PricingProblem(CuttingStock modelData, String name) {
		super(modelData, name);
	}

}
