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
package org.jorlib.demo.frameworks.columnGeneration.cuttingStockCG.cg;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloObjective;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.List;

import org.jorlib.demo.frameworks.columnGeneration.cuttingStockCG.model.CuttingStock;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblemSolver;
import org.jorlib.frameworks.columnGeneration.util.MathProgrammingUtil;

/**
 * This class provides a solver for the cutting stock pricing problem.
 * The pricing problem is a simple knapsack problem. Here we solve this problem through a simple MIP implementation.
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 */
public final class ExactPricingProblemSolver extends AbstractPricingProblemSolver<CuttingStock, CuttingPattern, PricingProblem> {

	private IloCplex cplex; //Cplex instance.
	private IloObjective obj; //Objective function
	private IloIntVar[] vars; //Problem variables
	
	public ExactPricingProblemSolver(CuttingStock dataModel, PricingProblem pricingProblem) {
		super(dataModel, pricingProblem);
		this.name="ExactSolver"; //Set a name for the solver
		this.buildModel();
	}

	/**
	 * Build the MIP model
	 */
	private void buildModel(){
		try {
			cplex=new IloCplex();
			cplex.setParam(IloCplex.IntParam.AdvInd, 0);
			cplex.setParam(IloCplex.IntParam.Threads,config.MAXTHREADS);
			cplex.setOut(null);
			
			//Create the variables
			vars=cplex.intVarArray(dataModel.nrFinals, 0, Integer.MAX_VALUE);
			//Create the objective
			obj=cplex.addMaximize(cplex.sum(vars));
			//Create the constraints
			cplex.addLe(cplex.scalProd(vars, dataModel.finals), dataModel.rollWidth);
						
		} catch (IloException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void close() {
		cplex.end();
	}

	@Override
	protected List<CuttingPattern> generateNewColumns() throws TimeLimitExceededException {
		List<CuttingPattern> newPatterns=new ArrayList<>();
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
				}else{
					throw new RuntimeException("Pricing problem solve failed! Status: "+cplex.getStatus());
				}
			}else{ //Pricing problem solved to optimality
				this.pricingProblemInfeasible=false;
				this.objective=cplex.getObjValue();
				
				if(objective >= 1+config.PRECISION){ //Generate new column if it has negative reduced cost
					double[] values=cplex.getValues(vars);
					int[] pattern=new int[dataModel.nrFinals];
					for(int i=0; i<dataModel.nrFinals; i++)
						pattern[i]= MathProgrammingUtil.doubleToInt(values[i]);
					CuttingPattern column=new CuttingPattern("exactPricing", false, pattern, pricingProblem);
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
			cplex.setLinearCoefs(obj, pricingProblem.dualCosts, vars);
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

}
