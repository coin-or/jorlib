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
 * LiftedCoverInequalitySeparatorTest.java
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
package org.jorlib.alg.knapsack.separation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jorlib.alg.knapsack.BinaryKnapsack;
import org.jorlib.alg.knapsack.KnapsackAlgorithm;
import org.junit.Assert;

import junit.framework.TestCase;

/**
 * Test class for Lifted Cover AbstractInequality Separator implementation
 * @author Joris Kinable
 * @since April 8, 2015
 * 
 */
public final class LiftedCoverInequalitySeparatorTest extends TestCase {
	
	private final KnapsackAlgorithm knapsackAlgorithm;
	private final LiftedCoverInequalitySeparator separator;
	
	public LiftedCoverInequalitySeparatorTest(){
		knapsackAlgorithm=new BinaryKnapsack();
		separator=new LiftedCoverInequalitySeparator(knapsackAlgorithm);
	}

	/**
	 * Test 1 - Violated cover: {@code x1+x7<=1}, minimalCoverValue: 0.29
	 */
	public void testSeparateMinimalCoverInequality1(){
		
		double[] variableValues1={0.71, 0, 0.35, 1, 1, 0, 1, 1, 0};
		int[] knapsackCoefficients1={774, 76, 22, 42, 21, 760, 818, 62, 785};
		int b1=1500;
		separator.separateMinimalCover(variableValues1.length, knapsackCoefficients1, b1, variableValues1);
		
		assertTrue(separator.coverInequalityExists());
		assertTrue(separator.isMinimalCoverViolated());
		Assert.assertArrayEquals(new boolean[]{true, false, false, false, false, false, true, false, false}, separator.getMinimalCoverMask());
		Set<Integer> expected=new HashSet<>(Arrays.asList(0,6));
		assertEquals(expected, separator.getMinimalCover());
		assertEquals(1, separator.getMinimalCoverRHS());
	}
	
	/**
	 * Test2 - Violated cover: {@code x3+x7<=1}, minimalCoverValue: 0.65
	 */
	public void testSeparateMinimalCoverInequality2(){	
		//Test2 - Violated cover: x3+x7<=1, minimalCoverValue: 0.65
		double[] variableValues2={0.71, 0, 0.35, 1, 1, 0, 1, 1, 0};
		int[] knapsackCoefficients2={67, 27, 794, 53, 234, 32, 797, 97, 435};
		int b2=1500;
		separator.separateMinimalCover(variableValues2.length, knapsackCoefficients2, b2, variableValues2);
		
		assertTrue(separator.coverInequalityExists());
		assertTrue(separator.isMinimalCoverViolated());
		Assert.assertArrayEquals(new boolean[]{false, false, true, false, false, false, true, false, false}, separator.getMinimalCoverMask());
		Set<Integer> expected=new HashSet<>(Arrays.asList(2,6));
		assertEquals(expected, separator.getMinimalCover());
		assertEquals(1, separator.getMinimalCoverRHS());
	}
	
	/**
	 * Test 3 - Violated cover: {@code x3+x4+x5<=2}, minimalCoverValue: 7/53=0.13207547169
	 */
	public void testSeparateMinimalCoverInequality3(){	
		double[] variableValues3={0, 0, 1, 1, 46.0/53};
		int[] knapsackCoefficients3={47, 45, 79, 53, 53};
		int b3=178;
		separator.separateMinimalCover(variableValues3.length, knapsackCoefficients3, b3, variableValues3);
		
		assertTrue(separator.coverInequalityExists());
		assertTrue(separator.isMinimalCoverViolated());
		Assert.assertArrayEquals(new boolean[]{false, false, true, true, true}, separator.getMinimalCoverMask());
		Set<Integer> expected=new HashSet<>(Arrays.asList(2,3,4));
		assertEquals(expected, separator.getMinimalCover());
		assertEquals(2, separator.getMinimalCoverRHS());
	}
	
