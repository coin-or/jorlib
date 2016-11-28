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
package org.jorlib.frameworks.columngeneration.io;

import org.jorlib.frameworks.columngeneration.branchandprice.AbstractBranchAndPrice;
import org.jorlib.frameworks.columngeneration.branchandprice.BAPNode;
import org.jorlib.frameworks.columngeneration.branchandprice.eventhandling.*;
import org.jorlib.frameworks.columngeneration.colgenmain.AbstractColumn;
import org.jorlib.frameworks.columngeneration.colgenmain.ColGen;
import org.jorlib.frameworks.columngeneration.master.MasterData;
import org.jorlib.frameworks.columngeneration.master.cutGeneration.AbstractCutGenerator;
import org.jorlib.frameworks.columngeneration.master.cutGeneration.CutHandler;
import org.jorlib.frameworks.columngeneration.master.cutGeneration.AbstractInequality;
import org.jorlib.frameworks.columngeneration.model.ModelInterface;
import org.jorlib.frameworks.columngeneration.pricing.AbstractPricingProblem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple class which helps debugging Branch-and-Price and Column Generation implementations. The
 * debugger logs events originating from the Branch-and-Price instance, Column Generation
 * instance(s) and the CutHandler.
 *
 * @author Joris Kinable
 * @version 21-5-2015
 */
