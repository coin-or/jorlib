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
package org.jorlib.alg.knapsack;

/**
 * Interface defining a knapsack problem solver. Multiple implementations to solve knapsacks may
 * implement this interface. Some of the other classes present in this library rely on a knapsack
 * solver.
 * 
 * @author Joris Kinable
 * @since April 8, 2015
 */
public interface KnapsackAlgorithm
{

    /**
     * Solve the knapsack problem.
     * 
     * @param nrItems nr of items in the knapsack
     * @param maxKnapsackWeight max size/weight of the knapsack
     * @param itemValues item values
     * @param itemWeights item weights
     * @return The value of the knapsack solution
     */
    double solveKnapsackProblem(
        int nrItems, int maxKnapsackWeight, double[] itemValues, int[] itemWeights);

    /**
     * @return Get the value of the knapsack
     */
    double getKnapsackValue();

    /**
     * @return Get the total weight of the knapsack
     */
    int getKnapsackWeight();

    /**
     * @return Get the items in the knapsack
     */
    boolean[] getKnapsackItems();
}
