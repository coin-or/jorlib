package org.jorlib.demo.frameworks.columnGeneration.example1.cg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jorlib.demo.frameworks.columnGeneration.example1.model.CuttingStock;
import org.jorlib.frameworks.columnGeneration.colgenMain.ColGen;
import org.jorlib.frameworks.columnGeneration.master.Master;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblem;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemSolver;

public class CuttingStockSolver {
//	T dataModel, 
//	Master<T,V,U> master, 
//	List<V> pricingProblems,
//	List<Class<PricingProblemSolver<T, U, V>>> solvers,
//	double objectiveInitSolution,
//	List<U> initSolution,
//	int upperBound
	
	private final CuttingStock modelData;
	
	public CuttingStockSolver(CuttingStock modelData){
		this.modelData=modelData;
		//Create the master problem
		MasterImpl master=new MasterImpl(modelData);
		//Create the pricing problem
		CuttingStockPricingProblem pricingProblem=new CuttingStockPricingProblem(modelData, "cuttingStockPricing");
		List<CuttingStockPricingProblem> pricingProblems=new ArrayList<>();
		pricingProblems.add(pricingProblem);
		//Define which solvers to use
		//List<Class<PricingProblemSolver<CuttingStock, CuttingPattern, CuttingStockPricingProblem>>> solvers=Arrays.asList(ExactPricingProblemSolver.class);
		List<Class<PricingProblemSolver<CuttingStock, CuttingPattern, CuttingStockPricingProblem>>> solvers=new ArrayList<>();
		//Define an upper bound (stronger is better). In this case we simply sum the demands, i.e. cut each final from its own raw (Rather poor initial solution). 
		int upperBound=IntStream.of(modelData.demandForFinals).sum();
		//Create a set of initial columns.
		List<CuttingPattern> initSolution=this.getInitialSolution(pricingProblem);
		//Create a column generation instance
		ColGen<CuttingStock, CuttingPattern, CuttingStockPricingProblem> cg=new ColGen<CuttingStock, CuttingPattern, CuttingStockPricingProblem>(modelData, master, pricingProblems, solvers, initSolution, upperBound);
	}
	
	/**
	 * Create an initial solution for the Cutting Stock Problem.
	 * Simple initial solution: cut each final from its own raw/roll.
	 * @param pricingProblem
	 * @return Initial solution
	 */
	private List<CuttingPattern> getInitialSolution(CuttingStockPricingProblem pricingProblem){
		List<CuttingPattern> initSolution=new ArrayList<CuttingPattern>();
		for(int i=0; i<modelData.nrFinals; i++){
			int[] pattern=new int[modelData.nrFinals];
			pattern[i]=1;
			CuttingPattern column=new CuttingPattern("initSolution", false, pattern, pricingProblem);
			initSolution.add(column);
		}
		return initSolution;
	}
	public static void main(String[] args){
		CuttingStock cs=new CuttingStock();
		new CuttingStockSolver(cs);
	}
}
