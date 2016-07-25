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
 * ColoringMasterData.java
 * -----------------
 * (C) Copyright 2016, by Joris Kinable and Contributors.
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
package org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.cg.master;

import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.cg.ChromaticNumberPricingProblem;
import org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.cg.IndependentSet;
import org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.model.ColoringGraph;
import org.jorlib.frameworks.columnGeneration.master.MasterData;
import org.jorlib.frameworks.columnGeneration.util.OrderedBiMap;

import java.util.Map;

/**
 * Container which stores information coming from the master problem. It contains:
 * <ul>
 * <li>a reference to the cplex model</li>
 * <li>reference to the pricing problem</li>
 * </ul>
 * @author Joris Kinable
 * @version 29-6-2016
 */
public final class ColoringMasterData extends MasterData<ColoringGraph, IndependentSet, ChromaticNumberPricingProblem, IloNumVar> {

    /** Cplex instance **/
    public final IloCplex cplex;

    /**
     * Creates a new MasterData object
     *
     * @param cplex cplex instance
     * @param varMap A bi-directional map which stores the variables. The first key is the pricing problem, the second key is a column and the value is a variable object, e.g. an IloNumVar in cplex.
     */
    public ColoringMasterData(IloCplex cplex,
                              Map<ChromaticNumberPricingProblem, OrderedBiMap<IndependentSet, IloNumVar>> varMap) {
        super(varMap);
        this.cplex=cplex;
    }
}
