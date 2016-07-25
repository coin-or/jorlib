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
package org.jorlib.demo.frameworks.columnGeneration.tspBAP;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jorlib.demo.frameworks.columnGeneration.tspBAP.bap.BranchAndPrice;
import org.jorlib.demo.frameworks.columnGeneration.tspBAP.bap.branching.BranchOnEdge;
import org.jorlib.demo.frameworks.columnGeneration.tspBAP.cg.ExactPricingProblemSolver;
import org.jorlib.demo.frameworks.columnGeneration.tspBAP.cg.Matching;
import org.jorlib.demo.frameworks.columnGeneration.tspBAP.cg.PricingProblemByColor;
import org.jorlib.demo.frameworks.columnGeneration.tspBAP.cg.master.Master;
import org.jorlib.demo.frameworks.columnGeneration.tspBAP.cg.master.TSPMasterData;
import org.jorlib.demo.frameworks.columnGeneration.tspBAP.cg.master.cuts.SubtourInequalityGenerator;
import org.jorlib.demo.frameworks.columnGeneration.tspBAP.model.MatchingColor;
import org.jorlib.demo.frameworks.columnGeneration.tspBAP.model.TSP;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.AbstractBranchCreator;
import org.jorlib.frameworks.columnGeneration.io.SimpleBAPLogger;
import org.jorlib.frameworks.columnGeneration.io.SimpleDebugger;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutHandler;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblemSolver;
import org.jorlib.io.tspLibReader.TSPLibTour;

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
public final class TSPSolver {

	private final TSP tsp;

	public TSPSolver(TSP tsp){
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
		
		//Define which solvers to use for the pricing problem
		List<Class<? extends AbstractPricingProblemSolver<TSP, Matching, PricingProblemByColor>>> solvers= Collections.singletonList(ExactPricingProblemSolver.class);
		
		//OPTIONAL: Get an initial solution and use it as an upper bound
		TSPLibTour initTour=TSPLibTour.createCanonicalTour(tsp.N); //Feasible solution
		int tourLength=tsp.getTourLength(initTour); //Upper bound (Stronger is better)
		List<Matching> initSolution=this.convertTourToColumns(initTour, pricingProblems); //Create a set of initial columns.

		//Define Branch creators
		List<? extends AbstractBranchCreator<TSP, Matching, PricingProblemByColor>> branchCreators= Collections.singletonList(new BranchOnEdge(tsp, pricingProblems));

		//Create a Branch-and-Price instance
		BranchAndPrice bap=new BranchAndPrice(tsp, master, pricingProblems, solvers, branchCreators, tourLength, initSolution);

		//OPTIONAL: Attach a debugger
		SimpleDebugger debugger=new SimpleDebugger(bap, cutHandler, true);

		//OPTIONAL: Attach a logger to the Branch-and-Price procedure.
		SimpleBAPLogger logger=new SimpleBAPLogger(bap, new File("./output/tsp.log"));

		//Solve the TSP problem through Branch-and-Price
		bap.runBranchAndPrice(System.currentTimeMillis()+8000000L);

		
		//Print solution:
		System.out.println("================ Solution ================");
		System.out.println("BAP terminated with objective (tour length): "+bap.getObjective());
		System.out.println("Total Number of iterations: "+bap.getTotalNrIterations());
		System.out.println("Total Number of processed nodes: "+bap.getNumberOfProcessedNodes());
		System.out.println("Total Time spent on master problems: "+bap.getMasterSolveTime()+" Total time spent on pricing problems: "+bap.getPricingSolveTime());
		if(bap.hasSolution()) {
			System.out.println("Solution is optimal: "+bap.isOptimal());
			System.out.println("Columns (only non-zero columns are returned):");
			List<Matching> solution = bap.getSolution();
			for (Matching column : solution)
				System.out.println(column);

			TSPLibTour tour = this.convertColumnsToTour(solution);
			System.out.println("Best tour found: " + tour);
			System.out.println("Tour length: " + tsp.getTourLength(tour));
		}

		//Clean up:
		bap.close(); //Close master and pricing problems
		cutHandler.close(); //Close the cut handler. The close() call is propagated to all registered AbstractCutGenerator classes
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

		new TSPSolver(tsp);
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

	/**
	 * Converts a set of matchings (columns) back to a TSPLib tour
	 * @param columns list of columns
	 * @return a TSPLibTour
	 */
	private TSPLibTour convertColumnsToTour(List<Matching> columns){
		int[] nodes=new int[tsp.N];
		nodes[0]=0;
		for(int i=1; i<tsp.N; i++){
			nodes[i]=columns.get(i%2).succ[nodes[i-1]];
		}
		return TSPLibTour.createTour(nodes);
	}
}


