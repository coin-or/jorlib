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
 * DifferentColor.java
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
package org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.bap.branching.branchingDecisions;

import org.jgrapht.util.VertexPair;
import org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.cg.IndependentSet;
import org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.model.ColoringGraph;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.AbstractInequality;

/**
 * Ensure that two vertices are colored differently
 * @author Joris Kinable
 * @version 29-6-2016
 */
public final class DifferentColor implements BranchingDecision<ColoringGraph, IndependentSet> {

    /** Vertices to be colored differently **/
    public final VertexPair<Integer> vertexPair;

    public DifferentColor(VertexPair<Integer> vertexPair){
        this.vertexPair=vertexPair;
    }

    /**
     * Determine whether the given column remains feasible for the child node
     * @param column column
     * @return true if the column is compliant with the branching decision
     */
    @Override
    public boolean columnIsCompatibleWithBranchingDecision(IndependentSet column) {
        return !(column.vertices.contains(vertexPair.getFirst()) && column.vertices.contains(vertexPair.getSecond()));
    }

    /**
     * Determine whether the given inequality remains feasible for the child node
     * @param inequality inequality
     * @return true
     */
    @Override
    public boolean inEqualityIsCompatibleWithBranchingDecision(AbstractInequality inequality) {
        return true; //Cuts are not added in this example
    }

    @Override
    public String toString(){
        return "DifferentColor "+vertexPair;
    }
}
