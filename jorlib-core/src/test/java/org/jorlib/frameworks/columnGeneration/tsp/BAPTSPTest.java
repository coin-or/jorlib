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
package org.jorlib.frameworks.columnGeneration.tsp;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.AbstractBranchCreator;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutHandler;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblemSolver;
import org.jorlib.frameworks.columnGeneration.tsp.bap.BranchAndPrice;
import org.jorlib.frameworks.columnGeneration.tsp.bap.branching.BranchOnEdge;
import org.jorlib.frameworks.columnGeneration.tsp.cg.ExactPricingProblemSolver;
import org.jorlib.frameworks.columnGeneration.tsp.cg.Matching;
import org.jorlib.frameworks.columnGeneration.tsp.cg.PricingProblemByColor;
import org.jorlib.frameworks.columnGeneration.tsp.cg.master.Master;
import org.jorlib.frameworks.columnGeneration.tsp.cg.master.TSPMasterData;
import org.jorlib.frameworks.columnGeneration.tsp.cg.master.cuts.SubtourInequalityGenerator;
import org.jorlib.frameworks.columnGeneration.tsp.model.MatchingColor;
import org.jorlib.frameworks.columnGeneration.tsp.model.TSP;
import org.jorlib.io.tspLibReader.TSPLibTour;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * This class tests the Branch-and-Price framework by solving a number of TSP instances through column generation.
 * This class does NOT test individual methods of the framework; it only tests the entire framework.<br><br>
 *
 * A column generation solution to the Traveling Salesman Problem.
 * Each TSP solution on a graph with an even number of vertices can be seen as the union of two edge disjoint perfect matchings.
 * As a result, the TSP problem can be solved by selecting two edge disjoint matchings which comply with the well-known
 * DFJ subtour elimination constraints. The pricing problems amount to generating these matchings.<p>
 * 
 * The ideas in this example are loosely based on the work Kinable, J. "Decomposition approaches for optimization problems, 
 * chapter: "The balanced TSP problem". 2014<p>
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public final class BAPTSPTest {

	private static Map<String, Integer> instances;

	@BeforeClass
	public static void initializeInstances() {
		instances = new LinkedHashMap<>();
		instances.put("burma14", 3323);
		instances.put("ulysses16", 6859);
		instances.put("ulysses22", 7013);
		instances.put("gr24", 1272);
		instances.put("fri26", 937);
		instances.put("dantzig42", 699);
		instances.put("swiss42", 1273);
//		instances.put("att48", 10628);
	}

	@AfterClass
	public static void freeInstances() {
		instances = null;
	}

	@Test
	public void testBAPFrameworkThroughTSP() throws IOException {
		for(String instance : instances.keySet()){
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("./tspLib/tsp/"+instance+".tsp");
			if(inputStream == null)
				Assert.fail("Cannot find problem instance!");
			TSP tsp =new TSP(inputStream);
			int solution=this.solveTSPInstance(tsp);
			System.out.println("Solution for : "+instance+" is: "+solution);
			Assert.assertEquals(solution, instances.get(instance).intValue());
			inputStream.close();
		}
	}

	private int solveTSPInstance(TSP tsp){
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

		//Get an initial solution and use it as an upper bound
		TSPLibTour initTour=TSPLibTour.createCanonicalTour(tsp.N); //Feasible solution
		int tourLength=tsp.getTourLength(initTour); //Upper bound (Stronger is better)
		List<Matching> initSolution=this.convertTourToColumns(tsp, initTour, pricingProblems); //Create a set of initial columns.

		//Define Branch creators
		List<? extends AbstractBranchCreator<TSP, Matching, PricingProblemByColor>> branchCreators= Collections.singletonList(new BranchOnEdge(tsp, pricingProblems));

		//Create a Branch-and-Price instance
		BranchAndPrice bap=new BranchAndPrice(tsp, master, pricingProblems, solvers, branchCreators, tourLength, initSolution);

		//Solve the TSP problem through Branch-and-Price
		bap.runBranchAndPrice(System.currentTimeMillis()+8000000L);


		//Get the solution
		int solution=-1;
		if(bap.hasSolution()) {
			assert(bap.isOptimal());
			solution = bap.getObjective();
		}

		//Clean up:
		bap.close(); //Close master and pricing problems
		cutHandler.close(); //Close the cut handler. The close() call is propagated to all registered AbstractCutGenerator classes

		return solution;
	}



	//------------------ Helper methods -----------------

	/**
	 * Converts a TSPLib tour to a set of columns: A column for every pricing problem is created
	 * @param tour tour
	 * @param pricingProblems pricing problems
	 * @return List of columns
	 */
	private List<Matching> convertTourToColumns(TSP tsp, TSPLibTour tour, List<PricingProblemByColor> pricingProblems) {
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
		initSolution.add(this.buildMatching(tsp, pricingProblems.get(0), matchings.get(0)));
		initSolution.add(this.buildMatching(tsp, pricingProblems.get(1), matchings.get(1)));
		return initSolution;
	}

	/**
	 * Helper method which builds a column for a given pricing problem consisting of the predefined edges
	 * @param pricingProblem pricing problem
	 * @param edges List of edges constituting the matching
	 * @return Matching
	 */
	private Matching buildMatching(TSP tsp, PricingProblemByColor pricingProblem, Set<DefaultWeightedEdge> edges) {
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


