/* ==========================================
 * jORLib : Java Operations Research Library
 * ==========================================
 *
 * Project Info:  http://www.coin-or.org/projects/jORLib.xml
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015-2016, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 * as published by the Free Software Foundation.
 */
package org.jorlib.demo.frameworks.columngeneration.tspbap.cg;

import java.util.Arrays;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jorlib.demo.frameworks.columngeneration.tspbap.model.TSP;
import org.jorlib.frameworks.columngeneration.colgenmain.AbstractColumn;

/**
 * Definition of a column. For the TSP example, each column is a perfect matching.
 *
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public final class Matching
    extends AbstractColumn<TSP, PricingProblemByColor>
{

    /** Edges in the matching **/
    public final Set<DefaultWeightedEdge> edges;
    /**
     * successor array. succ[2]=4 and succ[4]=2 models that edge (2,4) is contained in the matching.
     **/
    public final int[] succ;
    /** Weighted cost of the matching **/
    public final double cost;

    /**
     * Creates a new column (matching)
     * 
     * @param creator who created the matching
     * @param isVolatile indicates whether column is volatile
     * @param associatedPricingProblem pricing problem for which the matching is created
     * @param edges edges in the matching
     * @param succ successor array
     * @param cost cost of matching (sum of edge lengths)
     */
    public Matching(
        String creator, boolean isVolatile, PricingProblemByColor associatedPricingProblem,
        Set<DefaultWeightedEdge> edges, int[] succ, double cost)
    {
        super(associatedPricingProblem, isVolatile, creator);
        this.edges = edges;
        this.succ = succ;
        this.cost = cost;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        else if (!(o instanceof Matching))
            return false;
        Matching other = (Matching) o;
        return Arrays.equals(this.succ, other.succ)
            && this.isVolatile == other.isVolatile;
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(succ);
    }

    @Override
    public String toString()
    {
        return "Value: " + this.value + " cost: " + this.cost + " color: "
            + associatedPricingProblem.color + " artificial: " + isVolatile + " edges: "
            + edges + " succ: " + Arrays.toString(succ);
    }

}
