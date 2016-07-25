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
 * SmallestEnclosingCircleCalculatorDemo.java
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
package org.jorlib.demo.alg.packing.circlePacking;

import org.jorlib.alg.packing.circlePacking.SmallestEnclosingCircleCalculator;

/**
 * Simple class which calculates the smallest enclosing circle around a fixed set of circles
 * @author Joris Kinable
 * @since April 9, 2015
 *
 */
public final class SmallestEnclosingCircleCalculatorDemo {
	public static void main(String[] args){
		
		//Define some circles. In this example we define 3 circles
		double[] xCors={-1,1,-0.8,0.9};
		double[] yCors={0,0,-1.8,-1.9};
		double[] radii={1,1,.8,.9};
		
		//Calculate the smallest enclosing circle (exaxt)
		SmallestEnclosingCircleCalculator cecc=new SmallestEnclosingCircleCalculator();
		cecc.calcExactContainer(xCors, yCors, radii);
		
		//Print the result
		System.out.println("Exact - Smallest enclosing circle has a radius of: "+cecc.getRadius()+
				" and its center is at: "+cecc.getContainerPositionAsPoint());
		
		
		//Calculate the smallest enclosing circle (approximate)
		cecc.calculateApproximateContainer(xCors, yCors, radii);
		
		//Print the result
		System.out.println("Approximate - Smallest enclosing circle has a radius of: "+cecc.getRadius()+
				" and its center is at: "+cecc.getContainerPositionAsPoint());
	}
}
