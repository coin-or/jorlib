package org.jorlib.demo.frameworks.columnGeneration.example2.cg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jorlib.demo.frameworks.columnGeneration.example2.cg.master.Master;
import org.jorlib.demo.frameworks.columnGeneration.example2.cg.master.TSPMasterData;
import org.jorlib.demo.frameworks.columnGeneration.example2.cg.master.cuts.SubtourInequalityGenerator;
import org.jorlib.demo.frameworks.columnGeneration.example2.model.Edge;
import org.jorlib.demo.frameworks.columnGeneration.example2.model.MatchingColor;
import org.jorlib.demo.frameworks.columnGeneration.example2.model.TSP;
import org.jorlib.frameworks.columnGeneration.colgenMain.ColGen;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
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
		Master master=new Master(tsp, cutHandler);
		
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
		
		//Solve the problem through column generation
		try {
			cg.solve(System.currentTimeMillis()+10000L);
		} catch (TimeLimitExceededException e) {
			e.printStackTrace();
		}
		
		//Print solution:
		List<Matching> solution=cg.getSolution();
		System.out.println("CG terminated with objective: "+cg.getObjective());
		System.out.println("Number of iterations: "+cg.getNumberOfIterations());
		System.out.println("Time spent on master: "+cg.getMasterSolveTime()+" time spent on pricing: "+cg.getPricingSolveTime());
		System.out.println("Columns (only non-zero columns are returned):");
		for(Matching column : solution)
			System.out.println(column);
		
		//Clean up:
		cg.close(); //This closes both the master and pricing problems
		cutHandler.close(); //Close the cut handler. The close() call is propagated to all registered CutGenerator classes
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
		int[][] succ=new int[2][tsp.N];
//		Arrays.fill(succ[0], -1);
//		Arrays.fill(succ[1], -1);
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
		System.out.println("Init solution cost: "+(column1.cost+column2.cost)+" - columns: \n"+column1+"\n"+column2);
		return initSolution;
	}
	
	public static void main(String[] args){
		//TSPLib instance Burma 14 - Optimal tour length: 3323, see http://www.iwr.uni-heidelberg.de/groups/comopt/software/TSPLIB95/
		int N=14;
		int[][] distanceMatrix={{0, 153, 510, 706, 966, 581, 455, 70, 160, 372, 157, 567, 342, 398}, {153, 0, 422, 664, 997, 598, 507, 197, 311, 479, 310, 581, 417, 376}, {510, 422, 0, 289, 744, 390, 437, 491, 645, 880, 618, 374, 455, 211}, {706, 664, 289, 0, 491, 265, 410, 664, 804, 1070, 768, 259, 499, 310}, {966, 997, 744, 491, 0, 400, 514, 902, 990, 1261, 947, 418, 635, 636}, {581, 598, 390, 265, 400, 0, 168, 522, 634, 910, 593, 19, 284, 239}, {455, 507, 437, 410, 514, 168, 0, 389, 482, 757, 439, 163, 124, 232}, {70, 197, 491, 664, 902, 522, 389, 0, 154, 406, 133, 508, 273, 355}, {160, 311, 645, 804, 990, 634, 482, 154, 0, 276, 43, 623, 358, 498}, {372, 479, 880, 1070, 1261, 910, 757, 406, 276, 0, 318, 898, 633, 761}, {157, 310, 618, 768, 947, 593, 439, 133, 43, 318, 0, 582, 315, 464}, {567, 581, 374, 259, 418, 19, 163, 508, 623, 898, 582, 0, 275, 221}, {342, 417, 455, 499, 635, 284, 124, 273, 358, 633, 315, 275, 0, 247}, {398, 376, 211, 310, 636, 239, 232, 355, 498, 761, 464, 221, 247, 0}};
		TSP tsp=new TSP(N, distanceMatrix);
		new TSPSolver(tsp);
	}
}
