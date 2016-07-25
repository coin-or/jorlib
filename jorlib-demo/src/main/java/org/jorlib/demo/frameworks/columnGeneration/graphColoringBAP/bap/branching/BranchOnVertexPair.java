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
 * BranchOnVertexPair.java
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
package org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.bap.branching;

import org.jgrapht.util.VertexPair;
import org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.bap.branching.branchingDecisions.DifferentColor;
import org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.bap.branching.branchingDecisions.SameColor;
import org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.cg.ChromaticNumberPricingProblem;
import org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.cg.IndependentSet;
import org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.model.ColoringGraph;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.AbstractBranchCreator;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.BAPNode;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Class which creates new branches in the Branch-and-Price tree. This particular class branches on a pair of vertices, thereby creating
 * two branches. In one branch, these vertices receive the same color, whereas in the other branch they are colored differently
 * @author Joris Kinable
 * @version 29-6-2016
 */
public final class BranchOnVertexPair extends AbstractBranchCreator<ColoringGraph, IndependentSet, ChromaticNumberPricingProblem> {

    /** Pair of vertices to branch on **/
    VertexPair<Integer> candidateVertexPair=null;

    public BranchOnVertexPair(ColoringGraph dataModel, ChromaticNumberPricingProblem pricingProblem) {
        super(dataModel, pricingProblem);
    }

    /**
     * Determine on which edge from the red or blue matchings we are going to branch.
     * @param solution Fractional column generation solution
     * @return true if a fractional edge exists
     */
    @Override
    protected boolean canPerformBranching(List<IndependentSet> solution) {
        //Find a vertex v1 which is in BOTH independent set s1 and independent set s2, and a vertex v2 which is ONLY in s1.
        int v1=-1;
        int v2=-1;
        boolean foundPair=false;
        for(int i=0; i<solution.size()-1 && !foundPair; i++){
            for(int j=i+1; j<solution.size() && !foundPair; j++){
                IndependentSet s1=solution.get(i);
                IndependentSet s2=solution.get(j);
                v1=v2=-1;

                for(Iterator<Integer> it=s1.vertices.iterator(); it.hasNext() && !foundPair; ){
                    int v=it.next();
                    if(v1==-1 && s2.vertices.contains(v))
                        v1=v;
                    else if(v2 == -1 && !s2.vertices.contains(v))
                        v2=v;
                    foundPair=!(v1==-1 || v2==-1);
                }
            }
        }
        if(foundPair)
            candidateVertexPair=new VertexPair<>(v1, v2);
        return foundPair;
    }

    /**
     * Create the branches:
     * <ol>
     * <li>branch 1: pair of vertices {@code vertexPair} must be assigned the same color,</li>
     * <li>branch 2: pair of vertices {@code vertexPair} must be assigned different colors,</li>
     * </ol>
     * @param parentNode Fractional node on which we branch
     * @return List of child nodes
     */
    @Override
    protected List<BAPNode<ColoringGraph, IndependentSet>> getBranches(BAPNode<ColoringGraph, IndependentSet> parentNode) {
        //Branch 1: same color:
        SameColor branchingDecision1=new SameColor(candidateVertexPair);
        BAPNode<ColoringGraph,IndependentSet> node2=this.createBranch(parentNode, branchingDecision1, parentNode.getSolution(), parentNode.getInequalities());

        //Branch 2: different colors:
        DifferentColor branchingDecision2=new DifferentColor(candidateVertexPair);
        BAPNode<ColoringGraph,IndependentSet> node1=this.createBranch(parentNode, branchingDecision2, parentNode.getSolution(), parentNode.getInequalities());

        return Arrays.asList(node1, node2);
    }
}
