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
 * LiftedCoverInequalitySeparatorDemo.java
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
package org.jorlib.demo.alg.knapsack.separation;

import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.jorlib.alg.knapsack.BinaryKnapsack;
import org.jorlib.alg.knapsack.KnapsackAlgorithm;
import org.jorlib.alg.knapsack.separation.LiftedCoverInequalitySeparator;

/**
 * Demo class for the Lifted Cover AbstractInequality Separator implementation
 * @author Joris Kinable
 * @since April 8, 2015
 * 
 */
public final class LiftedCoverInequalitySeparatorDemo {

	public static void main(String[] args){
		//Define a knapsack algorithm and the LiftedCoverInequalitySeparator
		KnapsackAlgorithm knapsackAlgorithm=new BinaryKnapsack();
		LiftedCoverInequalitySeparator separator=new LiftedCoverInequalitySeparator(knapsackAlgorithm);
		
		
		//EXAMPLE 1: MINIMAL COVER INEQUALITY
		
		//Define the knapsack constraint and a solution to it.
		double[] variableValues1={0.71, 0, 0.35, 1, 1, 0, 1, 1, 0};
		int[] knapsackCoefficients1={774, 76, 22, 42, 21, 760, 818, 62, 785};
		int b1=1500;
		System.out.println("Knapsack constr: 774x_0 + 76x_1 + 22x_2 + 42x_3 + 21x_4 + 760x_5 + 818x_6 + 62x_7 + 785x_9 <= 1500");
		
		//Invoke the separator
		separator.separateMinimalCover(variableValues1.length, knapsackCoefficients1, b1, variableValues1);
		
		//First check whether a cover inequality exists. NOTE this is not a guarantee that there exists a *violated* cover inequality!
		System.out.println("Cover inequality exists: "+separator.coverInequalityExists());
		
		//If the cover inequality exists we can check whether there is a violated minimal cover:
		System.out.println("Has violated Cover AbstractInequality: "+separator.isMinimalCoverViolated());
		//Get the minimal cover inequality:
		Set<Integer> coverSet=separator.getMinimalCover(); //Get the indices of the variables that occur in the cover
		//Print the cover inequality in a fancy way
		String coverLhs=coverSet.stream().map(i -> "x_"+i.toString()).collect(Collectors.joining(" + "));
		String coverRhs=Integer.toString(separator.getMinimalCoverRHS());
		System.out.println("Cover inequality: "+coverLhs+" <= "+coverRhs+"\n\n");
		
		
		
		//EXAMPLE 2: LIFTED COVER INEQUALITY
		
		//Define the knapsack constraint and a solution to it.
		double[] variableValues2={.5, .5, 1, .5, .5};
		int[] knapsackCoefficients2={47, 45, 79, 53, 53};
		int b2=178;
		System.out.println("Knapsack constr: 47x_0 + 45x_1 + 79x_2 + 53x_3 + 53x_4 <= 178");
		
		//Invoke the separator to separate a lifted cover inequality
		separator.separateLiftedCover(variableValues2.length, knapsackCoefficients2, b2, variableValues2, true);
		
		//First check whether a cover inequality exists. NOTE this is not a guarantee that there exists a *violated* cover inequality!
		System.out.println("Cover inequality exists: "+separator.coverInequalityExists());
		//If the cover inequality exists we can check whether there is a violated minimal cover:
		System.out.println("Has violated Cover AbstractInequality: "+separator.isMinimalCoverViolated());
		
		
		System.out.println("Has violated lifted cover inequality: "+separator.isLiftedCoverViolated());
		int[] liftedCoverInequalityCoefficients = separator.getLiftedCoverCoefficients();
		//Print the cover inequality in a fancy way
		StringJoiner sj=new StringJoiner(" + ", "", "");
		for(int i=0; i<variableValues2.length; i++){
			int coefficient=liftedCoverInequalityCoefficients[i];
			if(coefficient > 0)
				sj.add(""+coefficient+"x_"+i);
		}
		String liftedCoverLhs=sj.toString();
		String liftedCoverRhs=Integer.toString(separator.getLiftedCoverRHS());
		System.out.println("Lifted Cover: "+liftedCoverLhs+" <= " + liftedCoverRhs);
	}
	
	
}
