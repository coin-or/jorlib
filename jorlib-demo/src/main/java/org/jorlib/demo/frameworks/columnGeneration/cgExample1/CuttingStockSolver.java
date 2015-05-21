/* ==========================================
 * jORLib : a free Java OR library
 * ==========================================
 *
 * Project Info:  https://github.com/jkinable/jorlib
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under GPLv3
 *
 */
/* -----------------
 * CuttingStockSolver.java
 * -----------------
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * Original Author:  Joris Kinable
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 *
 */
package org.jorlib.demo.frameworks.columnGeneration.cgExample1;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.jorlib.demo.frameworks.columnGeneration.cgExample1.cg.CuttingPattern;
import org.jorlib.demo.frameworks.columnGeneration.cgExample1.cg.ExactPricingProblemSolver;
import org.jorlib.demo.frameworks.columnGeneration.cgExample1.cg.Master;
import org.jorlib.demo.frameworks.columnGeneration.cgExample1.cg.PricingProblem;
import org.jorlib.demo.frameworks.columnGeneration.cgExample1.model.CuttingStock;
import org.jorlib.frameworks.columnGeneration.colgenMain.ColGen;
import org.jorlib.frameworks.columnGeneration.io.SimpleCGLogger;
import org.jorlib.frameworks.columnGeneration.io.SimpleDebugger;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblemSolver;

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

		//Create the pricing problem
		PricingProblem pricingProblem=new PricingProblem(modelData, "cuttingStockPricing");

		//Create the master problem
		Master master=new Master(modelData, pricingProblem);

		//Define which solvers to use
		List<Class<? extends AbstractPricingProblemSolver<CuttingStock, CuttingPattern, PricingProblem>>> solvers= Collections.singletonList(ExactPricingProblemSolver.class);

		//Define an upper bound (stronger is better). In this case we simply sum the demands, i.e. cut each final from its own raw (Rather poor initial solution).
		int upperBound=IntStream.of(modelData.demandForFinals).sum();

		//Create a set of initial columns.
		List<CuttingPattern> initSolution=this.getInitialSolution(pricingProblem);

		//Create a column generation instance
		ColGen<CuttingStock, CuttingPattern, PricingProblem> cg=new ColGen<>(modelData, master, pricingProblem, solvers, initSolution, upperBound);

		//OPTIONAL: add a debugger
		SimpleDebugger debugger=new SimpleDebugger(cg);

		//OPTIONAL: add a logger
		SimpleCGLogger logger=new SimpleCGLogger(cg, new File("./output/cuttingStock.log"));

		//Solve the problem through column generation
		try {
			cg.solve(System.currentTimeMillis()+1000L);
		} catch (TimeLimitExceededException e) {
			e.printStackTrace();
		}
		//Print solution:
		System.out.println("================ Solution ================");
		List<CuttingPattern> solution=cg.getSolution();
		System.out.println("CG terminated with objective: "+cg.getObjective());
		System.out.println("Number of iterations: "+cg.getNumberOfIterations());
		System.out.println("Time spent on master: "+cg.getMasterSolveTime()+" time spent on pricing: "+cg.getPricingSolveTime());
		System.out.println("Columns (only non-zero columns are returned):");
		for(CuttingPattern column : solution)
			System.out.println(column);
		
		//Clean up:
		cg.close(); //This closes both the master and pricing problems
	}
	
	/**
	 * Create an initial solution for the Cutting Stock Problem.
	 * Simple initial solution: cut each final from its own raw/roll.
	 * @param pricingProblem pricing problem
	 * @return Initial solution
	 */
	private List<CuttingPattern> getInitialSolution(PricingProblem pricingProblem){
		List<CuttingPattern> initSolution=new ArrayList<>();
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
