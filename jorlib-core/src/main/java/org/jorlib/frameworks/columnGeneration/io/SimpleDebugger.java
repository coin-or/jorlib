/* ==========================================
 * jORLib : a free Java OR library
 * ==========================================
 *
 * Project Info:  https://github.com/jkinable/jorlib
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 *
 */
/* -----------------
 * SimpleDebugger.java
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
package org.jorlib.frameworks.columnGeneration.io;

import org.jorlib.frameworks.columnGeneration.branchAndPrice.AbstractBranchAndPrice;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.BAPNode;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling.*;
import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;
import org.jorlib.frameworks.columnGeneration.colgenMain.ColGen;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.AbstractCutGenerator;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutHandler;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.AbstractInequality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple class which helps debugging Branch-and-Price and Column Generation implementations.
 * The debugger logs events originating from the Branch-and-Price instance, Column Generation instance(s)
 * and the CutHandler.
 *
 * @author Joris Kinable
 * @version 21-5-2015
 */
public class SimpleDebugger implements BAPListener, CGListener, CHListener{

    /** Logger for this class **/
    protected final Logger logger = LoggerFactory.getLogger(SimpleDebugger.class);

    /** Branch-and-Price instance being debugged **/
    protected final AbstractBranchAndPrice bap;
    /** Column Generation instance being debugged **/
    protected final ColGen colGen;
    /** CutHandler instance being debugged **/
    protected final CutHandler cutHandler;

    /** Name of the instance being solved **/
    protected String instanceName;
    /** Best integer solution obtained thus far **/
    protected int bestIntegerSolution;

    /**
     * Creates a debugger for Column Generation instances
     * @param colGen Column generation instance to which the debugger should be attached
     */
    public SimpleDebugger(ColGen colGen){
       this(colGen, null);
    }

    /**
     * Creates a debugger for the Column Generation instance
     * @param colGen Column generation instance to which the debugger should be attached
     * @param cutHandler Cut Handler instance to which the debugger should be attached
     */
    public SimpleDebugger(ColGen colGen, CutHandler cutHandler){
        this.bap=null;
        this.colGen=colGen;
        this.cutHandler=cutHandler;
        colGen.addCGEventListener(this);
        if(cutHandler != null)
            cutHandler.addCHEventListener(this);
    }

    /**
     * Creates a debugger for the Branch-and-Price instance
     * @param bap Branch-and-Price instance
     * @param captureColumnGenerationEventsBAP boolean indicating whether Column Generation events should be captured which are being
     *                                      generated when BAPNodes are being processed.
     */
    public SimpleDebugger(AbstractBranchAndPrice bap, boolean captureColumnGenerationEventsBAP){
        this(bap, null, captureColumnGenerationEventsBAP);
    }

    /**
     * Creates a debugger for the Branch-and-Price instance
     * @param bap Branch-and-Price instance
     * @param cutHandler Cut Handler instance to which the debugger should be attached
     * @param captureColumnGenerationEventsBAP boolean indicating whether Column Generation events should be captured which are being
     *                                      generated when BAPNodes are being processed.
     */
    public SimpleDebugger(AbstractBranchAndPrice bap, CutHandler cutHandler, boolean captureColumnGenerationEventsBAP){
        this.bap=bap;
        this.colGen=null;
        this.cutHandler=cutHandler;
        bap.addBranchAndPriceEventListener(this);
        if(captureColumnGenerationEventsBAP)
            bap.addColumnGenerationEventListener(this);
        if(cutHandler != null)
            cutHandler.addCHEventListener(this);
    }


    @Override
    public void startBAP(StartEvent startEvent) {
        instanceName=startEvent.instanceName;
        bestIntegerSolution=startEvent.objectiveIncumbentSolution;
        logger.debug("BAP solving {} - Initial solution: {}", instanceName, startEvent.objectiveIncumbentSolution);
    }

    @Override
    public void finishBAP(FinishEvent finishEvent) {
        logger.debug("Finished Branch-and-Price for instance {}", instanceName);
    }

    @Override
    public void pruneNode(PruneNodeEvent pruneNodeEvent) {
        logger.debug("Pruning node {}. Bound: {}, best integer solution: {}", new Object[]{pruneNodeEvent.node.nodeID, pruneNodeEvent.nodeBound, pruneNodeEvent.bestIntegerSolution});
    }

    @Override
    public void nodeIsInfeasible(NodeIsInfeasibleEvent nodeIsInfeasibleEvent) {
        logger.debug("Node {} is infeasible.", nodeIsInfeasibleEvent.node.nodeID);
    }

