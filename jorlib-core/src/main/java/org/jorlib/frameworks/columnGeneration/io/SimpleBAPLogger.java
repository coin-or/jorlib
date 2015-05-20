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
 * SimpleBAPLogger.java
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
import org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Simple class which logs events from the Branch and Price class
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class SimpleBAPLogger implements BAPListener{
    protected BufferedWriter writer;
    protected NumberFormat formatter;

    /** Branch and price node ID of node currently being solved**/
    protected int bapNodeID;
    /** Parent node ID, -1 if root node **/
    protected int parentNodeID;
    /** Best integer solution **/
    protected int globalUB;
    /** Lower bound on the BAP node **/
    protected double lowerBoundNode;
    /** What to do with the node, i.e. prune (based on obj), Infeasible, Integer, Fractional, or Inconclusive if the nodeStatus could not be determined (e.g. due to time limit) **/
    protected NodeResultStatus nodeStatus;
    /** Number of nodes currently in the queue **/
    protected int nodesInQueue;

    //Colgen stats
    /** Number of column generation iterations **/
    protected int cgIterations;

    //Master problem
    /** Counts how much time is spent on solving master problems **/
    protected long timeSolvingMaster;
    /** Objective value of bap node **/
    protected double nodeValue;

    //Pricing Problem
    /** Counts how much time is spend on solving pricing problems **/
    protected long timeSolvingPricing;
    /** Total number of generated columns by the pricing problems **/
    protected int nrGeneratedColumns;


    /**
     * Create a new logger which writes its output the the file specified
     * @param outputFile file to redirect the output to.
     */
    public <B extends AbstractBranchAndPrice> SimpleBAPLogger(B branchAndPrice, File outputFile){
        try {
            writer=new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        formatter=new DecimalFormat("#0.00");
        branchAndPrice.addBranchAndPriceEventListener(this);
    }

    /**
     * Write a single line of text to the output file
     * @param line line of text to be written
     */
    protected void writeLine(String line){
        try {
            writer.write(line);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reset the values
     */
    protected void reset(){
        bapNodeID=-1;
        parentNodeID=-1;
        globalUB=-1;
        lowerBoundNode=-1;
        cgIterations=0;
        timeSolvingMaster=0;
        nodeValue=-1;
        timeSolvingPricing=0;
        nrGeneratedColumns=0;
        nodesInQueue=-1;
    }

    /**
     * Construct a single line in the log file, and write it to the output file
     */
    protected void constructAndWriteLine(){
        this.writeLine(String.valueOf(bapNodeID) + "\t" + parentNodeID + "\t" + globalUB + "\t" + lowerBoundNode + "\t" + formatter.format(nodeValue) + "\t" + cgIterations + "\t" + timeSolvingMaster + "\t" + timeSolvingPricing + "\t" + nrGeneratedColumns + "\t" + nodeStatus + "\t" + nodesInQueue);
    }

    @Override
    public void startBAP(StartBAPEvent startBAPEvent) {
        this.writeLine("BAPNodeID \t parentNodeID \t globalUB \t nodeLB \t nodeValue \t cgIterations \t t_master \t t_pricing \t nrGenColumns \t solutionStatus \t nodesInQueue");
    }

    @Override
    public void stopBAP(StopBAPEvent startBAPEvent) {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pruneNode(PruneNodeEvent pruneNodeEvent) {
        this.nodeStatus =NodeResultStatus.PRUNED;
        this.lowerBoundNode=pruneNodeEvent.nodeBound;
        this.constructAndWriteLine();
    }

    @Override
    public void nodeIsInfeasible(NodeIsInfeasibleEvent nodeIsInfeasibleEvent) {
        this.nodeStatus =NodeResultStatus.INFEASIBLE;
        this.constructAndWriteLine();
    }

    @Override
    public void nodeIsInteger(NodeIsIntegerEvent nodeIsIntegerEvent) {
        this.nodeStatus =NodeResultStatus.INTEGER;
        this.constructAndWriteLine();
    }

    @Override
    public void nodeIsFractional(NodeIsFractionalEvent nodeIsFractionalEvent) {
        this.nodeStatus =NodeResultStatus.FRACTIONAL;
        this.constructAndWriteLine();
    }

    @Override
    public void processNextNode(ProcessingNextNodeEvent processingNextNodeEvent) {
        this.reset();
        this.bapNodeID=processingNextNodeEvent.node.nodeID;
        this.parentNodeID=processingNextNodeEvent.node.getParentID();
        this.globalUB=processingNextNodeEvent.globalUB;
        this.nodesInQueue=processingNextNodeEvent.nodesInQueue;
    }

    @Override
    public void finishedColumnGenerationForNode(FinishCGEvent finishCGEvent) {
        this.lowerBoundNode=finishCGEvent.nodeBound;
        this.nodeValue=finishCGEvent.nodeValue;
        this.cgIterations=finishCGEvent.numberOfCGIterations;
        this.timeSolvingMaster=finishCGEvent.masterSolveTime;
        this.timeSolvingPricing=finishCGEvent.pricingSolveTime;
        this.nrGeneratedColumns=finishCGEvent.nrGeneratedColumns;
    }

    @Override
    public void timeOut(TimeOutEvent timeOutEvent){
        this.nodeStatus =NodeResultStatus.INCONCLUSIVE;
        this.constructAndWriteLine();
    }

    @Override
    public void branchCreated(BranchEvent branchEvent) {
        //Ignore this event, not needed by the logger.
    }

    protected enum NodeResultStatus{
        PRUNED, INFEASIBLE, FRACTIONAL, INTEGER, INCONCLUSIVE
    }
}
