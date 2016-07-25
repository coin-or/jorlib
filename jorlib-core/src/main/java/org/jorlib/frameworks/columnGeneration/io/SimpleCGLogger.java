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

import org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling.*;
import org.jorlib.frameworks.columnGeneration.colgenMain.ColGen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Simple class which logs events from the Branch-and-Price class
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class SimpleCGLogger implements CGListener{
    protected BufferedWriter writer;
    protected NumberFormat formatter;

    /** Keeps track of the number of column generations. A regular iteration consist of solving the master problem and the pricing problem **/
    protected int cgIteration=-1;

    //Master problem
    /** Counts how much time is spent on solving master problem during iteration it**/
    protected long timeSolvingMaster;
    /** Objective of master problem at the end of iteration it **/
    protected double objective;
    /** Cutoff value **/
    protected int cutoffValue;
    /** Bound on the objective at the end of iteration it **/
    protected double boundOnMasterObjective;

    //Pricing Problem
    /** Counts how much time is spent on solving the pricing problem at iteration it **/
    protected long timeSolvingPricing;
    /** Total number of columns generated during iteration it **/
    protected int nrGeneratedColumns;
    /** Solver which produced new columns during iteration it **/
    protected String pricingSolver;

    /** Boolean indicating whether the pricing problem has been solved after solving the master problem. In some cases,
     * inequalities are added to master problem, after which the master is resolved while skipping the pricing problem.
     */
    boolean pricingProblemHasBeenSkipped=false;

    /**
     * Create a new logger which writes its output the the file specified
     * @param colGen Column generation instance for which this logger is created
     * @param outputFile file to redirect the output to.
     */
    public SimpleCGLogger(ColGen colGen, File outputFile){
        try {
            writer=new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        formatter=new DecimalFormat("#0.00");
        colGen.addCGEventListener(this);
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
        cgIteration++;
        objective=-1;
        cutoffValue =-1;
        boundOnMasterObjective =-1;
        timeSolvingMaster=0;
        timeSolvingPricing=0;
        nrGeneratedColumns=0;
        pricingSolver="";
    }

    /**
     * Construct a single line in the log file, and write it to the output file
     */
    protected void constructAndWriteLine(){
        this.writeLine(String.valueOf(cgIteration) + "\t" + formatter.format(boundOnMasterObjective) + "\t" + formatter.format(objective) + "\t" + cutoffValue + "\t"  + timeSolvingMaster + "\t" + timeSolvingPricing + "\t"+ nrGeneratedColumns + "\t" + pricingSolver);
    }

    @Override
    public void startCG(StartEvent startEvent) {
        this.writeLine("iteration \t boundOnMasterObjective \t objectiveMasterProblem \t cutoffValue \t t_master \t t_pricing \t nrGenColumns \t pricingSolver");
    }

    @Override
    public void finishCG(FinishEvent finishEvent) {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startMaster(StartMasterEvent startMasterEvent) {
        if(pricingProblemHasBeenSkipped)
            this.constructAndWriteLine();
        reset();

        timeSolvingMaster=System.currentTimeMillis();
    }

    @Override
    public void finishMaster(FinishMasterEvent finishMasterEvent) {
        timeSolvingMaster=System.currentTimeMillis()-timeSolvingMaster;
        objective=finishMasterEvent.objective;
        boundOnMasterObjective =finishMasterEvent.boundOnMasterObjective;
        cutoffValue =finishMasterEvent.cutoffValue;
        pricingProblemHasBeenSkipped=true;
    }

    @Override
    public void startPricing(StartPricingEvent startPricing) {
        pricingProblemHasBeenSkipped=false;
        timeSolvingPricing=System.currentTimeMillis();
    }

    @Override
    public void finishPricing(FinishPricingEvent finishPricingEvent) {
        timeSolvingPricing=System.currentTimeMillis()-timeSolvingPricing;
        objective=finishPricingEvent.objective;
        boundOnMasterObjective =finishPricingEvent.boundOnMasterObjective;
        cutoffValue =finishPricingEvent.cutoffValue;
        nrGeneratedColumns=finishPricingEvent.columns.size();
        if(nrGeneratedColumns > 0)
            pricingSolver=finishPricingEvent.columns.get(0).creator;
        this.constructAndWriteLine();
    }

    @Override
    public void timeLimitExceeded(TimeLimitExceededEvent timeLimitExceededEvent) {
        this.constructAndWriteLine();
    }
}
