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
package org.jorlib.frameworks.columnGeneration.tsp.cg.master;

import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jorlib.frameworks.columnGeneration.master.MasterData;
import org.jorlib.frameworks.columnGeneration.tsp.cg.Matching;
import org.jorlib.frameworks.columnGeneration.tsp.cg.PricingProblemByColor;
import org.jorlib.frameworks.columnGeneration.tsp.cg.master.cuts.SubtourInequality;
import org.jorlib.frameworks.columnGeneration.tsp.model.TSP;
import org.jorlib.frameworks.columnGeneration.util.OrderedBiMap;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Container which stores information coming from the master problem. It contains:
 * <ul>
 * <li>a reference to the cplex model</li>
 * <li>a list of pricing problems</li>
 * <li>a mapping of subtour inequalities to the constraints in the cplex model</li>
 * </ul>
 *
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public final class TSPMasterData extends MasterData<TSP, Matching, PricingProblemByColor, IloNumVar> {

	/** Cplex instance **/
	public final IloCplex cplex;
	/** List of pricing problems **/
	public final List<PricingProblemByColor> pricingProblems;

	/** Record how often a particular edge is used (only non-zero edges are considered) **/
	public Map<DefaultWeightedEdge, Double> edgeValueMap;

	/** Mapping of the Subtour inequalities to constraints in the cplex model **/
	public final Map<SubtourInequality, IloRange> subtourInequalities;
	
	public TSPMasterData(IloCplex cplex,
						 List<PricingProblemByColor> pricingProblems,
						 Map<PricingProblemByColor, OrderedBiMap<Matching, IloNumVar>> varMap){
		super(varMap);
		this.cplex=cplex;
		this.pricingProblems=pricingProblems;
		subtourInequalities=new LinkedHashMap<>();
		edgeValueMap=new HashMap<>();
	}
}
