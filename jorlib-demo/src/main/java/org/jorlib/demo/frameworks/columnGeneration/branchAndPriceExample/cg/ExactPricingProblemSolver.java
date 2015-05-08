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
 * ExactPricingProblemSolver.java
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
package org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg;

import com.sun.org.apache.xpath.internal.SourceTree;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearIntExpr;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloObjective;
import ilog.cplex.IloCplex;

import java.util.*;

import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.bap.branching.branchingDecisions.FixEdge;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.bap.branching.branchingDecisions.RemoveEdge;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.model.MatchingColor;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.model.TSP;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecisionListener;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemSolver;
import org.jorlib.frameworks.columnGeneration.util.MathProgrammingUtil;
import org.jorlib.frameworks.columnGeneration.util.OrderedBiMap;
import org.jorlib.io.tspLibReader.graph.Edge;


/**
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public class ExactPricingProblemSolver extends PricingProblemSolver<TSP, Matching, PricingProblemByColor> implements BranchingDecisionListener{

	private IloCplex cplex; //Cplex instance.
	private IloObjective obj; //Objective function
	public OrderedBiMap<Edge, IloIntVar> vars; //Variables
	
	public ExactPricingProblemSolver(TSP dataModel, PricingProblemByColor pricingProblem) {
		super(dataModel, pricingProblem);
		this.name="ExactMatchingCalculator"; //Set a nice name for the solver
		this.buildModel();
	}

	/**
	 * Build the MIP model
	 */
	private void buildModel(){
		try {
			cplex=new IloCplex();
			cplex.setParam(IloCplex.IntParam.AdvInd, 0);
			cplex.setParam(IloCplex.IntParam.Threads,1);
			cplex.setOut(null);
			
			//Create the variables (a single variable per edge)
			vars=new OrderedBiMap<>();
			for(int i=0; i<dataModel.N-1; i++){
				for(int j=i+1; j<dataModel.N; j++){
					Edge edge=new Edge(i, j);
					IloIntVar var=cplex.boolVar("x_"+i+"_"+j);
					vars.put(edge, var);
				}
			}
			//Create the objective
			obj=cplex.addMaximize();
			//Create the constraints:
			//1. EXACTLY 1 edge must be selected from all edges incident to a particular vertex
			for(int i=0; i<dataModel.N; i++){
				IloLinearIntExpr expr=cplex.linearIntExpr();
				for(int j=0; j<dataModel.N; j++){
					if(i==j)
						continue;
					IloIntVar var=vars.get(new Edge(i, j));
					expr.addTerm(1, var);
				}
				cplex.addEq(expr, 1);
			}
						
		} catch (IloException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected List<Matching> generateNewColumns()throws TimeLimitExceededException {
		List<Matching> newPatterns=new ArrayList<Matching>();
		try {
//			cplex.exportModel("./output/pricingLP/pricing.lp");
			//Compute how much time we may take to solve the pricing problem
			double timeRemaining=Math.max(1,(timeLimit-System.currentTimeMillis())/1000.0);
			cplex.setParam(IloCplex.DoubleParam.TiLim, timeRemaining); //set time limit in seconds
			
			//Solve the problem and check the solution nodeStatus
			if(!cplex.solve() || cplex.getStatus()!=IloCplex.Status.Optimal){
				if(cplex.getCplexStatus()==IloCplex.CplexStatus.AbortTimeLim){ //Aborted due to time limit
					throw new TimeLimitExceededException();
				}else if(cplex.getStatus()==IloCplex.Status.Infeasible) { //Pricing problem infeasible
					pricingProblemInfeasible=true;
					this.objective=Double.MAX_VALUE;
					throw new RuntimeException("Pricing problem infeasible");
				}else{
					if(cplex.getStatus() == IloCplex.Status.Unbounded) {
						cplex.exportModel("./output/pricingLP/pricing_unbounded.lp");
						cplex.exportModel("./output/pricingLP/pricing_unbounded.mps");
					}
					throw new RuntimeException("Pricing problem solve failed! Status: "+cplex.getStatus());
				}
			}else{ //Pricing problem solved to optimality. Exact Matching
				this.pricingProblemInfeasible=false;
				this.objective=cplex.getObjValue();
				
				if(objective >= -pricingProblem.dualConstant+config.PRECISION){ //Generate new column if it has negative reduced cost
					Edge[] edges=vars.getKeysAsArray(new Edge[vars.size()]);
					IloIntVar[] edgeVarsArray=vars.getValuesAsArray(new IloIntVar[vars.size()]);
					double[] values=cplex.getValues(edgeVarsArray);
					
					Set<Edge> matching=new LinkedHashSet<>();
					int[] succ=new int[dataModel.N];
//					Arrays.fill(succ, -1);
					int cost=0;
					for(int k=0; k<vars.size(); k++){
						if(MathProgrammingUtil.doubleToBoolean(values[k])){
							matching.add(edges[k]);
							int i=edges[k].getId1();
							int j=edges[k].getId2();
							succ[i]=j;
							succ[j]=i;
							cost+=dataModel.getEdgeWeight(i,j);
						}
					}
					Matching column=new Matching("exactPricing", false, pricingProblem, matching, succ, cost);
					logger.debug("Generated new column for pricing: {}:\n{}",pricingProblem.color.name(),column);
					newPatterns.add(column);
				}else{
					Object[] o={pricingProblem.color.name(), objective, pricingProblem.dualConstant*-1};
					logger.debug("No columns for pricing problem {}. Objective: {} dual constant: {}",o);
				}
			}
			
		}catch (IloException e1) {
			e1.printStackTrace();
		}
//		this.test();
		return newPatterns;
	}

	@Override
	protected void setObjective() {
		//Update the objective function with the new dual values
		try {
			IloIntVar[] edgeVarsArray=vars.getValuesAsArray(new IloIntVar[vars.size()]);
			IloLinearNumExpr objExpr=cplex.scalProd(pricingProblem.modifiedCosts, edgeVarsArray);
			obj.setExpr(objExpr);
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		cplex.end();
	}

	@Override
	public void branchingDecisionPerformed(BranchingDecision bd) {
		try {
			if(bd instanceof FixEdge){
				FixEdge fixEdgeDecision = (FixEdge) bd;
				if(fixEdgeDecision.pricingProblem == this.pricingProblem)
					vars.get(fixEdgeDecision.edge).setLB(1); //Ensure that any column returned contains this edge.
			}else if(bd instanceof RemoveEdge){
				RemoveEdge removeEdgeDecision= (RemoveEdge) bd;
				if(removeEdgeDecision.pricingProblem == this.pricingProblem)
					vars.get(removeEdgeDecision.edge).setUB(0); //Ensure that any column returned does NOT contain this edge.
			}
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void branchingDecisionRewinded(BranchingDecision bd) {
		try {
			if(bd instanceof FixEdge){
				FixEdge fixEdgeDecision = (FixEdge) bd;
				if(fixEdgeDecision.pricingProblem == this.pricingProblem)
					vars.get(fixEdgeDecision.edge).setLB(0); //Reset the LB to its original value
			}else if(bd instanceof RemoveEdge){
				RemoveEdge removeEdgeDecision= (RemoveEdge) bd;
				if(removeEdgeDecision.pricingProblem == this.pricingProblem)
					vars.get(removeEdgeDecision.edge).setUB(1); //Reset the UB to its original value
			}
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

	public void test(){
		Set<Edge> myEdges=new HashSet<>();
		if(this.pricingProblem.color== MatchingColor.RED){
			myEdges.add(new Edge(0,7));
			myEdges.add(new Edge(37,30));
			myEdges.add(new Edge(43,17));
			myEdges.add(new Edge(6,27));
			myEdges.add(new Edge(5,36));
			myEdges.add(new Edge(18,26));
			myEdges.add(new Edge(16,42));
			myEdges.add(new Edge(29,35));
			myEdges.add(new Edge(45,32));
			myEdges.add(new Edge(19,46));
			myEdges.add(new Edge(20,31));
			myEdges.add(new Edge(38,47));
			myEdges.add(new Edge(4,41));
			myEdges.add(new Edge(23,9));
			myEdges.add(new Edge(44,34));
			myEdges.add(new Edge(3,25));
			myEdges.add(new Edge(1,28));
			myEdges.add(new Edge(33,40));
			myEdges.add(new Edge(15,21));
			myEdges.add(new Edge(2,22));
			myEdges.add(new Edge(13,24));
			myEdges.add(new Edge(12,10));
			myEdges.add(new Edge(11,14));
			myEdges.add(new Edge(39,8));
		}else{
			myEdges.add(new Edge(7,37));
			myEdges.add(new Edge(30,43));
			myEdges.add(new Edge(17,6));
			myEdges.add(new Edge(27,5));
			myEdges.add(new Edge(36,18));
			myEdges.add(new Edge(26,16));
			myEdges.add(new Edge(42,29));
			myEdges.add(new Edge(35,45));
			myEdges.add(new Edge(32,19));
			myEdges.add(new Edge(46,20));
			myEdges.add(new Edge(31,38));
			myEdges.add(new Edge(47,4));
			myEdges.add(new Edge(41,23));
			myEdges.add(new Edge(9,44));
			myEdges.add(new Edge(34,3));
			myEdges.add(new Edge(25,1));
			myEdges.add(new Edge(28,33));
			myEdges.add(new Edge(40,15));
			myEdges.add(new Edge(21,2));
			myEdges.add(new Edge(22,13));
			myEdges.add(new Edge(24,12));
			myEdges.add(new Edge(10,11));
			myEdges.add(new Edge(14,39));
			myEdges.add(new Edge(8,0));
		}

		double value=0;
		for(int i=0; i<vars.keyList().size(); i++){
			Edge e=vars.keyList().get(i);
			if(myEdges.contains(e))
				value+=pricingProblem.modifiedCosts[i];
		}
		try {
			cplex.exportModel("./output/pricingLP/pricing"+pricingProblem.color.name()+".lp");
		} catch (IloException e) {
			e.printStackTrace();
		}
		System.out.println("Reduced cost of optimal column: "+value+" dual constant: "+(-pricingProblem.dualConstant) +" color: "+pricingProblem.color.name());
	}
}
