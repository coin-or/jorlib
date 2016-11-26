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
package org.jorlib.frameworks.columngeneration.tsp.cg.master;

import ilog.concert.*;
import ilog.cplex.IloCplex;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jorlib.frameworks.columngeneration.branchandprice.branchingdecisions.BranchingDecision;
import org.jorlib.frameworks.columngeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columngeneration.master.AbstractMaster;
import org.jorlib.frameworks.columngeneration.master.OptimizationSense;
import org.jorlib.frameworks.columngeneration.master.cutGeneration.CutHandler;
import org.jorlib.frameworks.columngeneration.tsp.cg.Matching;
import org.jorlib.frameworks.columngeneration.tsp.cg.PricingProblemByColor;
import org.jorlib.frameworks.columngeneration.tsp.cg.master.cuts.SubtourInequality;
import org.jorlib.frameworks.columngeneration.tsp.model.MatchingColor;
import org.jorlib.frameworks.columngeneration.tsp.model.TSP;
import org.jorlib.frameworks.columngeneration.util.OrderedBiMap;
import org.jorlib.frameworks.columngeneration.util.SolverStatus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines the master problem: Select exactly 1 red and 1 blue matching which together form a
 * hamiltonian cycle of minimum cost.
 *
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public final class Master
    extends AbstractMaster<TSP, Matching, PricingProblemByColor, TSPMasterData>
{

    private IloObjective obj; // Objective function
    private IloRange exactlyOneRedMatchingConstr; // Constraint
    private IloRange exactlyOneBlueMatchingConstr; // Constraint
    private Map<DefaultWeightedEdge, IloRange> edgeOnlyUsedOnceConstr; // Constraint

    /**
     * Create a new master problem
     * 
     * @param modelData model data
     * @param pricingProblems list of pricing problems
     * @param cutHandler cut handler
     */
    public Master(
        TSP modelData, List<PricingProblemByColor> pricingProblems,
        CutHandler<TSP, TSPMasterData> cutHandler)
    {
        super(modelData, pricingProblems, cutHandler, OptimizationSense.MINIMIZE);
    }

    /**
     * Builds the master model
     * 
     * @return Returns a MasterData object which is a data container for information coming from the
     *         master problem
     */
    @Override
    protected TSPMasterData buildModel()
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
            exactlyOneRedMatchingConstr = cplex.addRange(1, 1, "exactlyOneRed"); // Select exactly
                                                                                 // one red matching
            exactlyOneBlueMatchingConstr = cplex.addRange(1, 1, "exactlyOneBlue"); // Select exactly
                                                                                   // one blue
                                                                                   // matching

            edgeOnlyUsedOnceConstr = new LinkedHashMap<>(); // Each edge may only be used once
            for (DefaultWeightedEdge edge : dataModel.edgeSet()) {
                IloRange constr = cplex.addRange(
                    0, 1, "edgeOnlyUsedOnce_" + dataModel.getEdgeSource(edge) + "_"
                        + dataModel.getEdgeTarget(edge));
                edgeOnlyUsedOnceConstr.put(edge, constr);
            }
        } catch (IloException e) {
            e.printStackTrace();
        }

        Map<PricingProblemByColor, OrderedBiMap<Matching, IloNumVar>> varMap =
            new LinkedHashMap<>();
        for (PricingProblemByColor pricingProblem : pricingProblems)
            varMap.put(pricingProblem, new OrderedBiMap<>());

        // Create a new data object which will store information from the master. This object
        // automatically be passed to the CutHandler class.
        return new TSPMasterData(cplex, pricingProblems, varMap);
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
     * Adds a new column to the master problem
     * 
     * @param column column to add
     */
    @Override
    public void addColumn(Matching column)
    {
        MatchingColor matchingColor = column.associatedPricingProblem.color;
        try {
            // Register column with objective
            IloColumn iloColumn = masterData.cplex.column(obj, column.cost);
            // Register column with exactlyOneRedMatching/exactlyOneBlueMatching constr
            if (matchingColor == MatchingColor.RED) {
                iloColumn = iloColumn.and(masterData.cplex.column(exactlyOneRedMatchingConstr, 1));
            } else {
                iloColumn = iloColumn.and(masterData.cplex.column(exactlyOneBlueMatchingConstr, 1));
            }
            // Register column with edgeOnlyUsedOnce constraints
            for (DefaultWeightedEdge edge : column.edges) {
                iloColumn =
                    iloColumn.and(masterData.cplex.column(edgeOnlyUsedOnceConstr.get(edge), 1));
            }
            // Register column with subtour elimination constraints
            for (SubtourInequality subtourInequality : masterData.subtourInequalities.keySet()) {
                // Test how many edges in the matching enter/leave the cutSet (edges with exactly
                // one endpoint in the cutSet)
                int crossings = 0;
                for (DefaultWeightedEdge edge : column.edges) {
                    if (subtourInequality.cutSet.contains(dataModel.getEdgeSource(edge))
                        ^ subtourInequality.cutSet.contains(dataModel.getEdgeTarget(edge)))
                        crossings++;
                }
                if (crossings > 0) {
                    IloRange subtourConstraint =
                        masterData.subtourInequalities.get(subtourInequality);
                    iloColumn =
                        iloColumn.and(masterData.cplex.column(subtourConstraint, crossings));
                }
            }

            // Create the variable and store it
            IloNumVar var = masterData.cplex.numVar(
                iloColumn, 0, Double.MAX_VALUE, "z_" + matchingColor.name() + "_"
                    + masterData.getNrColumnsForPricingProblem(column.associatedPricingProblem));
            masterData.cplex.add(var);
            masterData.addColumn(column, var);
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extracts information from the master problem which is required by the pricing problems, e.g.
     * the reduced costs/dual values
     * 
     * @param pricingProblem pricing problem
     */
    @Override
    public void initializePricingProblem(PricingProblemByColor pricingProblem)
    {
        try {
            double[] modifiedCosts = new double[dataModel.N * (dataModel.N - 1) / 2]; // Modified
                                                                                      // cost for
                                                                                      // every edge
            int index = 0;
            for (DefaultWeightedEdge edge : dataModel.edgeSet()) {
                modifiedCosts[index] = masterData.cplex.getDual(edgeOnlyUsedOnceConstr.get(edge))
                    - dataModel.getEdgeWeight(edge);
                for (SubtourInequality subtourInequality : masterData.subtourInequalities
                    .keySet())
                {
                    if (subtourInequality.cutSet.contains(dataModel.getEdgeSource(edge))
                        ^ subtourInequality.cutSet.contains(dataModel.getEdgeTarget(edge)))
                        modifiedCosts[index] += masterData.cplex
                            .getDual(masterData.subtourInequalities.get(subtourInequality));
                }
                index++;
            }
            double dualConstant;
            if (pricingProblem.color == MatchingColor.RED)
                dualConstant = masterData.cplex.getDual(exactlyOneRedMatchingConstr);
            else
                dualConstant = masterData.cplex.getDual(exactlyOneBlueMatchingConstr);

            pricingProblem.initPricingProblem(modifiedCosts, dualConstant);
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
    public List<Matching> getSolution()
    {
        List<Matching> solution = new ArrayList<>();
        try {
            for (PricingProblemByColor pricingProblem : pricingProblems) {
                Matching[] matchings =
                    masterData.getVarMapForPricingProblem(pricingProblem).getKeysAsArray(
                        new Matching[masterData.getNrColumnsForPricingProblem(pricingProblem)]);
                IloNumVar[] vars =
                    masterData.getVarMapForPricingProblem(pricingProblem).getValuesAsArray(
                        new IloNumVar[masterData.getNrColumnsForPricingProblem(pricingProblem)]);
                double[] values = masterData.cplex.getValues(vars);

                // Iterate over each column and add it to the solution if it has a non-zero value
                for (int i = 0; i < matchings.length; i++) {
                    matchings[i].value = values[i];
                    if (values[i] >= config.PRECISION) {
                        solution.add(matchings[i]);
                    }
                }
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
        List<Matching> solution = this.getSolution();
        for (Matching m : solution)
            System.out.println(m);
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
     * Checks whether there are any violated inequalities, thereby invoking the cut handler
     * 
     * @return true if violated inequalities have been found (and added to the master problem)
     */
    @Override
    public boolean hasNewCuts()
    {
        // For convenience, we will precompute values required by the SubtourInequalityGenerator
        // class
        // and store it in the masterData object. For each edge, we need to know how often it is
        // used.
        masterData.edgeValueMap.clear();
        for (Matching matching : this.getSolution()) {
            for (DefaultWeightedEdge edge : matching.edges) {
                if (masterData.edgeValueMap.containsKey(edge))
                    masterData.edgeValueMap
                        .put(edge, masterData.edgeValueMap.get(edge) + matching.value);
                else
                    masterData.edgeValueMap.put(edge, matching.value);
            }
        }
        return super.hasNewCuts();
    }

    /**
     * Listen to branching decisions
     * 
     * @param bd Branching decision
     */
    @Override
    public void branchingDecisionPerformed(BranchingDecision<TSP, Matching> bd)
    {
        // For simplicity, we simply destroy the master problem and rebuild it. Of course, something
        // more sophisticated may be used which retains the master problem.
        this.close(); // Close the old cplex model
        masterData = this.buildModel(); // Create a new model without any columns
        cutHandler.setMasterData(masterData); // Inform the cutHandler about the new master model
    }

    /**
     * Undo branching decisions during backtracking in the Branch-and-Price tree
     * 
     * @param bd Branching decision
     */
    @Override
    public void branchingDecisionReversed(BranchingDecision<TSP, Matching> bd)
    {
        // No action required
    }
}
