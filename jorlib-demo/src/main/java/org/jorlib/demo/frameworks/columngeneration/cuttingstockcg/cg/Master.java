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
package org.jorlib.demo.frameworks.columngeneration.cuttingstockcg.cg;

import ilog.concert.IloColumn;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jorlib.demo.frameworks.columngeneration.cuttingstockcg.model.CuttingStock;
import org.jorlib.frameworks.columngeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columngeneration.master.AbstractMaster;
import org.jorlib.frameworks.columngeneration.master.OptimizationSense;
import org.jorlib.frameworks.columngeneration.util.OrderedBiMap;
import org.jorlib.frameworks.columngeneration.util.SolverStatus;

/**
 * Implementation of the Master problem for the Cutting Stock problem The Master problem is an LP
 * which is being handled by Cplex
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public final class Master
    extends AbstractMaster<CuttingStock, CuttingPattern, PricingProblem, CuttingStockMasterData>
{

    private IloCplex cplex; // Cplex instance
    private IloObjective obj; // Objective function
    private IloRange[] satisfyDemandConstr; // Constraint

    public Master(CuttingStock modelData, PricingProblem pricingProblem)
    {
        super(modelData, pricingProblem, OptimizationSense.MINIMIZE);
    }

    /**
     * Build the cplex problem
     */
    @Override
    protected CuttingStockMasterData buildModel()
    {
        try {
            cplex = new IloCplex(); // Create cplex instance
            cplex.setOut(null); // Disable cplex output
            cplex.setParam(IloCplex.IntParam.Threads, config.MAXTHREADS); // Set number of threads
                                                                          // that may be used by the
                                                                          // cplex

            // Define the objective
            obj = cplex.addMinimize();

            // Define constraints
            satisfyDemandConstr = new IloRange[dataModel.nrFinals];
            for (int i = 0; i < dataModel.nrFinals; i++)
                satisfyDemandConstr[i] = cplex.addRange(
                    dataModel.demandForFinals[i], dataModel.demandForFinals[i],
                    "satisfyDemandFinal_" + i);

            // Define a container for the variables
        } catch (IloException e) {
            e.printStackTrace();
        }

        // Define a container for the variables
        Map<PricingProblem, OrderedBiMap<CuttingPattern, IloNumVar>> varMap = new LinkedHashMap<>();
        varMap.put(pricingProblems.get(0), new OrderedBiMap<>());

        // Return a new data object which will hold data from the Master Problem. Since we are not
        // working with inequalities in this example,
        // we can simply return the default.
        return new CuttingStockMasterData(varMap);
    }

    /**
     * Solve the cplex problem and return whether it was solved to optimality
     */
    @Override
    protected SolverStatus solveMasterProblem(long timeLimit)
        throws TimeLimitExceededException
    {
        try {
            // Set time limit
            double timeRemaining = Math.max(1, (timeLimit - System.currentTimeMillis()) / 1000.0);
            cplex.setParam(IloCplex.DoubleParam.TiLim, timeRemaining); // set time limit in seconds
            // Potentially export the model
            if (config.EXPORT_MODEL)
                cplex.exportModel(
                    config.EXPORT_MASTER_DIR + "master_" + this.getIterationCount() + ".lp");

            // Solve the model
            if (!cplex.solve() || cplex.getStatus() != IloCplex.Status.Optimal) {
                if (cplex.getCplexStatus() == IloCplex.CplexStatus.AbortTimeLim) // Aborted due to
                                                                                 // time limit
                    throw new TimeLimitExceededException();
                else
                    throw new RuntimeException(
                        "Master problem solve failed! Status: " + cplex.getStatus());
            } else {
                masterData.objectiveValue = cplex.getObjValue();
            }
        } catch (IloException e) {
            e.printStackTrace();
        }

        return SolverStatus.OPTIMAL;
    }

    /**
     * Store the dual information required by the pricing problems into the pricing problem object
     */
    @Override
    public void initializePricingProblem(PricingProblem pricingProblem)
    {
        try {
            double[] duals = cplex.getDuals(satisfyDemandConstr);
            pricingProblem.initPricingProblem(duals);
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function which adds a new column to the cplex problem
     */
    @Override
    public void addColumn(CuttingPattern column)
    {
        try {
            // Register column with objective
            IloColumn iloColumn = cplex.column(obj, 1);

            // Register column with demand constraint
            for (int i = 0; i < dataModel.nrFinals; i++)
                iloColumn =
                    iloColumn.and(cplex.column(satisfyDemandConstr[i], column.yieldVector[i]));

            // Create the variable and store it
            IloNumVar var = cplex
                .numVar(iloColumn, 0, Double.MAX_VALUE, "z_" + "," + masterData.getNrColumns());
            cplex.add(var);
            masterData.addColumn(column, var);
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the solution, i.e columns with non-zero values in the cplex problem
     */
    @Override
    public List<CuttingPattern> getSolution()
    {
        List<CuttingPattern> solution = new ArrayList<>();
        try {
            CuttingPattern[] cuttingPatterns = masterData
                .getVarMap().getKeysAsArray(new CuttingPattern[masterData.getNrColumns()]);
            IloNumVar[] vars =
                masterData.getVarMap().getValuesAsArray(new IloNumVar[masterData.getNrColumns()]);
            double[] values = cplex.getValues(vars);

            // Iterate over each column and add it to the solution if it has a non-zero value
            for (int i = 0; i < cuttingPatterns.length; i++) {
                cuttingPatterns[i].value = values[i];
                if (values[i] >= config.PRECISION) {
                    solution.add(cuttingPatterns[i]);
                }
            }
        } catch (IloException e) {
            e.printStackTrace();
        }
        return solution;
    }

    /**
     * Close the cplex problem
     */
    @Override
    public void close()
    {
        cplex.end();
    }

    /**
     * Print the solution if desired
     */
    @Override
    public void printSolution()
    {
        System.out.println("Master solution:");
        for (CuttingPattern cp : this.getSolution())
            System.out.println(cp);
    }

    /**
     * Export the model to a file
     */
    @Override
    public void exportModel(String fileName)
    {
        try {
            cplex.exportModel(config.EXPORT_MASTER_DIR + fileName);
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

}
