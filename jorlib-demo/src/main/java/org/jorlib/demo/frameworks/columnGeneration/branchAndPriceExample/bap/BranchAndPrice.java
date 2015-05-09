package org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.bap;

import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.Matching;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.PricingProblemByColor;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.master.Master;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.model.TSP;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.AbstractBranchAndPrice;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.AbstractBranchCreator;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemSolver;
import org.jorlib.io.tspLibReader.graph.Edge;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by jkinable on 4/22/15.
 */
public class BranchAndPrice extends AbstractBranchAndPrice<TSP,Matching, PricingProblemByColor> {

    public BranchAndPrice(TSP modelData,
                          Master master,
                          List<PricingProblemByColor> pricingProblems,
                          List<Class<? extends PricingProblemSolver<TSP, Matching, PricingProblemByColor>>> solvers,
                          List<? extends AbstractBranchCreator<TSP, Matching, PricingProblemByColor>> branchCreators,
                          int upperBoundOnObjective,
                          List<Matching> initialSolution){
        super(modelData, master, pricingProblems, solvers, branchCreators, upperBoundOnObjective, initialSolution);
    }

    @Override
    protected List<Matching> generateArtificialSolution() {
        Matching matching1=new Matching("Artificial", true,	pricingProblems.get(0), bestSolution.get(0).edges,bestSolution.get(0).succ,bestObjective);
        Matching matching2=new Matching("Artificial", true,	pricingProblems.get(1), bestSolution.get(1).edges,bestSolution.get(1).succ,bestObjective);
        return Arrays.asList(matching1, matching2);

    }

    @Override
    protected boolean isIntegralSolution(List<Matching> solution) {
        return solution.size()==pricingProblems.size();
    }
}

