/* ==========================================
 * jORLib : a free Java OR library
 * ==========================================
 *
 * Project Info:  https://github.com/jkinable/jorlib
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under GPLv3
 *
 */
/* -----------------
 * RemoveEdge.java
 * -----------------
 * (C) Copyright 2015, by Joris Kinable and Contributors.
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
package org.jorlib.demo.frameworks.columnGeneration.bapExample.bap.branching.branchingDecisions;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jorlib.demo.frameworks.columnGeneration.bapExample.cg.Matching;
import org.jorlib.demo.frameworks.columnGeneration.bapExample.cg.PricingProblemByColor;
import org.jorlib.demo.frameworks.columnGeneration.bapExample.model.TSP;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.Inequality;

/**
 * Prevent an edge from being used
 * @author Joris Kinable
 * @version 22-4-2015
 */
public class RemoveEdge implements BranchingDecision<TSP,Matching> {

    /** Pricing problem **/
    public final PricingProblemByColor pricingProblem;
    /** Edge on which we branch **/
    public final DefaultWeightedEdge edge;

    public RemoveEdge(PricingProblemByColor pricingProblem, DefaultWeightedEdge edge){
        this.pricingProblem=pricingProblem;
        this.edge=edge;
    }

    /**
     * Determine whether the given inequality remains feasible for the child node
     * @param inequality inequality
     * @return true
     */
    @Override
    public boolean inEqualityIsCompatibleWithBranchingDecision(Inequality inequality) {
        return true;  //In this example we only have subtourInequalities. They remain valid, independent of whether we remove an edge.
    }

    /**
     * Determine whether the given column remains feasible for the child node
     * @param column column
     * @return true if the column is compliant with the branching decision
     */
    @Override
    public boolean columnIsCompatibleWithBranchingDecision(Matching column) {
        return column.associatedPricingProblem != this.pricingProblem || !column.edges.contains(edge);
    }

    @Override
    public String toString(){
        return "Remove: "+edge+" for pricingProblem: "+pricingProblem;
    }
}
