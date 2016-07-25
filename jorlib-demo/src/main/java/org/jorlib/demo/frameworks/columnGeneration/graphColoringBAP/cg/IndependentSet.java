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
 * IndependentSet.java
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
package org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.cg;

import org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.model.ColoringGraph;
import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;

import java.util.Set;

/**
 * Definition of a column.
 *
 * @author Joris Kinable
 * @version 29-6-2016
 */
public final class IndependentSet extends AbstractColumn<ColoringGraph, ChromaticNumberPricingProblem>{

    /** Vertices in the independent set **/
    public final Set<Integer> vertices;
    /** Cost of this column in the objective of the Master Problem **/
    public final int cost;

    /**
     * Constructs a new column
     *
     * @param associatedPricingProblem Pricing problem to which this column belongs
     * @param isArtificial             Is this an artificial column?
     * @param creator                  Who/What created this column?
     * @param vertices Vertices in the independent set
     * @param cost cost of the independent set
     */
    public IndependentSet(ChromaticNumberPricingProblem associatedPricingProblem, boolean isArtificial, String creator, Set<Integer> vertices, int cost) {
        super(associatedPricingProblem, isArtificial, creator);
        this.vertices=vertices;
        this.cost=cost;
    }


    @Override
    public boolean equals(Object o) {
        if(this==o)
            return true;
        else if(!(o instanceof IndependentSet))
            return false;
        IndependentSet other=(IndependentSet) o;
        return this.vertices.equals(other.vertices) && this.isArtificialColumn == other.isArtificialColumn;
    }

    @Override
    public int hashCode() {
        return vertices.hashCode();
    }

    @Override
    public String toString() {
        return "Value: "+ this.value+" artificial: "+isArtificialColumn+" set: "+vertices.toString();
    }

}
