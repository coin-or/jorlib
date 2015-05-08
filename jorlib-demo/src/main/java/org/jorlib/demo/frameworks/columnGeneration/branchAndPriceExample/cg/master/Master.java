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
 * Master.java
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
package org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.master;

import ilog.concert.IloColumn;
import ilog.concert.IloException;

import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.UnknownObjectException;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.Matching;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.PricingProblemByColor;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.master.cuts.SubtourInequality;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.model.MatchingColor;

import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.model.TSP;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.master.AbstractMaster;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutHandler;
import org.jorlib.frameworks.columnGeneration.util.OrderedBiMap;
import org.jorlib.io.tspLibReader.graph.Edge;


/**
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public class Master extends AbstractMaster<TSP, PricingProblemByColor, Matching, TSPMasterData> {

	private IloObjective obj; //Objective function
	private IloRange exactlyOneRedMatchingConstr; //Constraint
	private IloRange exactlyOneBlueMatchingConstr; //Constraint
	private Map<Edge, IloRange> edgeOnlyUsedOnceConstr;
	
	public Master(TSP modelData, CutHandler<TSP, TSPMasterData> cutHandler) {
		super(modelData, cutHandler);
	}
	
	@Override
	protected boolean solveMasterProblem(long timeLimit)	throws TimeLimitExceededException {
		try {
			//Set time limit
			double timeRemaining=Math.max(1,(timeLimit-System.currentTimeMillis())/1000.0);
			masterData.cplex.setParam(IloCplex.DoubleParam.TiLim, timeRemaining); //set time limit in seconds
			//Potentially export the model
			if(config.EXPORT_MODEL) masterData.cplex.exportModel(config.EXPORT_MASTER_DIR+"master_"+this.getIterationCount()+".lp");
			
			//Solve the model
			if(!masterData.cplex.solve() || masterData.cplex.getStatus()!=IloCplex.Status.Optimal){
				if(masterData.cplex.getCplexStatus()==IloCplex.CplexStatus.AbortTimeLim) //Aborted due to time limit
					throw new TimeLimitExceededException();
				else
					throw new RuntimeException("Master problem solve failed! Status: "+masterData.cplex.getStatus());
			}else{
				masterData.objectiveValue=masterData.cplex.getObjValue();
			}
			logger.debug("Finished solving master");
		} catch (IloException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void initializePricingProblem(PricingProblemByColor pricingProblem) {
		try {
			double[] modifiedCosts=new double[modelData.N*(modelData.N-1)/2]; //Modified cost for every edge
			int index=0;
			for(int i=0; i<modelData.N-1; i++){
				for(int j=i+1; j<modelData.N; j++){
					Edge edge=new Edge(i, j);
					modifiedCosts[index]=masterData.cplex.getDual(edgeOnlyUsedOnceConstr.get(edge))-modelData.getEdgeWeight(edge);

					for(SubtourInequality subtourInequality : masterData.subtourInequalities.keySet()){
						if(subtourInequality.cutSet.contains(i) ^ subtourInequality.cutSet.contains(j))
							modifiedCosts[index]+= masterData.cplex.getDual(masterData.subtourInequalities.get(subtourInequality));
					}
					index++;
				}
			}
			double dualConstant;
			if(pricingProblem.color==MatchingColor.RED)
				dualConstant=masterData.cplex.getDual(exactlyOneRedMatchingConstr);
			else
				dualConstant=masterData.cplex.getDual(exactlyOneBlueMatchingConstr);
			
			pricingProblem.initPricingProblem(modifiedCosts, dualConstant);
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected TSPMasterData buildModel() {
		IloCplex cplex=null;
//		OrderedBiMap<Matching, IloNumVar> matchingVars=null;
		EnumMap<MatchingColor, OrderedBiMap<Matching, IloNumVar>> matchingVars=null;
		
		try {
			cplex=new IloCplex(); //Create cplex instance
			cplex.setOut(null); //Disable cplex output
			cplex.setParam(IloCplex.IntParam.Threads,config.MAXTHREADS); //Set number of threads that may be used by the master
			
			//Define objective
			obj=cplex.addMinimize();
			
			//Define constraints
			exactlyOneRedMatchingConstr=cplex.addRange(1, 1, "exactlyOneRed"); //Select exactly one red matching
			exactlyOneBlueMatchingConstr=cplex.addRange(1, 1, "exactlyOneBlue"); //Select exactly one blue matching
			
			edgeOnlyUsedOnceConstr=new LinkedHashMap<Edge, IloRange>(); //Each edge may only be used once
			for(int i=0; i<modelData.N-1; i++){
				for(int j=i+1; j<modelData.N; j++){
					Edge edge=new Edge(i, j);
					IloRange constr=cplex.addRange(0, 1, "edgeOnlyUsedOnce_"+i+"_"+j);
					edgeOnlyUsedOnceConstr.put(edge,  constr);
				}
			}
			
			//Define a container for the variables
//			matchingVars=new OrderedBiMap<>();
			matchingVars=new EnumMap<>(MatchingColor.class);
			matchingVars.put(MatchingColor.RED, new OrderedBiMap<>());
			matchingVars.put(MatchingColor.BLUE, new OrderedBiMap<>());
		} catch (IloException e) {
			e.printStackTrace();
		}
		logger.info("Finished building master");
		
		//Create a new data object which will store information from the master. This object automatically be passed to the CutHandler class.
		return new TSPMasterData(cplex, matchingVars);
	}

	@Override
	public void addColumn(Matching column) {
		MatchingColor matchingColor= column.associatedPricingProblem.color;
		try{
			//Register column with objective
			IloColumn iloColumn=masterData.cplex.column(obj,column.cost);
			//Register column with exactlyOneRedMatching/exactlyOneBlueMatching constr
			if(matchingColor==MatchingColor.RED){
				iloColumn=iloColumn.and(masterData.cplex.column(exactlyOneRedMatchingConstr, 1));
			}else{
				iloColumn=iloColumn.and(masterData.cplex.column(exactlyOneBlueMatchingConstr, 1));
			}
			//Register column with edgeOnlyUsedOnce constraints
			for(int i=0; i<modelData.N-1; i++){
				for(int j=i+1; j<modelData.N; j++){
					if(column.succ[i]==j){
						Edge edge=new Edge(i, j);
						iloColumn=iloColumn.and(masterData.cplex.column(edgeOnlyUsedOnceConstr.get(edge), 1));
					}
				}
			}
			//Register column with subtour elimination constraints
			for(SubtourInequality subtourInequality : masterData.subtourInequalities.keySet()){
				//Test how many edges in the matching enter/leave the cutSet (edges with exactly one endpoint in the cutSet)
				int crossings=0;
				for(Edge edge: column.edges){
					if(subtourInequality.cutSet.contains(edge.getId1()) ^ subtourInequality.cutSet.contains(edge.getId2()))
						crossings++;
				}
				if(crossings>0){
					IloRange subtourConstraint= masterData.subtourInequalities.get(subtourInequality);
					iloColumn=iloColumn.and(masterData.cplex.column(subtourConstraint, crossings));
				}
			}
			
			//Create the variable and store it
			//IloNumVar var=masterData.cplex.numVar(iloColumn, 0, Double.MAX_VALUE, "z_"+matchingColor.name()+"_"+column.associatedPricingProblem.getNrColumns());
			IloNumVar var=masterData.cplex.numVar(iloColumn, 0, Double.MAX_VALUE, "z_"+matchingColor.name()+"_"+masterData.matchingVars.get(matchingColor).size());
			masterData.cplex.add(var);
			masterData.matchingVars.get(matchingColor).put(column, var);
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Matching> getSolution() {
		List<Matching> solution=new ArrayList<>();
		try {
			for(MatchingColor color : MatchingColor.values()){
				Matching[] matchings=masterData.matchingVars.get(color).getKeysAsArray(new Matching[masterData.matchingVars.size()]);
				IloNumVar[] vars=masterData.matchingVars.get(color).getValuesAsArray(new IloNumVar[masterData.matchingVars.size()]);
				double[] values=masterData.cplex.getValues(vars);
				
				//Iterate over each column and add it to the solution if it has a non-zero value
				for(int i=0; i<matchings.length; i++){
					matchings[i].value=values[i];
					if(values[i]>=config.PRECISION){
						solution.add(matchings[i]);
					}
				}
			}
		} catch (IloException e) {
			e.printStackTrace();
		}
		return solution;
	}

	@Override
	public void printSolution() {
		List<Matching> solution=this.getSolution();
		for(Matching m : solution)
			System.out.println(m);
	}

	@Override
	public void close() {
		masterData.cplex.end();
	}

	@Override
	public boolean hasNewCuts(){
		//For convenience, we will precompute values required by the SubtourInequalityGenerator class
		//and store it in the masterData object.
		masterData.edgeValues=new double[modelData.N][modelData.N];
		for(Matching m : this.getSolution()){
			for(Edge edge : m.edges){
				masterData.edgeValues[edge.getId1()][edge.getId2()]+=m.value;
				masterData.edgeValues[edge.getId2()][edge.getId1()]+=m.value;
			}
		}
		return super.hasNewCuts();
	}

	@Override
	public void branchingDecisionPerformed(BranchingDecision bd) {
		//For simplicity, we simply destroy the master problem and rebuild it. Of course, something more sophisticated may be used which retains the master problem.
		this.close(); //Close the old cplex model
		masterData=this.buildModel(); //Create a new model
		cutHandler.setMasterData(masterData); //Inform the cutHandler about the new master model
	}

	@Override
	public void branchingDecisionRewinded(BranchingDecision bd) {
		//No action required
	}
}
