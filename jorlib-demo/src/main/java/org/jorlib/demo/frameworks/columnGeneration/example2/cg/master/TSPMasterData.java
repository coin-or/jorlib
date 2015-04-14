package org.jorlib.demo.frameworks.columnGeneration.example2.cg.master;

import java.util.LinkedHashMap;
import java.util.Map;

import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

import org.jorlib.demo.frameworks.columnGeneration.example2.cg.Matching;
import org.jorlib.demo.frameworks.columnGeneration.example2.cg.master.cuts.SubtourInequality;
import org.jorlib.frameworks.columnGeneration.master.MasterData;
import org.jorlib.frameworks.columnGeneration.util.OrderedBiMap;

public class TSPMasterData extends MasterData{

	//Cplex instance
	public final IloCplex cplex;
	//Variables
	public final OrderedBiMap<Matching, IloNumVar> matchingVars; 
	//Subtour inequalities
	public final Map<SubtourInequality, IloRange> subtourInequalities;
	
	
	protected double[][] edgeValues;
	
	public TSPMasterData(IloCplex cplex,
							OrderedBiMap<Matching, IloNumVar> matchingVars){
		this.cplex=cplex;
		this.matchingVars=matchingVars;
		subtourInequalities=new LinkedHashMap<SubtourInequality, IloRange>();
	}
	
	public double[][] getEdgeValues(){
		return edgeValues;
	}
}
