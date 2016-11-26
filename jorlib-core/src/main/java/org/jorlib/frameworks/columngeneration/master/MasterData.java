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

import org.jorlib.frameworks.columngeneration.colgenmain.AbstractColumn;
import org.jorlib.frameworks.columngeneration.model.ModelInterface;
import org.jorlib.frameworks.columngeneration.pricing.AbstractPricingProblem;
import org.jorlib.frameworks.columngeneration.util.OrderedBiMap;
import org.jorlib.frameworks.columngeneration.util.SolverStatus;

import java.util.*;

/**
 * This is a data object which is being managed by the Master problem. The same data object is
 * passed to the cutHandlers. Therefore, the object can be used to pass information from the master
 * problem to the classes which separate valid inequalities (and also in the opposite direction).
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 * @param <T> type of model data
 * @param <U> type of column
 * @param <V> type of pricing problem
 * @param <X> type of variable, e.g. an IloNumVar in cplex
 *
 */
public class MasterData<T extends ModelInterface, U extends AbstractColumn<T, V>, V extends AbstractPricingProblem<T, U>, X>
{

    /** Objective value of the current master problem **/
    public double objectiveValue;
    /** Number of times the master problem has been solved **/
    public int iterations = 0;
    /** Indicates whether the master problem has been solved to optimality **/
    public SolverStatus status = SolverStatus.UNDECIDED;

    /** Storage of the variables representing the columns in the master problem **/
    protected final Map<V, OrderedBiMap<U, X>> varMap;

    /**
     * Creates a new MasterData object
     * 
     * @param varMap A double map which stores the variables. The first key is the pricing problem,
     *        the second key is a column and the value is a variable object, e.g. an IloNumVar in
     *        cplex.
     */
    public MasterData(Map<V, OrderedBiMap<U, X>> varMap)
    {
        this.varMap = varMap;
    }

    /**
     * Adds a column and corresponding variable
     * 
     * @param column column
     * @param variable corresponding variable
     */
    public void addColumn(U column, X variable)
    {
        if (varMap.get(column.associatedPricingProblem).containsKey(column))
            throw new RuntimeException(
                "Duplicate column has been generated for pricing problem: "
                    + column.associatedPricingProblem.toString()
                    + "! This column already exists and by definition should not have negative reduced cost: "
                    + column);
        else
            varMap.get(column.associatedPricingProblem).put(column, variable);
    }

    // ============= Single Pricing Problem methods ====================

    /**
     * Returns a set of columns registered with the master problem
     * 
     * @return Set of columns
     * @throws UnsupportedOperationException if the number of pricing problems does not equal one
     */
    public Set<U> getColumns()
    {
        if (varMap.size() != 1)
            throw new UnsupportedOperationException(
                "This method can only be used if there's only a single pricing problem! Use getColumnsForPricingProblem(V pricingProblem) instead.");
        return this.getColumnsForPricingProblem(varMap.keySet().iterator().next());
    }

    /**
     * Returns a list of columns registered with the master problem
     * 
     * @return List of columns
     * @throws UnsupportedOperationException if the number of pricing problems does not equal one
     */
    public List<U> getColumnsForPricingProblemAsList()
    {
        if (varMap.size() != 1)
            throw new UnsupportedOperationException(
                "This method can only be used if there's only a single pricing problem! Use getColumnsForPricingProblemAsList(V pricingProblem) instead.");
        return varMap.get(varMap.keySet().iterator().next()).keyList();
    }

    /**
     * Returns the number of columns registered with the master problem
     * 
     * @return Number of columns
     * @throws UnsupportedOperationException if the number of pricing problems does not equal one
     */
    public int getNrColumns()
    {
        if (varMap.size() != 1)
            throw new UnsupportedOperationException(
                "This method can only be used if there's only a single pricing problem! Use getNrColumnsForPricingProblem(V pricingProblem) instead.");
        return this.getNrColumnsForPricingProblem(varMap.keySet().iterator().next());
    }

    /**
     * Returns a mapping of the columns to their variables. WARNING: extreme care must be taken when
     * modifying this mapping outside of the MasterData class!
     * 
     * @return Mapping of columns to variables
     * @throws UnsupportedOperationException if the number of pricing problems does not equal one
     */
    public OrderedBiMap<U, X> getVarMap()
    {
        if (varMap.size() != 1)
            throw new UnsupportedOperationException(
                "This method can only be used if there's only a single pricing problem! Use getVarMapForPricingProblem(V pricingProblem) instead.");
        return this.getVarMapForPricingProblem(varMap.keySet().iterator().next());
    }

    /**
     * Returns the variable corresponding to the given column
     * 
     * @param column column
     * @return variable
     * @throws UnsupportedOperationException if the number of pricing problems does not equal one
     */
    public X getVar(U column)
    {
        if (varMap.size() != 1)
            throw new UnsupportedOperationException(
                "This method can only be used if there's only a single pricing problem! Use getVar(V pricingProblem, U column) instead.");
        return varMap.get(varMap.keySet().iterator().next()).get(column);
    }

    // ============= Multiple Pricing Problem methods ====================

    /**
     * Returns a set of columns registered with the master problem for the given pricing problem
     * 
     * @param pricingProblem pricing problem
     * @return Set of columns
     */
    public Set<U> getColumnsForPricingProblem(V pricingProblem)
    {
        return varMap.get(pricingProblem).keySet();
    }

    /**
     * Returns a list of columns registered with the master problem for the given pricing problem
     * 
     * @param pricingProblem pricing problem
     * @return List of columns
     */
    public List<U> getColumnsForPricingProblemAsList(V pricingProblem)
    {
        return varMap.get(pricingProblem).keyList();
    }

    /**
     * Returns the number of columns registered with the master problem for the given pricing
     * problem
     * 
     * @param pricingProblem pricing problem
     * @return Number of columns
     */
    public int getNrColumnsForPricingProblem(V pricingProblem)
    {
        return varMap.get(pricingProblem).size();
    }

    /**
     * Returns a mapping of the columns to their variables for the given pricing problem. WARNING:
     * extreme care must be taken when modifying this mapping outside of teh MasterData class!
     * 
     * @param pricingProblem pricing problem
     * @return Mapping of columns to variables
     */
    public OrderedBiMap<U, X> getVarMapForPricingProblem(V pricingProblem)
    {
        return varMap.get(pricingProblem);
    }

    /**
     * Returns the variable corresponding to the given column registered with the given pricing
     * problem
     * 
     * @param column column
     * @param pricingProblem pricing problem
     * @return variable
     */
    public X getVar(V pricingProblem, U column)
    {
        return varMap.get(pricingProblem).get(column);
    }
}
