package org.jorlib.demo.frameworks.columnGeneration.example1.cg;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloObjective;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.List;

import org.jorlib.demo.frameworks.columnGeneration.example1.model.CuttingStock;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemSolver;
import org.jorlib.frameworks.columnGeneration.util.CplexUtil;

public class ExactPricingProblemSolver extends PricingProblemSolver<CuttingStock, CuttingPattern, CuttingStockPricingProblem> {

	private IloCplex cplex; //Cplex instance.
	private IloObjective obj; //Objective function
	private IloIntVar[] vars;
	
	public ExactPricingProblemSolver(CuttingStock dataModel, 
			CuttingStockPricingProblem pricingProblem) {
		super(dataModel, "ExactSolver", pricingProblem);
		this.buildModel();
	}

	
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
		List<CuttingPattern> newPatterns=new ArrayList<CuttingPattern>();
		try {
			double timeRemaining=Math.max(1,(timeLimit-System.currentTimeMillis())/1000.0);
			logger.debug("Setting time limit to: {}",timeRemaining);
			cplex.setParam(IloCplex.DoubleParam.TiLim, timeRemaining); //set time limit in seconds
			
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
				
				if(objective >= 1+config.PRECISION){ //Generate new column
					double[] values=cplex.getValues(vars);
					int[] pattern=new int[dataModel.nrFinals];
					for(int i=0; i<dataModel.nrFinals; i++)
						pattern[i]=CplexUtil.doubleToInt(values[i]);
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
		try {
			cplex.setLinearCoefs(obj, pricingProblem.modifiedCosts, vars);
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

}
