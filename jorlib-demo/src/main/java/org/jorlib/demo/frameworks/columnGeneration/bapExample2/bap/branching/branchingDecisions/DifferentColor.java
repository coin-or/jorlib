package org.jorlib.demo.frameworks.columnGeneration.bapExample2.bap.branching.branchingDecisions;

import org.jgrapht.util.VertexPair;
import org.jorlib.demo.frameworks.columnGeneration.bapExample2.cg.IndependentSet;
import org.jorlib.demo.frameworks.columnGeneration.bapExample2.model.ColoringGraph;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.AbstractInequality;

/**
 * Created by jkinable on 6/28/16.
 */
public class DifferentColor implements BranchingDecision<ColoringGraph, IndependentSet> {

    public final VertexPair<Integer> vertexPair;

    public DifferentColor(VertexPair<Integer> vertexPair){
        this.vertexPair=vertexPair;
    }

    @Override
    public boolean columnIsCompatibleWithBranchingDecision(IndependentSet column) {
        return !(column.vertices.contains(vertexPair.getFirst()) && column.vertices.contains(vertexPair.getSecond()));
    }

    @Override
    public boolean inEqualityIsCompatibleWithBranchingDecision(AbstractInequality inequality) {
        return true;
    }

    @Override
    public String toString(){
        return "DifferentColor "+vertexPair;
    }
}
