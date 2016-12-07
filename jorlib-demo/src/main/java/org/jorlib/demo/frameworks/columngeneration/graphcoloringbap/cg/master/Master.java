/* ==========================================
 * jORLib : Java Operations Research Library
 * ==========================================
 *
 * Project Info:  http://www.coin-or.org/projects/jORLib.xml
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2016-2016, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 * as published by the Free Software Foundation.
 */
package org.jorlib.demo.frameworks.columngeneration.graphcoloringbap.cg.master;

import ilog.concert.*;
import ilog.cplex.IloCplex;
import org.jorlib.demo.frameworks.columngeneration.graphcoloringbap.cg.ChromaticNumberPricingProblem;
import org.jorlib.demo.frameworks.columngeneration.graphcoloringbap.cg.IndependentSet;
import org.jorlib.demo.frameworks.columngeneration.graphcoloringbap.model.ColoringGraph;
import org.jorlib.frameworks.columngeneration.branchandprice.branchingdecisions.BranchingDecision;
import org.jorlib.frameworks.columngeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columngeneration.master.AbstractMaster;
import org.jorlib.frameworks.columngeneration.master.OptimizationSense;
import org.jorlib.frameworks.columngeneration.util.OrderedBiMap;
import org.jorlib.frameworks.columngeneration.util.SolverStatus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines the master problem: Select a subset of independent sets, such that the union of all
 * selected independent sets cover all vertices in the graph.
 * <ul>
 * <li>a reference to the cplex model</li>
 * <li>reference to the pricing problem</li>
 * </ul>
 * 
 * @author Joris Kinable
 * @version 29-6-2016
 */
