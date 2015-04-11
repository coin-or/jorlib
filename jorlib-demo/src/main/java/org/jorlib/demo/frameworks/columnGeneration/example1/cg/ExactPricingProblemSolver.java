package org.jorlib.demo.frameworks.columnGeneration.example1.cg;

import org.jorlib.demo.frameworks.columnGeneration.example1.model.CuttingStock;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemSolver;

public class ExactPricingProblemSolver extends PricingProblemSolver<CuttingStock, CuttingPattern, PricingProblemImpl> {

	public ExactPricingProblemSolver(CuttingStock dataModel, 
			PricingProblemImpl pricingProblem) {
		super(dataModel, "ExactSolver", pricingProblem);
	}

	@Override
	protected void solve() throws TimeLimitExceededException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getObjective() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean pricingProblemIsFeasible() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
