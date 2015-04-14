package org.jorlib.demo.frameworks.columnGeneration.example2.cg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jorlib.demo.frameworks.columnGeneration.example2.cg.master.TSPMaster;
import org.jorlib.demo.frameworks.columnGeneration.example2.cg.master.TSPMasterData;
import org.jorlib.demo.frameworks.columnGeneration.example2.cg.master.cuts.SubtourInequalityGenerator;
import org.jorlib.demo.frameworks.columnGeneration.example2.model.Edge;
import org.jorlib.demo.frameworks.columnGeneration.example2.model.MatchingColor;
import org.jorlib.demo.frameworks.columnGeneration.example2.model.TSP;
import org.jorlib.frameworks.columnGeneration.colgenMain.ColGen;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutHandler;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemSolver;

/**
 * A column generation solution to the Traveling Salesman Problem.
 * Each TSP solution on a graph with an even number of vertices can be seen as the union of two edge disjoint perfect matchings.
 * As a result, the TSP problem can be solved by selecting two edge disjoint matchings which comply with the well-known
 * DFJ subtour elimination constraints. The pricing problems amount to generating these matchings.
 * 
 * The ideas in this example are loosely based on the work Kinable, J. "Decomposition approaches for optimization problems, 
 * chapter: "The balanced TSP problem". 2014
 * 
 * Note: this is an example class to demonstrate features of the Column Generation framework. This class is not
 * intended as a high-performance TSP solver!
 * 
 * @author jkinable
 *
 */
public class TSPSolver {

	private final TSP tsp;
	
	public TSPSolver(TSP tsp){
		this.tsp=tsp;
		//Create a cutHandler, then create a Subtour Inequality Generator and add it to the handler 
		CutHandler<TSP, TSPMasterData> cutHandler=new CutHandler<>();
		SubtourInequalityGenerator cutGen=new SubtourInequalityGenerator(tsp);
		cutHandler.addCutGenerator(cutGen);
		
		//Create the master problem
		TSPMaster master=new TSPMaster(tsp, cutHandler);
		
		//Create the two pricing problems
		List<PricingProblemByColor> pricingProblems=new ArrayList<>();
		pricingProblems.add(new PricingProblemByColor(tsp, "redPricing", MatchingColor.RED));
		pricingProblems.add(new PricingProblemByColor(tsp, "bluePricing", MatchingColor.BLUE));
		
		//Define which solvers to use
		List<Class<? extends PricingProblemSolver<TSP, Matching, PricingProblemByColor>>> solvers=Arrays.asList(ExactPricingProblemSolver.class);
		
		//Define an upper bound (stronger is better). 
		int upperBound=Integer.MAX_VALUE;
		
		//Create a set of initial columns.
		List<Matching> initSolution=this.getInitialSolution(pricingProblems);
		
		//Create a column generation instance
		ColGen<TSP, Matching, PricingProblemByColor> cg= new ColGen<TSP, Matching, PricingProblemByColor>(tsp, master, pricingProblems, solvers, initSolution, upperBound);
		
		//Print solution:
		List<Matching> solution=cg.getSolution();
		System.out.println("CG terminated with objective: "+cg.getObjective());
		System.out.println("Number of iterations: "+cg.getNumberOfIterations());
		System.out.println("Time spent on master: "+cg.getMasterSolveTime()+" time spent on pricing: "+cg.getPricingSolveTime());
		System.out.println("Columns (only non-zero columns are returned):");
		for(Matching column : solution)
			System.out.println(column);
	}
	
	/**
	 * Create an initial solution for the TSP.
	 * Simple initial solution: generate any tour and split it into two edge disjoint matchings.
	 * @param pricingProblem
	 * @return Initial solution
	 */
	private List<Matching> getInitialSolution(List<PricingProblemByColor> pricingProblems){
		List<Matching> initSolution=new ArrayList<Matching>();
		List<Set<Edge>> matchings=new ArrayList<>();
		matchings.add(new LinkedHashSet<>());
		matchings.add(new LinkedHashSet<>());
		int[][] succ=new int[2][tsp.N/2];
		int[] cost=new int[2];
		
		int t=0;
		for(int i=0; i<tsp.N; i++){
			int j=(i+1)%tsp.N;
			Edge edge=new Edge(i, j);
			matchings.get(t).add(edge);
			succ[t][i]=j;
			succ[t][j]=i;
			cost[t]+=tsp.distanceMatrix[i][j];
			t=(t+1)%2;
		}
		
		Matching column1=new Matching("init", false, pricingProblems.get(0), matchings.get(0), succ[0], cost[0]);
		Matching column2=new Matching("init", false, pricingProblems.get(1), matchings.get(1), succ[1], cost[1]);
		initSolution.add(column1);
		initSolution.add(column2);
		return initSolution;
	}
	
	public static void main(String[] args){
		int N=4;
		int[][] distanceMatrix=null;
		TSP tsp=new TSP(N, distanceMatrix);
		new TSPSolver(tsp);
	}
}
