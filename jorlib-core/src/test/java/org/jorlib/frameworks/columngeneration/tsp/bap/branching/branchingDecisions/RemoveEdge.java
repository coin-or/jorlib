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
package org.jorlib.frameworks.columngeneration.tsp.bap.branching.branchingDecisions;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jorlib.frameworks.columngeneration.branchandprice.branchingdecisions.BranchingDecision;
import org.jorlib.frameworks.columngeneration.master.cutGeneration.AbstractInequality;
import org.jorlib.frameworks.columngeneration.tsp.cg.Matching;
import org.jorlib.frameworks.columngeneration.tsp.cg.PricingProblemByColor;
import org.jorlib.frameworks.columngeneration.tsp.cg.master.TSPMasterData;
import org.jorlib.frameworks.columngeneration.tsp.model.TSP;

/**
 * Prevent an edge from being used
 * 
 * @author Joris Kinable
 * @version 22-4-2015
 */
public final class RemoveEdge
    implements BranchingDecision<TSP, Matching, TSPMasterData>
{

    /** Pricing problem **/
    public final PricingProblemByColor pricingProblem;
    /** Edge on which we branch **/
    public final DefaultWeightedEdge edge;

    public RemoveEdge(PricingProblemByColor pricingProblem, DefaultWeightedEdge edge)
    {
        this.pricingProblem = pricingProblem;
        this.edge = edge;
    }

    /**
     * Determine whether the given inequality remains feasible for the child node
     * 
     * @param inequality inequality
     * @return true
     */
    @Override
    public boolean inEqualityIsCompatibleWithBranchingDecision(AbstractInequality<TSP, TSPMasterData> inequality)
    {
        return true; // In this example we only have subtourInequalities. They remain valid,
                     // independent of whether we remove an edge.
    }

    /**
     * Determine whether the given column remains feasible for the child node
     * 
     * @param column column
     * @return true if the column is compliant with the branching decision
     */
    @Override
    public boolean columnIsCompatibleWithBranchingDecision(Matching column)
    {
        return column.associatedPricingProblem != this.pricingProblem
            || !column.edges.contains(edge);
    }

    @Override
    public String toString()
    {
        return "Remove: " + edge + " for pricingProblem: " + pricingProblem;
    }
}
