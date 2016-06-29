package org.jorlib.demo.frameworks.columnGeneration.bapExample2.bap;

import org.jorlib.demo.frameworks.columnGeneration.bapExample2.cg.ChromaticNumberPricingProblem;
import org.jorlib.demo.frameworks.columnGeneration.bapExample2.cg.IndependentSet;
import org.jorlib.demo.frameworks.columnGeneration.bapExample2.model.ColoringGraph;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.AbstractBranchAndPrice;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.AbstractBranchCreator;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.BAPNode;
import org.jorlib.frameworks.columnGeneration.master.AbstractMaster;
import org.jorlib.frameworks.columnGeneration.master.MasterData;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblemSolver;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jkinable on 6/28/16.
 */
public class BranchAndPrice extends AbstractBranchAndPrice<ColoringGraph, IndependentSet, ChromaticNumberPricingProblem> {

    public BranchAndPrice(ColoringGraph dataModel,
                          AbstractMaster<ColoringGraph, IndependentSet, ChromaticNumberPricingProblem, ? extends MasterData> master,
                          ChromaticNumberPricingProblem pricingProblem,
                          List<Class<? extends AbstractPricingProblemSolver<ColoringGraph, IndependentSet, ChromaticNumberPricingProblem>>> solvers,
                          List<? extends AbstractBranchCreator<ColoringGraph, IndependentSet, ChromaticNumberPricingProblem>> abstractBranchCreators,
                          double lowerBoundOnObjective,
                          double upperBoundOnObjective) {
        super(dataModel, master, pricingProblem, solvers, abstractBranchCreators, lowerBoundOnObjective, upperBoundOnObjective);
    }

    @Override
    protected List<IndependentSet> generateArtificialSolution() {
        return Collections.emptyList();
    }

    /**
     * Checks whether the given node is integer. A solution is integer if every vertex is contained in exactly 1 independent set,
     * that is, if every vertex is assigned a single color.
     * @param node Node in the Branch-and-Price tree
     * @return true if the solution is an integer solution
     */
    @Override
    protected boolean isIntegerNode(BAPNode<ColoringGraph, IndependentSet> node) {
        /*Set<Integer> assignedVertices=new HashSet<>();
        for(IndependentSet column : node.getSolution()){
            for(int v : column.vertices)
                if(assignedVertices.contains(v))
                    return false;
            assignedVertices.addAll(column.vertices);
        }
        return true;*/
        int vertexCount=0;
        for(IndependentSet column : node.getSolution())
            vertexCount+= column.vertices.size();
        return vertexCount==dataModel.getNrVertices();
    }
}