public class SimpleDebugger<T extends ModelInterface, U extends AbstractColumn<T, V>, V extends AbstractPricingProblem<T, U>>
    implements BAPListener<T,U>, CGListener<T,U>, CHListener
{

    /** Logger for this class **/
    protected final Logger logger = LoggerFactory.getLogger(SimpleDebugger.class);

    /** Branch-and-Price instance being debugged **/
    protected final AbstractBranchAndPrice<?, ?, ?> bap;
    /** Column Generation instance being debugged **/
    protected final ColGen<?, ?, ?> colGen;
    /** CutHandler instance being debugged **/
    protected final CutHandler<?, ?> cutHandler;

    /** Name of the instance being solved **/
    protected String instanceName;
    /** Objective value of best incumbent solution obtained thus far **/
    protected double objectiveIncumbentSolution;

    /**
     * Creates a debugger for Column Generation instances
     * 
     * @param colGen Column generation instance to which the debugger should be attached
     */
    public SimpleDebugger(ColGen<T, U, V> colGen)
    {
        this(colGen, null);
    }

    /**
     * Creates a debugger for the Column Generation instance
     * 
     * @param colGen Column generation instance to which the debugger should be attached
     * @param cutHandler Cut Handler instance to which the debugger should be attached
     */
    public SimpleDebugger(ColGen<T, U, V> colGen, CutHandler<T, ? extends MasterData<T, U, V, ?>> cutHandler)
    {
        this.bap = null;
        this.colGen = colGen;
        this.cutHandler = cutHandler;
        colGen.addCGEventListener(this);
        if (cutHandler != null)
            cutHandler.addCHEventListener(this);
    }

    /**
     * Creates a debugger for the Branch-and-Price instance
     * 
     * @param bap Branch-and-Price instance
     * @param captureColumnGenerationEventsBAP boolean indicating whether Column Generation events
     *        should be captured which are being generated when BAPNodes are being processed.
     */
    public SimpleDebugger(AbstractBranchAndPrice<T, U, V> bap, boolean captureColumnGenerationEventsBAP)
    {
        this(bap, null, captureColumnGenerationEventsBAP);
    }

    /**
     * Creates a debugger for the Branch-and-Price instance
     * 
     * @param bap Branch-and-Price instance
     * @param cutHandler Cut Handler instance to which the debugger should be attached
     * @param captureColumnGenerationEventsBAP boolean indicating whether Column Generation events
     *        should be captured which are being generated when BAPNodes are being processed.
     */
    public SimpleDebugger(
        AbstractBranchAndPrice<T, U, V> bap, CutHandler<T, ? extends MasterData<T, U, V, ?>> cutHandler, boolean captureColumnGenerationEventsBAP)
    {
        this.bap = bap;
        this.colGen = null;
        this.cutHandler = cutHandler;
        bap.addBranchAndPriceEventListener(this);
        if (captureColumnGenerationEventsBAP)
            bap.addColumnGenerationEventListener(this);
        if (cutHandler != null)
            cutHandler.addCHEventListener(this);
    }

    @Override
    public void startBAP(StartEvent startEvent)
    {
        instanceName = startEvent.instanceName;
        objectiveIncumbentSolution = startEvent.objectiveIncumbentSolution;
        logger.debug(
            "BAP solving {} - Initial solution: {}", instanceName,
            startEvent.objectiveIncumbentSolution);
    }

    @Override
    public void finishBAP(FinishEvent finishEvent)
    {
        logger.debug("Finished Branch-and-Price for instance {}", instanceName);
    }

    @Override
    public void pruneNode(PruneNodeEvent<T,U> pruneNodeEvent)
    {
        logger.debug(
            "Pruning node {}. Bound: {}, best integer solution: {}",
            new Object[] { pruneNodeEvent.node.nodeID, pruneNodeEvent.nodeBound,
                pruneNodeEvent.objectiveIncumbentSolution});
    }

    @Override
    public void nodeIsInfeasible(NodeIsInfeasibleEvent<T,U> nodeIsInfeasibleEvent)
    {
        logger.debug("Node {} is infeasible.", nodeIsInfeasibleEvent.node.nodeID);
    }

    @Override
    public void nodeIsInteger(NodeIsIntegerEvent<T,U> nodeIsIntegerEvent)
    {
        this.objectiveIncumbentSolution = Math.min(this.objectiveIncumbentSolution, nodeIsIntegerEvent.nodeValue);
        logger.debug(
            "Node {} is integer. Objective: {} (best integer solution: {})",
            new Object[] { nodeIsIntegerEvent.node.nodeID, nodeIsIntegerEvent.nodeValue,
                    objectiveIncumbentSolution});
    }

    @Override
    public void nodeIsFractional(NodeIsFractionalEvent<T,U> nodeIsFractionalEvent)
    {
        logger.debug(
            "Node {} is fractional. Objective: {}, bound: {}",
            new Object[] { nodeIsFractionalEvent.node.nodeID, nodeIsFractionalEvent.nodeValue,
                nodeIsFractionalEvent.nodeBound });
    }

    @Override
    public void processNextNode(ProcessingNextNodeEvent<T,U> processingNextNodeEvent)
    {
        logger.debug(
            "Processing node {} - Nodes remaining in queue: {}",
            processingNextNodeEvent.node.nodeID, processingNextNodeEvent.nodesInQueue);
    }

    @Override
    public void finishedColumnGenerationForNode(FinishProcessingNodeEvent<T,U> finishProcessingNodeEvent)
    {
        // Ignore this event
    }

    @Override
    public void startCG(StartEvent startEvent)
    {
        if (colGen != null) {
            instanceName = startEvent.instanceName;
            objectiveIncumbentSolution = startEvent.objectiveIncumbentSolution;
            logger.debug(
                "CG solving {} - Initial upper bound: {}", instanceName,
                startEvent.objectiveIncumbentSolution);
        }
    }

    @Override
    public void finishCG(FinishEvent finishEvent)
    {
        if (colGen != null) {
            logger.debug("Finished Column Generation for instance {}", instanceName);
        }
    }

    @Override
    public void startMaster(StartMasterEvent startMasterEvent)
    {
        logger.debug(
            "=============== MASTER {} ===============",
            startMasterEvent.columnGenerationIteration);

    }

    @Override
    public void finishMaster(FinishMasterEvent finishMasterEvent)
    {
        logger.debug(
            "Finished master -> CG objective: {}, CG bound: {}, CG cutoff: {}",
            new Object[] { finishMasterEvent.objective, finishMasterEvent.boundOnMasterObjective,
                finishMasterEvent.cutoffValue });
    }

    @Override
    public void startPricing(StartPricingEvent startPricing)
    {
        logger.debug(
            "=============== PRICING {} ===============", startPricing.columnGenerationIteration);
    }

    @Override
    public void finishPricing(FinishPricingEvent<T,U> finishPricingEvent)
    {
        logger.debug(
            "Finished pricing ({} columns generated) -> CG objective: {}, CG bound: {}, CG cutoff: {}",
            new Object[] { finishPricingEvent.columns.size(), finishPricingEvent.objective,
                finishPricingEvent.boundOnMasterObjective, finishPricingEvent.cutoffValue });
        for (U column : finishPricingEvent.columns) {
            logger.debug(column.toString());
        }
    }

    @Override
    public void timeLimitExceeded(TimeLimitExceededEvent timeLimitExceededEvent)
    {
        if (timeLimitExceededEvent.node != null)
            logger.debug(
                "Caught timeout exception while processing node {}",
                timeLimitExceededEvent.node.nodeID);
    }

    @Override
    public void branchCreated(BranchEvent<T, U> branchEvent)
    {
        logger.debug("Branching - {} new nodes: ", branchEvent.nrBranches);
        for (BAPNode<?, ?> childNode : branchEvent.childNodes) {
            logger.debug(
                "ChildNode {} - {}", childNode.nodeID, childNode.getBranchingDecision().toString());
        }
    }

    @Override
    public void startGeneratingCuts(StartGeneratingCutsEvent startGenerateCutsEvent)
    {
        logger.debug("=============== GENERATING CUTS ===============");
    }

    @Override
    public void finishGeneratingCuts(FinishGeneratingCutsEvent finishGenerateCutsEvent)
    {
        Map<AbstractCutGenerator<?, ?>, Integer> cutSummary = new LinkedHashMap<>();
        if (finishGenerateCutsEvent.separatedInequalities.isEmpty())
            logger.debug("No inequalities found!");
        else {
            logger.debug("Cuts have been generated! Summary:");
            for (AbstractInequality inequality : finishGenerateCutsEvent.separatedInequalities) {
                if (cutSummary.containsKey(inequality.maintainingGenerator)) {
                    cutSummary.put(
                        inequality.maintainingGenerator,
                        cutSummary.get(inequality.maintainingGenerator) + 1);
                } else {
                    cutSummary.put(inequality.maintainingGenerator, 1);
                }
            }
            String summary = cutSummary
                .keySet().stream().map(i -> "-" + i.toString() + ": " + cutSummary.get(i))
                .collect(Collectors.joining(", "));
            logger.debug(summary);
        }
    }
}
