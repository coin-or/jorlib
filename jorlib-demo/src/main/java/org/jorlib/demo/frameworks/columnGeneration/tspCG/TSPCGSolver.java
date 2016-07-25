/* ==========================================
 * jORLib : a free Java OR library
 * ==========================================
 *
 * Project Info:  https://github.com/jkinable/jorlib
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 *
 */
/* -----------------
 * BAPTSPTest.java
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
package org.jorlib.demo.frameworks.columnGeneration.tspCG;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jorlib.demo.frameworks.columnGeneration.tspCG.cg.ExactPricingProblemSolver;
import org.jorlib.demo.frameworks.columnGeneration.tspCG.cg.Matching;
import org.jorlib.demo.frameworks.columnGeneration.tspCG.cg.PricingProblemByColor;
import org.jorlib.demo.frameworks.columnGeneration.tspCG.cg.master.Master;
import org.jorlib.demo.frameworks.columnGeneration.tspCG.cg.master.TSPMasterData;
import org.jorlib.demo.frameworks.columnGeneration.tspCG.cg.master.cuts.SubtourInequalityGenerator;
import org.jorlib.demo.frameworks.columnGeneration.tspCG.model.MatchingColor;
import org.jorlib.demo.frameworks.columnGeneration.tspCG.model.TSP;
import org.jorlib.frameworks.columnGeneration.colgenMain.ColGen;
import org.jorlib.frameworks.columnGeneration.io.SimpleCGLogger;
import org.jorlib.frameworks.columnGeneration.io.SimpleDebugger;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutHandler;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblemSolver;
import org.jorlib.io.tspLibReader.TSPLibTour;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * A column generation solution to the Traveling Salesman Problem.
 * Each TSP solution on a graph with an even number of vertices can be seen as the union of two edge disjoint perfect matchings.
 * As a result, the TSP problem can be solved by selecting two edge disjoint matchings which comply with the well-known
 * DFJ subtour elimination constraints. The pricing problems amount to generating these matchings.<p>
 * 
 * The ideas in this example are loosely based on the work Kinable, J. "Decomposition approaches for optimization problems, 
 * chapter: "The balanced TSP problem". 2014<p>
 * 
 * Note: this is an example class to demonstrate features of the Column Generation framework. This class is not
 * intended as a high-performance TSP solver!
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public final class TSPCGSolver {

	private final TSP tsp;

	public TSPCGSolver(TSP tsp){
		this.tsp=tsp;
		if(tsp.N % 2 == 1)
			throw new RuntimeException("This solver can only solve TSP instances with an even number of vertices!");

		//Create a cutHandler, then create a Subtour AbstractInequality Generator and add it to the handler
		CutHandler<TSP, TSPMasterData> cutHandler=new CutHandler<>();
		SubtourInequalityGenerator cutGen=new SubtourInequalityGenerator(tsp);
		cutHandler.addCutGenerator(cutGen);

		//Create the two pricing problems
		List<PricingProblemByColor> pricingProblems=new ArrayList<>();
		pricingProblems.add(new PricingProblemByColor(tsp, "redPricing", MatchingColor.RED));
		pricingProblems.add(new PricingProblemByColor(tsp, "bluePricing", MatchingColor.BLUE));

		//Create the master problem
		Master master=new Master(tsp, pricingProblems, cutHandler);
		
		//Define which solvers to use
		List<Class<? extends AbstractPricingProblemSolver<TSP, Matching, PricingProblemByColor>>> solvers= Collections.singletonList(ExactPricingProblemSolver.class);
		
		//Create an initial solution and use it as an upper bound
		TSPLibTour initTour=TSPLibTour.createCanonicalTour(tsp.N); //Feasible solution
		int tourLength=tsp.getTourLength(initTour); //Upper bound (Stronger is better)
		List<Matching> initSolution=this.convertTourToColumns(initTour, pricingProblems); //Create a set of initial columns.

		//Lower bound on solution (stronger is better), e.g. sum of cheapest edge out of every node.
		double lowerBound=0;

		//Create a column generation instance
		ColGen<TSP, Matching, PricingProblemByColor> cg=new ColGen<>(tsp, master, pricingProblems, solvers, initSolution, tourLength, lowerBound);

		//OPTIONAL: add a debugger
		SimpleDebugger debugger=new SimpleDebugger(cg, cutHandler);

		//OPTIONAL: add a logger
		SimpleCGLogger logger=new SimpleCGLogger(cg, new File("./output/tspCG.log"));

		//Solve the problem through column generation
		try {
			cg.solve(System.currentTimeMillis()+1000L);
		} catch (TimeLimitExceededException e) {
			e.printStackTrace();
		}
		//Print solution:
		System.out.println("================ Solution ================");
		List<Matching> solution=cg.getSolution();
		System.out.println("CG terminated with objective: "+cg.getObjective());
		System.out.println("Number of iterations: "+cg.getNumberOfIterations());
		System.out.println("Time spent on master: "+cg.getMasterSolveTime()+" time spent on pricing: "+cg.getPricingSolveTime());
		System.out.println("Columns (only non-zero columns are returned):");
		solution.forEach(System.out::println);

		//Clean up:
		cg.close(); //This closes both the master and pricing problems
	}

	public static void main(String[] args) throws IOException {
		//TSPLib instances, see http://www.iwr.uni-heidelberg.de/groups/comopt/software/TSPLIB95/
		TSP tsp= new TSP("./data/tspLib/tsp/burma14.tsp"); //Optimal: 3323
//		TSP tsp= new TSP("./data/tspLib/tsp/ulysses16.tsp"); //Optimal: 6859
//		TSP tsp= new TSP("./data/tspLib/tsp/ulysses22.tsp"); //Optimal: 7013
//		TSP tsp= new TSP("./data/tspLib/tsp/gr24.tsp"); //Optimal: 1272
//		TSP tsp= new TSP("./data/tspLib/tsp/fri26.tsp"); //Optimal: 937
//		TSP tsp= new TSP("./data/tspLib/tsp/dantzig42.tsp"); //Optimal: 699
//		TSP tsp= new TSP("./data/tspLib/tsp/swiss42.tsp"); //Optimal: 1273
//		TSP tsp= new TSP("./data/tspLib/tsp/att48.tsp"); //Optimal: 10628

		new TSPCGSolver(tsp);
	}


	//------------------ Helper methods -----------------

	/**
	 * Converts a TSPLib tour to a set of columns: A column for every pricing problem is created
	 * @param tour tour
	 * @param pricingProblems pricing problems
	 * @return List of columns
	 */
	private List<Matching> convertTourToColumns(TSPLibTour tour, List<PricingProblemByColor> pricingProblems) {
		List<Set<DefaultWeightedEdge>> matchings=new ArrayList<>();
		matchings.add(new LinkedHashSet<>());
		matchings.add(new LinkedHashSet<>());

		int color=0;
		for(int index=0; index<tsp.N; index++){
			int i=tour.get(index);
			int j=tour.get((index + 1) % tsp.N);
			DefaultWeightedEdge edge = tsp.getEdge(i, j);
			matchings.get(color).add(edge);
			color=(color+1)%2;
		}

		List<Matching> initSolution=new ArrayList<>();
		initSolution.add(this.buildMatching(pricingProblems.get(0), matchings.get(0)));
		initSolution.add(this.buildMatching(pricingProblems.get(1), matchings.get(1)));
		return initSolution;
	}

	/**
	 * Helper method which builds a column for a given pricing problem consisting of the predefined edges
	 * @param pricingProblem pricing problem
	 * @param edges List of edges constituting the matching
	 * @return Matching
	 */
	private Matching buildMatching(PricingProblemByColor pricingProblem, Set<DefaultWeightedEdge> edges) {
		int[] succ=new int[tsp.N];

		int cost=0;
		for(DefaultWeightedEdge edge : edges) {
			succ[tsp.getEdgeSource(edge)]=tsp.getEdgeTarget(edge);
			succ[tsp.getEdgeTarget(edge)]=tsp.getEdgeSource(edge);
			cost+=tsp.getEdgeWeight(edge);
		}
		return new Matching("init", false, pricingProblem, edges, succ, cost);
	}
}


