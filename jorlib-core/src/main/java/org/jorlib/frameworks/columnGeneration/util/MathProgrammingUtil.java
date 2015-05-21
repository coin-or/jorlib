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
 * MathProgrammingUtil.java
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
package org.jorlib.frameworks.columnGeneration.util;

/**
 * Utility class for LP/MIP solvers
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public class MathProgrammingUtil {

	/** Configuration file for this class **/
	private static final Configuration config=Configuration.getConfiguration();

	/**
	 * Returns the nearest rounded double. Throws an exception if the nearest double is further away than a given constant
	 * @param value value to be rounded
	 * @return rounded double
	 */
	public static double doubleToRoundedDouble(double value){
		double result= Math.round(value);
		if(Math.abs(value-result)<config.PRECISION)
			return result;
		else
			throw new RuntimeException("Failed to round double, not near an integer value: " + value);
	}
	
	/**
	 * Returns the nearest rounded int. Throws an exception if the nearest int is further away than a given constant
	 * @param value value to be rounded
	 * @return integer represented by the double
	 */
	public static int doubleToInt(double value){
		int result= (int)Math.round(value);
		if(Math.abs(value-result)<config.PRECISION)
			return result;
		else
			throw new RuntimeException("Failed to round double, not near an integer value: " + value);
	}
	
	/**
	 * Returns true if the variable is +/- 1, false if the variable is +/- 0, and throws an error otherwise
	 * @param value value to be rounded
	 * @return boolean value represented by the double
	 */
	public static boolean doubleToBoolean(double value){
		if(Math.abs(1-value) < config.PRECISION ){
			return true;
		}
		else if(Math.abs(value) < config.PRECISION){
			return false;
		}
		else throw new RuntimeException("Failed to convert to boolean, not near zero or one: " + value);
	}
	
	/**
	 * Returns true if variable is fractional, i.e more than epsilon away from the nearest int value, false otherwise.
	 * @param value to be checked
	 * @return {@code true} when the provided double is fractional
	 */
	public static boolean isFractional(double value){
		return Math.abs(value-Math.round(value)) > config.PRECISION;
	}
}