    @Override
    public void nodeIsInteger(NodeIsIntegerEvent nodeIsIntegerEvent) {
        this.bestIntegerSolution=Math.min(this.bestIntegerSolution, nodeIsIntegerEvent.nodeValue);
        logger.debug("Node {} is integer. Objective: {} (best integer solution: {})", new Object[]{nodeIsIntegerEvent.node.nodeID, nodeIsIntegerEvent.nodeValue, bestIntegerSolution});
    }

    @Override
    public void nodeIsFractional(NodeIsFractionalEvent nodeIsFractionalEvent) {
        logger.debug("Node {} is fractional. Objective: {}, bound: {}", new Object[]{nodeIsFractionalEvent.node.nodeID, nodeIsFractionalEvent.nodeValue, nodeIsFractionalEvent.nodeBound});
    }

    @Override
    public void processNextNode(ProcessingNextNodeEvent processingNextNodeEvent) {
        logger.debug("Processing node {} - Nodes remaining in queue: {}",processingNextNodeEvent.node.nodeID, processingNextNodeEvent.nodesInQueue);
    }

    @Override
    public void finishedColumnGenerationForNode(FinishProcessingNodeEvent finishProcessingNodeEvent) {
        //Ignore this event
    }

    @Override
    public void startCG(StartEvent startEvent) {
        if(colGen != null){
            instanceName=startEvent.instanceName;
            bestIntegerSolution=startEvent.objectiveIncumbentSolution;
            logger.debug("CG solving {} - Initial upper bound: {}", instanceName, startEvent.objectiveIncumbentSolution);
        }
    }

    @Override
    public void finishCG(FinishEvent finishEvent) {
        if(colGen != null){
            logger.debug("Finished Column Generation for instance {}", instanceName);
        }
    }

    @Override
    public void startMaster(StartMasterEvent startMasterEvent) {
        logger.debug("=============== MASTER {} ===============", startMasterEvent.columnGenerationIteration);

    }

    @Override
    public void finishMaster(FinishMasterEvent finishMasterEvent) {
        logger.debug("Finished master -> CG objective: {}, CG bound: {}, CG cutoff: {}", new Object[]{finishMasterEvent.objective, finishMasterEvent.boundOnMasterObjective, finishMasterEvent.cutoffValue});
    }

    @Override
    public void startPricing(StartPricingEvent startPricing) {
        logger.debug("=============== PRICING {} ===============", startPricing.columnGenerationIteration);
    }

    @Override
    public void finishPricing(FinishPricingEvent finishPricingEvent) {
        logger.debug("Finished pricing ({} columns generated) -> CG objective: {}, CG bound: {}, CG cutoff: {}", new Object[]{finishPricingEvent.columns.size(), finishPricingEvent.objective, finishPricingEvent.boundOnMasterObjective, finishPricingEvent.cutoffValue});
        for(AbstractColumn<?, ?> column : finishPricingEvent.columns){
            logger.debug(column.toString());
        }
    }

    @Override
    public void timeLimitExceeded(TimeLimitExceededEvent timeLimitExceededEvent) {
        if(timeLimitExceededEvent.node != null)
            logger.debug("Caught timeout exception while processing node {}",timeLimitExceededEvent.node.nodeID);
    }

    @Override
    public void branchCreated(BranchEvent branchEvent) {
        logger.debug("Branching - {} new nodes: ",branchEvent.nrBranches);
        for(BAPNode childNode : branchEvent.childNodes){
            logger.debug("ChildNode {} - {}",childNode.nodeID, childNode.getBranchingDecision().toString());
        }
    }

    @Override
    public void startGeneratingCuts(StartGeneratingCutsEvent startGenerateCutsEvent) {
        logger.debug("=============== GENERATING CUTS ===============");
    }

    @Override
    public void finishGeneratingCuts(FinishGeneratingCutsEvent finishGenerateCutsEvent) {
        Map<AbstractCutGenerator, Integer> cutSummary=new LinkedHashMap<>();
        if(finishGenerateCutsEvent.separatedInequalities.isEmpty())
            logger.debug("No inequalities found!");
        else{
            logger.debug("Cuts have been generated! Summary:");
            for(AbstractInequality inequality : finishGenerateCutsEvent.separatedInequalities){
                if(cutSummary.containsKey(inequality.maintainingGenerator)){
                    cutSummary.put(inequality.maintainingGenerator, cutSummary.get(inequality.maintainingGenerator)+1);
                }else{
                    cutSummary.put(inequality.maintainingGenerator, 1);
                }
            }
            String summary = cutSummary.keySet().stream().map(i -> "-"+i.toString() + ": " + cutSummary.get(i)).collect(Collectors.joining(", "));
            logger.debug(summary);
        }
    }
}