	/**
	 * Test 4 - minimal cover: {@code x3+x4+x5<=2}, minimalCoverValue: 1, NO violation
	 */
	public void testSeparateMinimalCoverInequality4(){	
		double[] variableValues4={.5, .5, 1, .5, .5};
		int[] knapsackCoefficients4={47, 45, 79, 53, 53};
		int b4=178;
		separator.separateMinimalCover(variableValues4.length, knapsackCoefficients4, b4, variableValues4);
		
		assertTrue(separator.coverInequalityExists());
		assertFalse(separator.isMinimalCoverViolated());
		Assert.assertArrayEquals(new boolean[]{false, false, true, true, true}, separator.getMinimalCoverMask());
		Set<Integer> expected=new HashSet<>(Arrays.asList(2,3,4));
		assertEquals(expected, separator.getMinimalCover());
		assertEquals(2, separator.getMinimalCoverRHS());
	}
	
	/**
	 * Test 5 - violated minimal cover: {@code x1+x7<=1}, violated lifted cover: {@code x1+x6+x7+x9<=1}
	 */
	public void testSeparateLiftedCoverInequality1(){
		double[] variableValues5={0.71, 0, 0.35, 1, 1, 0, 1, 1, 0};
		int[] knapsackCoefficients5={774, 76, 22, 42, 21, 760, 818, 62, 785};
		int b5=1500;
		separator.separateLiftedCover(variableValues5.length, knapsackCoefficients5, b5, variableValues5, true);
		
		//Check minimal cover
		assertTrue(separator.coverInequalityExists());
		assertTrue(separator.isMinimalCoverViolated());
		Assert.assertArrayEquals(new boolean[]{true, false, false, false, false, false, true, false, false}, separator.getMinimalCoverMask());
		Set<Integer> expected=new HashSet<>(Arrays.asList(0,6));
		assertEquals(expected, separator.getMinimalCover());
		assertEquals(1, separator.getMinimalCoverRHS());
		
		//Check lifted cover
		assertTrue(separator.isLiftedCoverViolated());
		Assert.assertArrayEquals(new int[]{1,0,0,0,0,1,1,0,1}, separator.getLiftedCoverCoefficients());
		assertEquals(1, separator.getLiftedCoverRHS());
		assertEquals(1.71, separator.getLiftedCoverLHS(), 0.000001);
	}
	
	/**
	 * Test 6 - minimal cover: {@code x3+x4+x5<=2} (NOT violated), violated lifted cover: {@code x1+2x3+x4+x5<=3}
	 */
	public void testSeparateLiftedCoverInequality2(){
		double[] variableValues6={.5, .5, 1, .5, .5};
		int[] knapsackCoefficients6={47, 45, 79, 53, 53};
		int b6=178;
		separator.separateLiftedCover(variableValues6.length, knapsackCoefficients6, b6, variableValues6, true);
		
		//Check minimal cover (not violated)
		assertTrue(separator.coverInequalityExists());
		assertFalse(separator.isMinimalCoverViolated());
		Assert.assertArrayEquals(new boolean[]{false, false, true, true, true}, separator.getMinimalCoverMask());
		Set<Integer> expected=new HashSet<>(Arrays.asList(2,3,4));
		assertEquals(expected, separator.getMinimalCover());
		assertEquals(2, separator.getMinimalCoverRHS());
		
		//Check lifted cover
		assertTrue(separator.isLiftedCoverViolated());
		Assert.assertArrayEquals(new int[]{1,0,2,1,1}, separator.getLiftedCoverCoefficients());
		assertEquals(3, separator.getLiftedCoverRHS());
		assertEquals(3.5, separator.getLiftedCoverLHS(), 0.000001);
	}
	
	/**
	 * Test 7 - Knapsack: {@code 3x_1+3_x_2 <=6}. NO cover inequality exists for this knapsack constraint.
	 */
	public void testNoCoverInequalityExists(){
		double[] variableValues7={0.8333333333333334, 0.8333333333333333};
		int[] knapsackCoefficients7={3,3};
		int b7=6;
		separator.separateLiftedCover(variableValues7.length, knapsackCoefficients7, b7, variableValues7, true);
		assertFalse(separator.coverInequalityExists());
	}
}
