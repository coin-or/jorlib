package org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.bap;

import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.Matching;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.PricingProblemByColor;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.model.TSP;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.AbstractBranchAndPrice;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.AbstractBranchCreator;
import org.jorlib.frameworks.columnGeneration.master.MasterFactory;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemSolver;

import java.util.List;

/**
 * Created by jkinable on 4/22/15.
 */
public class BranchAndPrice extends AbstractBranchAndPrice<TSP,Matching, PricingProblemByColor> {

    public BranchAndPrice(TSP modelData, MasterFactory masterFactory,
                          List<PricingProblemByColor> pricingProblems,
                          List<Class<? extends PricingProblemSolver<TSP, Matching, PricingProblemByColor>>> solvers,
                          List<? extends AbstractBranchCreator<TSP, Matching, PricingProblemByColor>> branchCreators,
                          int upperBoundOnObjective,
                          List<Matching> initialSolution){
        super(modelData, masterFactory, pricingProblems, solvers, branchCreators, upperBoundOnObjective, initialSolution);
    }
    @Override
    protected List<Matching> generateArtificialSolution() {
        return null;
    }

    @Override
    protected boolean isIntegralSolution(List<Matching> solution) {
        return solution.size()==pricingProblems.size();
    }
}

//(T modelData,
//        MasterFactory masterFactory,
//        List<V> pricingProblems,
//        List<Class<? extends PricingProblemSolver<T, U, V>>> solvers,
//        List<AbstractBranchCreator<T, U>> branchCreators,
//        int upperBoundOnObjective,
//        List<U> initialSolution)

//List<Class<? extends PricingProblemSolver<T, U, V>>> solvers