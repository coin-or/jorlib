/* ==========================================
 * jORLib : Java Operations Research Library
 * ==========================================
 *
 * Project Info:  http://www.coin-or.org/projects/jORLib.xml
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015-2016, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 * as published by the Free Software Foundation.
 */
package org.jorlib.demo.frameworks.columngeneration.tspcg.cg.master;

import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jorlib.demo.frameworks.columngeneration.tspcg.cg.Matching;
import org.jorlib.demo.frameworks.columngeneration.tspcg.cg.PricingProblemByColor;
import org.jorlib.demo.frameworks.columngeneration.tspcg.cg.master.cuts.SubtourInequality;
import org.jorlib.demo.frameworks.columngeneration.tspcg.model.TSP;
import org.jorlib.frameworks.columngeneration.master.MasterData;
import org.jorlib.frameworks.columngeneration.util.OrderedBiMap;

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
public final class TSPMasterData
    extends MasterData<TSP, Matching, PricingProblemByColor, IloNumVar>
{

    /** Cplex instance **/
    public final IloCplex cplex;
    /** List of pricing problems **/
    public final List<PricingProblemByColor> pricingProblems;

    /** Record how often a particular edge is used (only non-zero edges are considered) **/
    public Map<DefaultWeightedEdge, Double> edgeValueMap;

    /** Mapping of the Subtour inequalities to constraints in the cplex model **/
    public final Map<SubtourInequality, IloRange> subtourInequalities;

    public TSPMasterData(
        IloCplex cplex, List<PricingProblemByColor> pricingProblems,
        Map<PricingProblemByColor, OrderedBiMap<Matching, IloNumVar>> varMap)
    {
        super(varMap);
        this.cplex = cplex;
        this.pricingProblems = pricingProblems;
        subtourInequalities = new LinkedHashMap<>();
        edgeValueMap = new HashMap<>();
    }
}
