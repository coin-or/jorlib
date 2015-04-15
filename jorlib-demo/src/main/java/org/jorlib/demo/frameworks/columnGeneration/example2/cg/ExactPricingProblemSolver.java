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
package org.jorlib.demo.frameworks.columnGeneration.example2.cg;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearIntExpr;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloObjective;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jorlib.demo.frameworks.columnGeneration.example2.model.Edge;
import org.jorlib.demo.frameworks.columnGeneration.example2.model.TSP;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemSolver;
import org.jorlib.frameworks.columnGeneration.util.CplexUtil;
import org.jorlib.frameworks.columnGeneration.util.OrderedBiMap;

/**
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public class ExactPricingProblemSolver extends PricingProblemSolver<TSP, Matching, PricingProblemByColor> {

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
			cplex.exportModel("./output/pricingLP/pricing.lp");
			//Compute how much time we may take to solve the pricing problem
			double timeRemaining=Math.max(1,(timeLimit-System.currentTimeMillis())/1000.0);
			cplex.setParam(IloCplex.DoubleParam.TiLim, timeRemaining); //set time limit in seconds
			
			//Solve the problem and check the solution status
			if(!cplex.solve() || cplex.getStatus()!=IloCplex.Status.Optimal){
				if(cplex.getCplexStatus()==IloCplex.CplexStatus.AbortTimeLim){ //Aborted due to time limit
					throw new TimeLimitExceededException();
				}else if(cplex.getStatus()==IloCplex.Status.Infeasible) { //Pricing problem infeasible
					pricingProblemInfeasible=true;
					this.objective=Double.MAX_VALUE;
				}else{
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
						if(CplexUtil.doubleToBoolean(values[k])){
							matching.add(edges[k]);
							int i=edges[k].i;
							int j=edges[k].j;
							succ[i]=j;
							succ[j]=i;
							cost+=dataModel.distanceMatrix[i][j];
						}
					}
					Matching column=new Matching("exactPricing", false, pricingProblem, matching, succ, cost);
					logger.debug("Generated new column for pricing: {}:\n{}",pricingProblem.color.name(),column);
					newPatterns.add(column);
				}
			}
			
		}catch (IloException e1) {
			e1.printStackTrace();
		}
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

}
