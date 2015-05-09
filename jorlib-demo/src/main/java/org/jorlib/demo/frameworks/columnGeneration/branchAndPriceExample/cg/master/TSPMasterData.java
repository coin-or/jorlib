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
 * TSPMasterData.java
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

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.Matching;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.master.cuts.SubtourInequality;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.model.MatchingColor;
import org.jorlib.frameworks.columnGeneration.master.MasterData;
import org.jorlib.frameworks.columnGeneration.util.OrderedBiMap;

/**
 * Container which stores information coming from the master problem.
 *
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public class TSPMasterData extends MasterData{

	//Cplex instance
	public final IloCplex cplex;

	//Variables:

	//Maintain a separate storage of columns for each pricing problem
	public final EnumMap<MatchingColor, OrderedBiMap<Matching, IloNumVar>> matchingVars;
	//For each edge in the graph, record how often it is used (aggregated over all columns)
	protected double[][] edgeValues;


	//Inequalities:

	//Subtour inequalities
	public final Map<SubtourInequality, IloRange> subtourInequalities;
	
	public TSPMasterData(IloCplex cplex,
							EnumMap<MatchingColor, OrderedBiMap<Matching, IloNumVar>> matchingVars){
		this.cplex=cplex;
		this.matchingVars=matchingVars;
		subtourInequalities=new LinkedHashMap<SubtourInequality, IloRange>();
	}
	
	public double[][] getEdgeValues(){
		return edgeValues;
	}
}
