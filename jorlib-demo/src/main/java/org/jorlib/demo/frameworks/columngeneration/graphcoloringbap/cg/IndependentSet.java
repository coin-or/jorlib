/* ==========================================
 * jORLib : Java Operations Research Library
 * ==========================================
 *
 * Project Info:  http://www.coin-or.org/projects/jORLib.xml
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2016-2016, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 * as published by the Free Software Foundation.
 */
package org.jorlib.demo.frameworks.columngeneration.graphcoloringbap.cg;

import org.jorlib.demo.frameworks.columngeneration.graphcoloringbap.model.ColoringGraph;
import org.jorlib.frameworks.columngeneration.colgenmain.AbstractColumn;

import java.util.Set;

/**
 * Definition of a column.
 *
 * @author Joris Kinable
 * @version 29-6-2016
 */
public final class IndependentSet
    extends AbstractColumn<ColoringGraph, ChromaticNumberPricingProblem>
{

    /** Vertices in the independent set **/
    public final Set<Integer> vertices;
    /** Cost of this column in the objective of the Master Problem **/
    public final double cost;

    /**
     * Constructs a new column
     *
     * @param associatedPricingProblem Pricing problem to which this column belongs
     * @param isVolatile Is this column volatile?
     * @param creator Who/What created this column?
     * @param vertices Vertices in the independent set
     * @param cost cost of the independent set
     */
    public IndependentSet(
        ChromaticNumberPricingProblem associatedPricingProblem, boolean isVolatile,
        String creator, Set<Integer> vertices, double cost)
    {
        super(associatedPricingProblem, isVolatile, creator);
        this.vertices = vertices;
        this.cost = cost;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        else if (!(o instanceof IndependentSet))
            return false;
        IndependentSet other = (IndependentSet) o;
        return this.vertices.equals(other.vertices)
            && this.isVolatile == other.isVolatile;
    }

    @Override
    public int hashCode()
    {
        return vertices.hashCode();
    }

    @Override
    public String toString()
    {
        return "Value: " + this.value + " artificial: " + isVolatile + " set: "
            + vertices.toString();
    }

}
