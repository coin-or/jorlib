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
package org.jorlib.frameworks.columnGeneration.tsp.cg;

import ilog.concert.*;
import ilog.cplex.IloCplex;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblemSolver;
import org.jorlib.frameworks.columnGeneration.tsp.bap.branching.branchingDecisions.FixEdge;
import org.jorlib.frameworks.columnGeneration.tsp.bap.branching.branchingDecisions.RemoveEdge;
import org.jorlib.frameworks.columnGeneration.tsp.model.TSP;
import org.jorlib.frameworks.columnGeneration.util.MathProgrammingUtil;
import org.jorlib.frameworks.columnGeneration.util.OrderedBiMap;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * Algorthm implementation which solves the pricing problem to optimality. This solver is based on an exact MIP implementation
 * using Cplex.
 *
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public final class ExactPricingProblemSolver extends AbstractPricingProblemSolver<TSP, Matching, PricingProblemByColor> {

	private IloCplex cplex; //Cplex instance.
	private IloObjective obj; //Objective function
	private OrderedBiMap<DefaultWeightedEdge, IloIntVar> vars; //Variables

	/**
	 * Creates a new solver instance for a particular pricing problem
	 * @param dataModel data model
	 * @param pricingProblem pricing problem
	 */
	public ExactPricingProblemSolver(TSP dataModel, PricingProblemByColor pricingProblem) {
		super(dataModel, pricingProblem);
		this.name="ExactMatchingCalculator";
		this.buildModel();
	}

	/**
	 * Build the MIP model. Essentially this model generates maximum weight perfect matchings.
	 */
	private void buildModel(){
		try {
			cplex=new IloCplex();
			cplex.setParam(IloCplex.IntParam.AdvInd, 0);
			cplex.setParam(IloCplex.IntParam.Threads, 1);
			cplex.setOut(null);
			
			//Create the variables (a single variable per edge)
			vars=new OrderedBiMap<>();
			for(DefaultWeightedEdge edge : dataModel.edgeSet()){
				IloIntVar var=cplex.boolVar("x_"+dataModel.getEdgeSource(edge)+"_"+dataModel.getEdgeTarget(edge));
				vars.put(edge, var);
			}
			//Create the objective
			obj=cplex.addMaximize();
			//Create the constraints:
			//EXACTLY 1 edge must be selected from all edges incident to a particular vertex
			for(int i=0; i<dataModel.N; i++){
				IloLinearIntExpr expr=cplex.linearIntExpr();
				for(DefaultWeightedEdge edge : dataModel.edgesOf(i)){
					IloIntVar var=vars.get(edge);
					expr.addTerm(1, var);
				}
				cplex.addEq(expr, 1);
			}
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Main method which solves the pricing problem.
	 * @return List of columns (matchings) with negative reduced cost.
	 * @throws TimeLimitExceededException TimeLimitExceededException
	 */
	@Override
	protected List<Matching> generateNewColumns()throws TimeLimitExceededException {
		List<Matching> newPatterns=new ArrayList<>();
		try {
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
					throw new RuntimeException("Pricing problem solve failed! Status: "+cplex.getStatus());
				}
			}else{ //Pricing problem solved to optimality.
				this.pricingProblemInfeasible=false;
				this.objective=cplex.getObjValue();
				
				if(objective >= -pricingProblem.dualCost +config.PRECISION){ //Generate new column if it has negative reduced cost
					DefaultWeightedEdge[] edges=vars.getKeysAsArray(new DefaultWeightedEdge[vars.size()]); //Get the variable values
					IloIntVar[] edgeVarsArray=vars.getValuesAsArray(new IloIntVar[vars.size()]);
					double[] values=cplex.getValues(edgeVarsArray);
					
					Set<DefaultWeightedEdge> matching=new LinkedHashSet<>(); //Generate a new matching
					int[] succ=new int[dataModel.N];
					int cost=0;
					for(int k=0; k<vars.size(); k++){
						if(MathProgrammingUtil.doubleToBoolean(values[k])){
							matching.add(edges[k]);
							int i=dataModel.getEdgeSource(edges[k]);
							int j=dataModel.getEdgeTarget(edges[k]);
							succ[i]=j;
							succ[j]=i;
							cost+=dataModel.getEdgeWeight(edges[k]);
						}
					}
					Matching column=new Matching("exactPricing", false, pricingProblem, matching, succ, cost);
					newPatterns.add(column);
				}
			}
			
		}catch (IloException e1) {
			e1.printStackTrace();
		}
		return newPatterns;
	}

	/**
	 * Update the objective function of the pricing problem with the new pricing information (modified costs).
	 * The modified costs are stored in the pricing problem.
	 */
	@Override
	protected void setObjective() {
		try {
			IloIntVar[] edgeVarsArray=vars.getValuesAsArray(new IloIntVar[vars.size()]);
			IloLinearNumExpr objExpr=cplex.scalProd(pricingProblem.dualCosts, edgeVarsArray);
			obj.setExpr(objExpr);
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Close the pricing problem
	 */
	@Override
	public void close() {
		cplex.end();
	}

	/**
	 * Listen to branching decisions. The pricing problem is changed by the branching decisions.
	 * @param bd BranchingDecision
	 */
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

	/**
	 * When the Branch-and-Price algorithm backtracks, branching decisions are reversed.
	 * @param bd BranchingDecision
	 */
	@Override
	public void branchingDecisionReversed(BranchingDecision bd) {
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

}
