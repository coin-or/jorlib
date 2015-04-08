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
 * KnapsackAlgorithm.java
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
package org.jorlib.alg.knapsack;

/**
 * Interface defining a knapsack problem solver. Multiple implementations to solve knapsacks may implement this interface. Some of the other classes 
 * present in this library rely on a knapsack solver.
 * @author Joris Kinable
 * @since April 8, 2015
 */
public interface KnapsackAlgorithm {

	/**
	 * Solve the knapsack problem.
	 * @param nrItems nr of items in the knapsack
	 * @param maxKnapsackWeight max size/weight of the knapsack
	 * @param itemValues
	 * @param itemWeights
	 * @return The value of the knapsack solution
	 */
	public double solveKnapsackProblem(int nrItems, int maxKnapsackWeight, double[] itemValues, int[] itemWeights);
	/**
	 * @return Get the value of the knapsack
	 */
	public double getKnapsackValue();
	/**
	 * @return Get the total weight of the knapsack
	 */
	public int getKnapsackWeight();
	/**
	 * @return Get the items in the knapsack
	 */
	public boolean[] getKnapsackItems();
}
