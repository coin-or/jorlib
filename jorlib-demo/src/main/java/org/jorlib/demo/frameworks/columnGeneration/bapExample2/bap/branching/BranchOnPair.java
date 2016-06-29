package org.jorlib.demo.frameworks.columnGeneration.bapExample2.bap.branching;

import org.jgrapht.util.VertexPair;
import org.jorlib.demo.frameworks.columnGeneration.bapExample2.bap.branching.branchingDecisions.DifferentColor;
import org.jorlib.demo.frameworks.columnGeneration.bapExample2.bap.branching.branchingDecisions.SameColor;
import org.jorlib.demo.frameworks.columnGeneration.bapExample2.cg.ChromaticNumberPricingProblem;
import org.jorlib.demo.frameworks.columnGeneration.bapExample2.cg.IndependentSet;
import org.jorlib.demo.frameworks.columnGeneration.bapExample2.model.ColoringGraph;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.AbstractBranchCreator;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.BAPNode;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jkinable on 6/28/16.
 */
public class BranchOnPair extends AbstractBranchCreator<ColoringGraph, IndependentSet, ChromaticNumberPricingProblem> {

    VertexPair<Integer> candidateVertexPair=null;

    public BranchOnPair(ColoringGraph dataModel, ChromaticNumberPricingProblem pricingProblem) {
        super(dataModel, pricingProblem);
    }

    @Override
    protected boolean canPerformBranching(List<IndependentSet> solution) {
        int v1=-1;
        int v2=-1;
        for(int i=0; i<solution.size()-1; i++){
            for(int j=i+1; j<solution.size(); j++){
                IndependentSet s1=solution.get(i);
                IndependentSet s2=solution.get(j);

                //Find a vertex v1 which is in BOTH s1 and s2, and a vertex v2 which is ONLY in s1.
                for(Iterator<Integer> it=s1.vertices.iterator(); it.hasNext() && (v1==-1 || v2==-1); ){
                    int v=it.next();
                    if(v1==-1 && s2.vertices.contains(v)){
                        v1=v;
                    }else if(v2 == -1 && !s2.vertices.contains(v)){
                        v2=v;
                    }
                }
            }
        }
        if(v1!=-1 && v2!=-1){
            candidateVertexPair=new VertexPair<>(v1, v2);
            return true;
        }else
            return false;
    }

    @Override
    protected List<BAPNode<ColoringGraph, IndependentSet>> getBranches(BAPNode<ColoringGraph, IndependentSet> parentNode) {
        //Branch 1: remove the edge:
        SameColor branchingDecision1=new SameColor(candidateVertexPair);
        BAPNode<ColoringGraph,IndependentSet> node2=this.createBranch(parentNode, branchingDecision1, parentNode.getSolution(), parentNode.getInequalities());

        //Branch 2: fix the edge:
        DifferentColor branchingDecision2=new DifferentColor(candidateVertexPair);
        BAPNode<ColoringGraph,IndependentSet> node1=this.createBranch(parentNode, branchingDecision2, parentNode.getSolution(), parentNode.getInequalities());

        return Arrays.asList(node1, node2);
    }
}
