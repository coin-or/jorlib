package org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.bap.branchingDecisions;

import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.Matching;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.PricingProblemByColor;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.model.Edge;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.model.TSP;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.Inequality;

/**
 * Created by jkinable on 4/22/15.
 */
public class RemoveEdge implements BranchingDecision<TSP,Matching> {

    public final PricingProblemByColor pricingProblem;
    public final Edge edge;

    public RemoveEdge(PricingProblemByColor pricingProblem, Edge edge){
        this.pricingProblem=pricingProblem;
        this.edge=edge;
    }


    @Override
    public void executeDecision() {

    }

    @Override
    public void rewindDecision() {

    }

    @Override
    public boolean inEqualityIsCompatibleWithBranchingDecision(Inequality inequality) {
        //In this example we only have subtourInequalities. They remain valid, independent of whether we remove an edge.
        return true;
    }

    @Override
    public boolean columnIsCompatibleWithBranchingDecision(Matching column) {
        return column.associatedPricingProblem != this.pricingProblem || !column.edges.contains(edge);
    }
}
