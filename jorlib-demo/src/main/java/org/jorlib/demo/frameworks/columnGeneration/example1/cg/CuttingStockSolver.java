package org.jorlib.demo.frameworks.columnGeneration.example1.cg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jorlib.demo.frameworks.columnGeneration.example1.model.CuttingStock;
import org.jorlib.frameworks.columnGeneration.colgenMain.ColGen;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.master.Master;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblem;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemSolver;

/**
 * Simple solver class which solves the Cutting Stock Problem through Column Generation.
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 */
public class CuttingStockSolver {

	private final CuttingStock modelData;
	
	public CuttingStockSolver(CuttingStock modelData){
		this.modelData=modelData;
		//Create the master problem
		MasterImpl master=new MasterImpl(modelData);
		//Create the pricing problem
		CuttingStockPricingProblem pricingProblem=new CuttingStockPricingProblem(modelData, "cuttingStockPricing");
		//Define which solvers to use
		List<Class<? extends PricingProblemSolver<CuttingStock, CuttingPattern, CuttingStockPricingProblem>>> solvers=Arrays.asList(ExactPricingProblemSolver.class);
		//Define an upper bound (stronger is better). In this case we simply sum the demands, i.e. cut each final from its own raw (Rather poor initial solution). 
		int upperBound=IntStream.of(modelData.demandForFinals).sum();
		//Create a set of initial columns.
		List<CuttingPattern> initSolution=this.getInitialSolution(pricingProblem);
		//Create a column generation instance
		ColGen<CuttingStock, CuttingPattern, CuttingStockPricingProblem> cg=new ColGen<CuttingStock, CuttingPattern, CuttingStockPricingProblem>(modelData, master, pricingProblem, solvers, initSolution, upperBound);
		
		//Solve the problem through column generation
		try {
			cg.solve(System.currentTimeMillis()+1000L);
		} catch (TimeLimitExceededException e) {
			e.printStackTrace();
		}
		//Print solution:
		List<CuttingPattern> solution=cg.getSolution();
		System.out.println("CG terminated with objective: "+cg.getObjective());
		System.out.println("Number of iterations: "+cg.getNumberOfIterations());
		System.out.println("Time spent on master: "+cg.getMasterSolveTime()+" time spent on pricing: "+cg.getPricingSolveTime());
		System.out.println("Columns (only non-zero columns are returned):");
		for(CuttingPattern column : solution)
			System.out.println(column);
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
