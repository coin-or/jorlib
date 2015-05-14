package org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.bap.branching.branchingDecisions;

import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.Matching;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.PricingProblemByColor;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.model.TSP;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.Inequality;
import org.jorlib.io.tspLibReader.graph.Edge;

/**
 * Created by jkinable on 4/22/15.
 */
public class FixEdge implements BranchingDecision<TSP,Matching> {

    /** Pricing problem **/
    public final PricingProblemByColor pricingProblem;
    /** Edge on which we branch **/
    public final Edge edge;

    public FixEdge(PricingProblemByColor pricingProblem, Edge edge){
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
        return true;  //In this example we only have subtourInequalities. They remain valid, independent of whether we fix an edge.
    }

    /**
     * Determine whether the given column remains feasible for the child node
     * @param column column
     * @return true if the column is compliant with the branching decision
     */
    @Override
    public boolean columnIsCompatibleWithBranchingDecision(Matching column) {
       return column.associatedPricingProblem != this.pricingProblem || column.edges.contains(edge);
    }

    @Override
    public String toString(){
        return "FixEdge: "+edge+" for pricingProblem: "+pricingProblem;
    }
}
