package org.jorlib.demo.frameworks.columngeneration.graphcoloringbap;

import org.jorlib.demo.frameworks.columngeneration.graphcoloringbap.cg.ChromaticNumberPricingProblem;
import org.jorlib.demo.frameworks.columngeneration.graphcoloringbap.cg.IndependentSet;
import org.jorlib.demo.frameworks.columngeneration.graphcoloringbap.model.ColoringGraph;
import org.jorlib.frameworks.columngeneration.branchandprice.BAPNode;
import org.jorlib.frameworks.columngeneration.branchandprice.eventhandling.*;
import org.jorlib.frameworks.columngeneration.colgenmain.AbstractColumn;
import org.jorlib.frameworks.columngeneration.model.ModelInterface;
import org.jorlib.frameworks.columngeneration.pricing.AbstractPricingProblem;

import java.util.List;

/**
 * Created by jkinable on 9/24/16.
 */
public class Logger implements BAPListener<ColoringGraph, IndependentSet>, CGListener<ColoringGraph, IndependentSet> {
    @Override
    public void startBAP(StartEvent startEvent) {

    }

    @Override
    public void finishBAP(FinishEvent finishEvent) {

    }

    @Override
    public void pruneNode(PruneNodeEvent<ColoringGraph, IndependentSet> pruneNodeEvent) {

    }

    @Override
    public void nodeIsInfeasible(NodeIsInfeasibleEvent<ColoringGraph, IndependentSet> nodeIsInfeasibleEvent) {

    }

    @Override
    public void nodeIsInteger(NodeIsIntegerEvent<ColoringGraph, IndependentSet> nodeIsIntegerEvent) {

    }

    @Override
    public void nodeIsFractional(NodeIsFractionalEvent<ColoringGraph, IndependentSet> nodeIsFractionalEvent) {

    }

    @Override
    public void processNextNode(ProcessingNextNodeEvent<ColoringGraph, IndependentSet> processingNextNodeEvent) {

    }

    @Override
    public void finishedColumnGenerationForNode(FinishProcessingNodeEvent<ColoringGraph, IndependentSet> finishProcessingNodeEvent) {

    }

    @Override
    public void timeLimitExceeded(TimeLimitExceededEvent timeLimitExceededEvent) {

    }

    @Override
    public void branchCreated(BranchEvent<ColoringGraph, IndependentSet> branchEvent) {

    }

    @Override
    public void startCG(StartEvent startEvent) {

    }

    @Override
    public void finishCG(FinishEvent finishEvent) {
    }

    @Override
    public void startMaster(StartMasterEvent startMasterEvent) {

    }

    @Override
    public void finishMaster(FinishMasterEvent finishMasterEvent) {

    }

    @Override
    public void startPricing(StartPricingEvent startPricing) {

    }

    @Override
    public void finishPricing(FinishPricingEvent<ColoringGraph, IndependentSet> finishPricingEvent) {
        ChromaticNumberPricingProblem pricing=finishPricingEvent.columns.get(0).associatedPricingProblem;
    }
}
