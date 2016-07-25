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
 * KnapsackDemo.java
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
package org.jorlib.demo.alg.knapsack;

import java.util.Arrays;

import org.jorlib.alg.knapsack.BinaryKnapsack;

/**
 * Simple class which solves a Knapsack problem
 * @author Joris Kinable
 * @since April 8, 2015
 *
 */
public final class KnapsackDemo {
	private KnapsackDemo(){}
	
	public static void main(String[] args){
		
		//Define the knapsack problem...
		double[] itemValues={15,10,9,5};
		int[] itemWeights={1,5,3,4};
		int maxKnapsackWeight=8;
		
		//... and solve it:
		BinaryKnapsack knapsack=new BinaryKnapsack();
		knapsack.solveKnapsackProblem(itemValues.length, maxKnapsackWeight, itemValues, itemWeights);
		System.out.println("Knapsack solution: ");
		System.out.println("\t value: "+knapsack.getKnapsackValue());
		System.out.println("\t weight: "+knapsack.getKnapsackWeight());
		System.out.println("\t item selected: "+Arrays.toString(knapsack.getKnapsackItems()));
	}

}
