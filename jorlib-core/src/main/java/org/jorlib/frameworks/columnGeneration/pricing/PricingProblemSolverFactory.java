package org.jorlib.frameworks.columnGeneration.pricing;

import org.jorlib.frameworks.columnGeneration.colgenMain.Column;

public interface PricingProblemSolverFactory<T,U extends Column<T,U>, V extends PricingProblem<T, U>> {
	public PricingProblemSolver<T, U, V> createSolverInstance(V pricingProblem);
}
