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
package org.jorlib.frameworks.columngeneration.master;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jorlib.frameworks.columngeneration.branchandprice.branchingdecisions.BranchingDecision;
import org.jorlib.frameworks.columngeneration.branchandprice.branchingdecisions.BranchingDecisionListener;
import org.jorlib.frameworks.columngeneration.colgenmain.AbstractColumn;
import org.jorlib.frameworks.columngeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columngeneration.master.cutGeneration.CutHandler;
import org.jorlib.frameworks.columngeneration.master.cutGeneration.AbstractInequality;
import org.jorlib.frameworks.columngeneration.model.ModelInterface;
import org.jorlib.frameworks.columngeneration.pricing.AbstractPricingProblem;
import org.jorlib.frameworks.columngeneration.util.Configuration;
import org.jorlib.frameworks.columngeneration.util.SolverStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class representing the Master Problem.
 *
 * @author Joris Kinable
 * @version 13-4-2015
 *
 * @param <T> Type of data model
 * @param <V> Type of pricing problem
 * @param <U> Type of columns
 * @param <W> Type of Master Data
 */
public abstract class AbstractMaster<T extends ModelInterface, U extends AbstractColumn<T, V>,
    V extends AbstractPricingProblem<T, U>, W extends MasterData<T, U, V, ?>>
    implements BranchingDecisionListener<T, U>
{

    /** Logger for this class **/
    protected final Logger logger = LoggerFactory.getLogger(AbstractMaster.class);
    /** Configuration file for this class **/
    protected final Configuration config = Configuration.getConfiguration();

    /** Data model **/
    protected final T dataModel;
    /** Pricing Problems **/
    protected final List<V> pricingProblems;
    /** Data object which stores data from the Master Problem **/
    protected W masterData;
    /** Handle to a cutHandler which performs separation **/
    protected CutHandler<T, W> cutHandler;
    /** Defines whether the master problem is a minimization or a maximization problem **/
    protected final OptimizationSense optimizationSenseMaster;

    /**
     * Creates a new Master Problem.
     *
     * This implementation will invoke the {@link #buildModel()} buildModel} method. Any data
     * members required during the execution of {@link #buildModel()} buildModel} should be
     * instantiated prior to the invocation of this constructor, or within the {@link #buildModel()}
     * buildModel} method.
     * 
     * @param dataModel data model
     * @param pricingProblems pricing problems
     * @param optimizationSenseMaster indicates whether the Master Problem is a Minimization or a
     *        Maximization problem
     */
    public AbstractMaster(
        T dataModel, List<V> pricingProblems, OptimizationSense optimizationSenseMaster)
    {
        this.dataModel = dataModel;
        this.pricingProblems = pricingProblems;
        this.optimizationSenseMaster = optimizationSenseMaster;
        masterData = this.buildModel();
        cutHandler = new CutHandler<>();
        cutHandler.setMasterData(masterData);
    }

    /**
     * Creates a new Master Problem
     *
     * This implementation will invoke the {@link #buildModel()} buildModel} method. Any data
     * members required during the execution of {@link #buildModel()} buildModel} should be
     * instantiated prior to the invocation of this constructor, or within the {@link #buildModel()}
     * buildModel} method.
     * 
     * @param dataModel data model
     * @param pricingProblem pricing problem
     * @param optimizationSenseMaster indicates whether the Master Problem is a Minimization or a
     *        Maximization problem
     */
    public AbstractMaster(T dataModel, V pricingProblem, OptimizationSense optimizationSenseMaster)
    {
        this(dataModel, Collections.singletonList(pricingProblem), optimizationSenseMaster);
    }

    /**
     * Creates a new Master Problem
     *
     * This implementation will invoke the {@link #buildModel()} buildModel} method. Any data
     * members required during the execution of {@link #buildModel()} buildModel} should be
     * instantiated prior to the invocation of this constructor, or within the {@link #buildModel()}
     * buildModel} method.
     * 
     * @param dataModel data model
     * @param pricingProblems pricing problems
     * @param cutHandler Reference to a cut handler
     * @param optimizationSenseMaster indicates whether the Master Problem is a Minimization or a
     *        Maximization problem
     */
    public AbstractMaster(
        T dataModel, List<V> pricingProblems, CutHandler<T, W> cutHandler,
        OptimizationSense optimizationSenseMaster)
    {
        this.dataModel = dataModel;
        this.pricingProblems = pricingProblems;
        this.cutHandler = cutHandler;
        this.optimizationSenseMaster = optimizationSenseMaster;
        masterData = this.buildModel();
        cutHandler.setMasterData(masterData);
    }

    /**
     * Creates a new Master Problem
     *
     * This implementation will invoke the {@link #buildModel()} buildModel} method. Any data
     * members required during the execution of {@link #buildModel()} buildModel} should be
     * instantiated prior to the invocation of this constructor, or within the {@link #buildModel()}
     * buildModel} method.
     * 
     * @param dataModel data model
     * @param pricingProblem pricing problem
     * @param cutHandler Reference to a cut handler
     * @param optimizationSenseMaster indicates whether the Master Problem is a Minimization or a
     *        Maximization problem
     */
    public AbstractMaster(
        T dataModel, V pricingProblem, CutHandler<T, W> cutHandler,
        OptimizationSense optimizationSenseMaster)
    {
        this(
            dataModel, Collections.singletonList(pricingProblem), cutHandler,
            optimizationSenseMaster);
    }

    /**
     * Build the master problem
     * 
     * @return a MasterData object
     */
    protected abstract W buildModel();

    /**
     * Solve the master problem
     * 
     * @param timeLimit Future point in time by which this method must be finished
     * @throws TimeLimitExceededException if time limit is exceeded
     */
    public void solve(long timeLimit)
        throws TimeLimitExceededException
    {
        masterData.iterations++;
        masterData.status = this.solveMasterProblem(timeLimit);
    }

    /**
     * Method implementing the solve procedure for the master problem
     * 
     * @param timeLimit Future point in time by which this method must be finished
     * @return Returns true if successful (and optimal)
     * @throws TimeLimitExceededException if time limit is exceeded
     */
    protected abstract SolverStatus solveMasterProblem(long timeLimit)
        throws TimeLimitExceededException;

    /**
     * Get the reduced cost information required for a particular pricingProblem. The pricing
     * problem often looks like: {@literal a_1x_1+a_2x_2+...+a_nx_n <= b}, where a_i are dual
     * variables, and b some constant. The dual information is stored in the PricingProblem object.
     *
     * @param pricingProblem Object in which the dual information required to solve the pricing
     *        problems is stored.
     */
    public abstract void initializePricingProblem(V pricingProblem);

    /**
     * Returns the optimization sense of the Master Problem (minimization or maximization).
     * 
     * @return the optimization sense of the Master Problem (minimization or maximization).
     */
    public OptimizationSense getOptimizationSense()
    {
        return this.optimizationSenseMaster;
    }

    /**
     * Returns the objective value of the current master problem.
     * 
     * @return objective value of master problem
     */
    public double getObjective()
    {
        return masterData.objectiveValue;
    }

    /**
     * Returns the number of times the master problem has been solved
     * 
     * @return Returns the number of times the master problem has been solved
     */
    public int getIterationCount()
    {
        return masterData.iterations;
    }

    /**
     * Returns true if the master problem has been solved to optimality
     *
     * @return Returns true if the master problem has been solved to optimality
     * @deprecated use {@link #getStatus()} instead
     */
    @Deprecated
    public boolean isOptimal() {
        return masterData.status == SolverStatus.OPTIMAL;
    }
    
    /**
     * Returns the current status of the master problem
     * 
     * @return the {@link SolverStatus} associated to the master problem
     */
    public SolverStatus getStatus()
    {
        return masterData.status;
    }

    /**
     * Method which can be invoked externally to check whether the current master problem solution
     * violates any inequalities. A handle to a cutHandler must have been provided when constructing
     * the master problem
     * 
     * @return true if inequalities were added to the master problem, false otherwise
     */
    public boolean hasNewCuts()
    {
        boolean hasNewCuts = false;
        if (cutHandler != null) {
            hasNewCuts = cutHandler.generateInequalities();
        }
        return hasNewCuts;
    }

    /**
     * Adds inequalities to this master. A handle to a cutHandler must have been provided in the
     * constructor of this class
     * 
     * @param cuts inequalities to be added
     */
    public void addCuts(Collection<AbstractInequality> cuts)
    {
        cutHandler.addCuts(cuts);
    }

    /**
     * Returns all the inequalities in the master model. A handle to a cutHandler must have been
     * provided in the constructor of this class
     * 
     * @return a list of inequalities
     */
    public List<AbstractInequality> getCuts()
    {
        return cutHandler.getCuts();
    }

    /**
     * Add a column to the model
     * 
     * @param column column to add
     */
    public abstract void addColumn(U column);

    /**
     * Add an initial solution (list of columns)
     * 
     * @param columns initial set of columns
     */
    public void addColumns(List<U> columns)
    {
        for (U column : columns) {
            this.addColumn(column);
        }
    }

    /**
     * Returns all columns generated for the given pricing problem.
     * 
     * @param pricingProblem Pricing problem
     * @return Set of columns
     */
    public Set<U> getColumns(V pricingProblem)
    {
        return masterData.getColumnsForPricingProblem(pricingProblem);
    }

    /**
     * After the master problem has been solved, a solution has to be returned, consisting of a set
     * of columns selected by the master problem, i.e the columns with a non-zero value.
     * 
     * @return solution consisting of non-zero columns
     */
    public abstract List<U> getSolution();

    /**
     * To compute a bound on the optimal solution of the relaxed master problem, multiple components
     * are required, including information from the master problem. This function returns that
     * information.
     * 
     * @return value originating from the master problem which is required to calculate a bound on
     *         the optimal objective of the master problem
     */
    public double getBoundComponent()
    {
        throw new UnsupportedOperationException(
            "Not implemented. You should override this function");
    }

    /**
     * Export the master problem to a file e.g. an .lp file
     * 
     * @param name Name of the exported file
     */
    public void exportModel(String name)
    {
        throw new UnsupportedOperationException(
            "Not implemented. You should override this function");
    }

    /**
     * Give a textual representation of the solution
     */
    public abstract void printSolution();

    /**
     * Close the master problem
     */
    public abstract void close();

    /**
     * Method invoked when a branching decision is executed.
     * 
     * @param bd branching decision
     */
    @Override
    public void branchingDecisionPerformed(BranchingDecision<T,U> bd)
    {
    }

    /**
     * Method invoked when a branching decision is reversed due to backtracking in the
     * Branch-and-Price tree.
     * 
     * @param bd branching decision
     */
    @Override
    public void branchingDecisionReversed(BranchingDecision<T,U> bd)
    {
    }
}
