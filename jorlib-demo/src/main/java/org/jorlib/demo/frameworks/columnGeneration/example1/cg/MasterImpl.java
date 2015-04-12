package org.jorlib.demo.frameworks.columnGeneration.example1.cg;

import ilog.concert.IloColumn;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.UnknownObjectException;

import java.util.ArrayList;
import java.util.List;

import org.jorlib.demo.frameworks.columnGeneration.example1.model.CuttingStock;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.master.Master;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutHandler;
import org.jorlib.frameworks.columnGeneration.util.OrderedBiMap;

/**
 * Implementation of Master class
 * @author jkinable
 *
 */
public class MasterImpl extends Master<CuttingStock, CuttingStockPricingProblem, CuttingPattern> {

	IloCplex master;
	private IloObjective obj; //Objective function
	private IloRange[] satisfyDemandConstr; //Constraint
	public OrderedBiMap<CuttingPattern, IloNumVar> cuttingPatternVars; //Variables
	
	public MasterImpl(CuttingStock modelData) {
		super(modelData);
	}

	@Override
	protected boolean solveMasterProblem(long timeLimit) throws TimeLimitExceededException {
		try {
			//Set time limit
			double timeRemaining=Math.max(1,(timeLimit-System.currentTimeMillis())/1000.0);
			logger.debug("Setting time limit to: {}",timeRemaining);
			master.setParam(IloCplex.DoubleParam.TiLim, timeRemaining); //set time limit in seconds
			
			if(!master.solve() || master.getStatus()!=IloCplex.Status.Optimal){
				if(master.getCplexStatus()==IloCplex.CplexStatus.AbortTimeLim) //Aborted due to time limit
					throw new TimeLimitExceededException();
				else
					throw new RuntimeException("Master problem solve failed! Status: "+master.getStatus());
			}else{
				masterData.objectiveValue=master.getObjValue();
			}
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return true;
	}

	@Override
	public void initializePricingProblem(CuttingStockPricingProblem pricingProblem){
		try {
			double[] duals=master.getDuals(satisfyDemandConstr);
			pricingProblem.initPricingProblem(duals);
		} catch (UnknownObjectException e) {
			e.printStackTrace();
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void buildModel() {
		try {
			master=new IloCplex();
			master.setOut(null); //Disable cplex output
			master.setParam(IloCplex.IntParam.Threads,config.MAXTHREADS); //Set number of threads that may be used by the master
			
			//Define objective
			obj=master.addMinimize();
			
			//Define constraints
			satisfyDemandConstr=new IloRange[modelData.nrFinals];
			for(int i=0; i<modelData.nrFinals; i++)
				satisfyDemandConstr[i]=master.addRange(modelData.demandForFinals[i], modelData.demandForFinals[i], "satisfyDemandFinal_"+i);
			
			//Define a container for the variables
			cuttingPatternVars=new OrderedBiMap<>();
		} catch (IloException e) {
			e.printStackTrace();
		}
		logger.info("Finished building master");
		
	}

	@Override
	public void addColumn(CuttingPattern column) {
		try {
			//Register column with objective
			IloColumn iloColumn=master.column(obj,1);
		
			//Register column with demand constraint
			for(int i=0; i<modelData.nrFinals; i++)
				iloColumn=iloColumn.and(master.column(satisfyDemandConstr[i],column.yieldVector[i]));
			
			//Create the variable and store it
			IloNumVar var=master.numVar(iloColumn, 0, Double.MAX_VALUE, "z_"+","+column.associatedPricingProblem.getNrColumns());
			master.add(var);
			cuttingPatternVars.put(column, var);
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<CuttingPattern> getSolution() {
		List<CuttingPattern> solution=new ArrayList<>();
		try {
			CuttingPattern[] cuttingPatterns=cuttingPatternVars.getKeysAsArray(new CuttingPattern[cuttingPatternVars.size()]);
			IloNumVar[] vars=cuttingPatternVars.getValuesAsArray(new IloNumVar[cuttingPatternVars.size()]);
			double[] values=master.getValues(vars);
			
			for(int i=0; i<cuttingPatterns.length; i++){
				cuttingPatterns[i].value=values[i];
				if(values[i]>=config.PRECISION){
					solution.add(cuttingPatterns[i]);
				}
			}
		} catch (UnknownObjectException e) {
			e.printStackTrace();
		} catch (IloException e) {
			e.printStackTrace();
		}
		return solution;
	}

	@Override
	public void close() {
		master.end();
	}

	@Override
	public void printSolution() {
		System.out.println("Master solution:");
		for(CuttingPattern cp : this.getSolution())
			System.out.println(cp);
	}

}
