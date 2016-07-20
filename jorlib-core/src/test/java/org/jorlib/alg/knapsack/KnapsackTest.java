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
 * KnapsackTest.java
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

import junit.framework.TestCase;

import org.junit.Assert;

/**
 * Test class for knapsack implementation
 * @author Joris Kinable
 * @since April 8, 2015
 * 
 */
public final class KnapsackTest extends TestCase {

	public void testKnapsack1(){
		double[] itemValues={15,10,9,5};
		int[] itemWeights={1,5,3,4};
		int maxKnapsackWeight=8;
		
		BinaryKnapsack knapsack=new BinaryKnapsack();
		knapsack.solveKnapsackProblem(itemValues.length, maxKnapsackWeight, itemValues, itemWeights);
		assertEquals(8, knapsack.getKnapsackWeight());
		assertEquals(29, knapsack.getKnapsackValue(), 0.000001);
		Assert.assertArrayEquals(new boolean[]{true, false, true, true}, knapsack.getKnapsackItems());
	}
	
	public void testKnapsack2(){
		double[] itemValues={300, 60, 90, 100, 240};
		int[] itemWeights={50, 10, 20, 40, 30};
		int maxKnapsackWeight=60;
		
		BinaryKnapsack knapsack=new BinaryKnapsack();
		knapsack.solveKnapsackProblem(itemValues.length, maxKnapsackWeight, itemValues, itemWeights);
		assertEquals(60, knapsack.getKnapsackWeight());
		assertEquals(390, knapsack.getKnapsackValue(), 0.000001);
		Assert.assertArrayEquals(new boolean[]{false, true, true, false, true}, knapsack.getKnapsackItems());
	}
}
