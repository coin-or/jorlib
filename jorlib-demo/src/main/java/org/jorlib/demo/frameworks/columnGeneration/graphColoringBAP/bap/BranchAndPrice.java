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
 * BranchAndPrice.java
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
package org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.bap;

import org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.cg.ChromaticNumberPricingProblem;
import org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.cg.IndependentSet;
import org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.model.ColoringGraph;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.AbstractBranchAndPrice;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.AbstractBranchCreator;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.BAPNode;
import org.jorlib.frameworks.columnGeneration.master.AbstractMaster;
import org.jorlib.frameworks.columnGeneration.master.MasterData;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblemSolver;

import java.util.*;

/**
 * Branch-and-Price implementation
 * @author Joris Kinable
 * @version 29-6-2016
 */
public final class BranchAndPrice extends AbstractBranchAndPrice<ColoringGraph, IndependentSet, ChromaticNumberPricingProblem> {

    public BranchAndPrice(ColoringGraph dataModel,
                          AbstractMaster<ColoringGraph, IndependentSet, ChromaticNumberPricingProblem, ? extends MasterData> master,
                          ChromaticNumberPricingProblem pricingProblem,
                          List<Class<? extends AbstractPricingProblemSolver<ColoringGraph, IndependentSet, ChromaticNumberPricingProblem>>> solvers,
                          List<? extends AbstractBranchCreator<ColoringGraph, IndependentSet, ChromaticNumberPricingProblem>> abstractBranchCreators,
                          double lowerBoundOnObjective,
                          double upperBoundOnObjective) {
        super(dataModel, master, pricingProblem, solvers, abstractBranchCreators, lowerBoundOnObjective, upperBoundOnObjective);
    }

    /**
     * Generates an artificial solution. Columns in the artificial solution are of high cost such that they never end up in the final solution
     * if a feasible solution exists, since any feasible solution is assumed to be cheaper than the artificial solution. The artificial solution is used
     * to guarantee that the master problem has a feasible solution.
     *
     * @return artificial solution
     */
    @Override
    protected List<IndependentSet> generateInitialFeasibleSolution(BAPNode<ColoringGraph, IndependentSet> node) {
        List<IndependentSet> artificialSolution=new ArrayList<>();
        for(int v=0; v<dataModel.getNrVertices(); v++){
            artificialSolution.add(new IndependentSet(pricingProblems.get(0), true, "Artificial", new HashSet<>(Collections.singletonList(v)), objectiveIncumbentSolution));
        }
        return artificialSolution;
    }

    /**
     * Checks whether the given node is integer. A solution is integer if every vertex is contained in exactly 1 independent set,
     * that is, if every vertex is assigned a single color.
     * @param node Node in the Branch-and-Price tree
     * @return true if the solution is an integer solution
     */
    @Override
    protected boolean isIntegerNode(BAPNode<ColoringGraph, IndependentSet> node) {
        int vertexCount=0;
        for(IndependentSet column : node.getSolution())
            vertexCount+= column.vertices.size();
        return vertexCount==dataModel.getNrVertices();
    }
}
