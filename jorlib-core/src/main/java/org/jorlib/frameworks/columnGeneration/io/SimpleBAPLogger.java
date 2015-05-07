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
 * Created by jkinable on 5/5/15.
 */
public class SimpleBAPLogger implements BAPListener{
    protected BufferedWriter writer;
    protected NumberFormat formatter;


    //Branch and price
    protected int bapNodeID; //Branch and price node ID
    protected int parentNodeID; //Parent node ID, -1 if root node
    protected int globalUB; //Best integer solution
    protected double lowerBoundNode; //Lower bound on the BAP node
    protected NodeResultStatus nodeStatus; //What to do with the node, i.e. prune (based on obj), Infeasible, Integer, Fractional, or Inconclusive if the nodeStatus could not be determined (e.g. due to time limit)

    //Colgen stats
    protected int cgIterations=0; //Number of column generation iterations

    //Master problem
    protected long timeSolvingMaster; //Counts how much time is spent on solving master problems
    protected double nodeValue; //Objective value of bap node

    //Pricing Problem
    protected long timeSolvingPricing; //Counts how much time is spend on solving pricing problems
    protected int nrGeneratedColumns; //Total number of generated columns by the pricing problems


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
     * @param line
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
        cgIterations=-1;
        timeSolvingMaster=-1;
        nodeValue=-1;
        timeSolvingPricing=-1;
        nrGeneratedColumns=-1;
    }

    /**
     * Construct a single line in the log file, and write it to the output file
     */
    protected void constructAndWriteLine(){
        StringBuilder builder = new StringBuilder();
        builder.append(bapNodeID);
        builder.append("\t");
        builder.append(parentNodeID);
        builder.append("\t");
        builder.append(globalUB);
        builder.append("\t");
        builder.append(lowerBoundNode);
        builder.append("\t");
        builder.append(formatter.format(nodeValue));
        builder.append("\t");
        builder.append(cgIterations);
        builder.append("\t");
        builder.append(timeSolvingMaster);
        builder.append("\t");
        builder.append(timeSolvingPricing);
        builder.append("\t");
        builder.append(nrGeneratedColumns);
        builder.append("\t");
        builder.append(nodeStatus);
        this.writeLine(builder.toString());
    }

    @Override
    public void startBAP(StartBAPEvent startBAPEvent) {
        this.writeLine("BAPNodeID \t parentNodeID \t globalUB \t nodeLB \t nodeValue \t cgIterations \t t_master \t t_pricing \t nrGenColumns \t solutionStatus");
    }

    @Override
    public void stopBAP(StopBAPEvent startBAPEvent) {
        try {
            writer.close();
            System.out.println("logger closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pruneNode(PruneNodeEvent pruneNodeEvent) {
        this.nodeStatus =NodeResultStatus.PRUNED;
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
        this.parentNodeID=processingNextNodeEvent.node.getAncestorID();
        this.globalUB=processingNextNodeEvent.globalUB;
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
