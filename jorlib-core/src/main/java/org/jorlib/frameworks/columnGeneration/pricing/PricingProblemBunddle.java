package org.jorlib.frameworks.columnGeneration.pricing;

import java.util.ArrayList;
import java.util.List;

import org.jorlib.frameworks.columnGeneration.colgenMain.Column;

/**
 * Each pricing problem (PricingProblem) is solved by some algorithm (PricingProblemSolver). This class is a container which holds
 * a single instance of a PricingProblemSolver for *each* PricingProblem. 
 * @author jkinable
 *
 */
public final class PricingProblemBunddle<T, U extends Column, V extends PricingProblem<T, U>> {
	public final Class<? extends PricingProblemSolver<T, U, V>> pricingSolver;
	public final List<PricingProblemSolver<T, U, V>> solverInstances;
	
	public PricingProblemBunddle(Class<? extends PricingProblemSolver<T, U, V>> pricingSolver, 
									List<V> pricingProblems, 
									PricingProblemSolverFactory<T, U, V> solverFactory){
		this.pricingSolver=pricingSolver;
		//Create the solver instances
		solverInstances=new ArrayList<>(pricingProblems.size());
		for(V pricingProblem : pricingProblems){
			solverInstances.add(solverFactory.createSolverInstance(pricingProblem));
		}
	}
}
