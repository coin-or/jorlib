package org.jorlib.demo.frameworks.columnGeneration.bapExample2.cg.master;

import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import org.jorlib.demo.frameworks.columnGeneration.bapExample2.cg.ChromaticNumberPricingProblem;
import org.jorlib.demo.frameworks.columnGeneration.bapExample2.cg.IndependentSet;
import org.jorlib.demo.frameworks.columnGeneration.bapExample2.model.ColoringGraph;
import org.jorlib.frameworks.columnGeneration.master.MasterData;
import org.jorlib.frameworks.columnGeneration.util.OrderedBiMap;

import java.util.Map;

/**
 * Created by jkinable on 6/27/16.
 */
public class ColoringMasterData extends MasterData<ColoringGraph, IndependentSet, ChromaticNumberPricingProblem, IloNumVar> {

    /** Cplex instance **/
    public final IloCplex cplex;
    /** Pricing Problem **/
    public final ChromaticNumberPricingProblem pricingProblem;


    /**
     * Creates a new MasterData object
     *
     * @param varMap A double map which stores the variables. The first key is the pricing problem, the second key is a column and the value is a variable object, e.g. an IloNumVar in cplex.
     */
    public ColoringMasterData(IloCplex cplex,
                              ChromaticNumberPricingProblem pricingProblem,
                              Map<ChromaticNumberPricingProblem, OrderedBiMap<IndependentSet, IloNumVar>> varMap) {
        super(varMap);
        this.cplex=cplex;
        this.pricingProblem=pricingProblem;
    }
}