public final class Master
    extends
    AbstractMaster<ColoringGraph, IndependentSet, ChromaticNumberPricingProblem, ColoringMasterData>
{

    private IloObjective obj; // Objective function
    private IloRange[] oneColorPerVertex; // Constraint

    public Master(ColoringGraph dataModel, ChromaticNumberPricingProblem pricingProblem)
    {
        super(dataModel, pricingProblem, OptimizationSense.MINIMIZE);
        System.out.println("Master constructor. Columns: " + masterData.getNrColumns());
    }

    /**
     * Builds the master model
     * 
     * @return Returns a MasterData object which is a data container for information coming from the
     *         master problem
     */
    @Override
    protected ColoringMasterData buildModel()
    {
        IloCplex cplex = null;

        try {
            cplex = new IloCplex(); // Create cplex instance
            cplex.setOut(null); // Disable cplex output
            cplex.setParam(IloCplex.IntParam.Threads, config.MAXTHREADS); // Set number of threads
                                                                          // that may be used by the
                                                                          // master

            // Define objective
            obj = cplex.addMinimize();

            // Define constraints
            oneColorPerVertex = new IloRange[dataModel.getNrVertices()];
            for (int i = 0; i < dataModel.getNrVertices(); i++)
                oneColorPerVertex[i] = cplex.addRange(1, Double.MAX_VALUE, "oneColorPerVertex"); // Assign
                                                                                                 // one
                                                                                                 // color
                                                                                                 // to
                                                                                                 // every
                                                                                                 // vertex

        } catch (IloException e) {
            e.printStackTrace();
        }

        Map<ChromaticNumberPricingProblem, OrderedBiMap<IndependentSet, IloNumVar>> varMap =
            new LinkedHashMap<>();
        ChromaticNumberPricingProblem pricingProblem = this.pricingProblems.get(0);
        varMap.put(pricingProblem, new OrderedBiMap<>());

        // Create a new data object which will store information from the master.
        return new ColoringMasterData(cplex, varMap);
    }

    /**
     * Solve the master problem
     * 
     * @param timeLimit Future point in time by which the solve procedure must be completed
     * @return true if the master problem has been solved
     * @throws TimeLimitExceededException TimeLimitExceededException
     */
    @Override
    protected SolverStatus solveMasterProblem(long timeLimit)
        throws TimeLimitExceededException
    {
        try {
            // Set time limit
            double timeRemaining = Math.max(1, (timeLimit - System.currentTimeMillis()) / 1000.0);
            masterData.cplex.setParam(IloCplex.DoubleParam.TiLim, timeRemaining); // set time limit
                                                                                  // in seconds
            // Potentially export the model
            if (config.EXPORT_MODEL)
                masterData.cplex.exportModel(
                    config.EXPORT_MASTER_DIR + "master_" + this.getIterationCount() + ".lp");

            // Solve the model
            if (!masterData.cplex.solve()
                || masterData.cplex.getStatus() != IloCplex.Status.Optimal)
            {
                if (masterData.cplex.getCplexStatus() == IloCplex.CplexStatus.AbortTimeLim) // Aborted
                                                                                            // due
                                                                                            // to
                                                                                            // time
                                                                                            // limit
                    throw new TimeLimitExceededException();
                else
                    throw new RuntimeException(
                        "Master problem solve failed! Status: " + masterData.cplex.getStatus());
            } else {
                masterData.objectiveValue = masterData.cplex.getObjValue();
            }
        } catch (IloException e) {
            e.printStackTrace();
        }
        return SolverStatus.OPTIMAL;
    }

    /**
     * Extracts information from the master problem which is required by the pricing problems, e.g.
     * the reduced costs/dual values
     * 
     * @param pricingProblem pricing problem
     */
    @Override
    public void initializePricingProblem(ChromaticNumberPricingProblem pricingProblem)
    {
        try {
            double[] dualValues = masterData.cplex.getDuals(oneColorPerVertex); // Dual value per
                                                                                // vertex
            pricingProblem.initPricingProblem(dualValues);
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new column to the master problem
     * 
     * @param column column to add
     */
    @Override
    public void addColumn(IndependentSet column)
    {
        try {
            // Register column with objective
            IloColumn iloColumn = masterData.cplex.column(obj, column.cost);
            // Register column with oneColorPerVertex constraints
            for (Integer vertex : column.vertices)
                iloColumn = iloColumn.and(masterData.cplex.column(oneColorPerVertex[vertex], 1));

            // Create the variable and store it
            IloNumVar var = masterData.cplex
                .numVar(iloColumn, 0, Double.MAX_VALUE, "x_" + masterData.getNrColumns());
            masterData.cplex.add(var);
            masterData.addColumn(column, var);
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the solution from the master problem
     * 
     * @return Returns all non-zero valued columns from the master problem
     */
    @Override
    public List<IndependentSet> getSolution()
    {
        List<IndependentSet> solution = new ArrayList<>();
        try {
            IndependentSet[] independentSets =
                masterData.getColumnsForPricingProblemAsList().toArray(
                    new IndependentSet[masterData.getNrColumns()]);
            IloNumVar[] vars =
                masterData.getVarMap().getValuesAsArray(new IloNumVar[masterData.getNrColumns()]);
            double[] values = masterData.cplex.getValues(vars);
            for (int i = 0; i < independentSets.length; i++) {
                independentSets[i].value = values[i];
                if (values[i] >= config.PRECISION)
                    solution.add(independentSets[i]);
            }
        } catch (IloException e) {
            e.printStackTrace();
        }
        return solution;
    }

    /**
     * Prints the solution
     */
    @Override
    public void printSolution()
    {
        List<IndependentSet> solution = this.getSolution();
        for (IndependentSet is : solution)
            System.out.println(is);
    }

    /**
     * Closes the master problem
     */
    @Override
    public void close()
    {
        masterData.cplex.end();
    }

    /**
     * Listen to branching decisions
     * 
     * @param bd Branching decision
     */
    @Override
    public void branchingDecisionPerformed(BranchingDecision<ColoringGraph, IndependentSet> bd)
    {
        // For simplicity, we simply destroy the master problem and rebuild it. Of course, something
        // more sophisticated may be used which retains the master problem.
        this.close(); // Close the old cplex model
        masterData = this.buildModel(); // Create a new model without any columns
    }

    /**
     * Undo branching decisions during backtracking in the Branch-and-Price tree
     * 
     * @param bd Branching decision
     */
    @Override
    public void branchingDecisionReversed(BranchingDecision<ColoringGraph, IndependentSet> bd)
    {
        // No action required
    }
}
