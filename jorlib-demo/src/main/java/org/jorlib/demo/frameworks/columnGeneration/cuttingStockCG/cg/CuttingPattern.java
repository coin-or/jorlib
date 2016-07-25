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
 * CuttingPattern.java
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
package org.jorlib.demo.frameworks.columnGeneration.cuttingStockCG.cg;

import java.util.Arrays;

import org.jorlib.demo.frameworks.columnGeneration.cuttingStockCG.model.CuttingStock;
import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;

/**
 * Implementation of a column in the cutting stock problem.
 * A column is a pattern defining how to cut a specific raw.
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 */
public final class CuttingPattern extends AbstractColumn<CuttingStock, PricingProblem> {

	/** Denotes the number of times each final is cut out of the raw. **/
	public final int[] yieldVector;

	public CuttingPattern(String creator, boolean isArtificial, int[] pattern, PricingProblem pricingProblem) {
		super(pricingProblem, isArtificial, creator);
		this.yieldVector=pattern;
	}

	@Override
	public boolean equals(Object o) {
		if(this==o)
			return true;
		if(!(o instanceof CuttingPattern))
			return false;
		CuttingPattern other=(CuttingPattern) o;
		return Arrays.equals(this.yieldVector, other.yieldVector);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(yieldVector);
	}

	@Override
	public String toString() {
		return "Value: "+ this.value+" Cutting pattern: "+Arrays.toString(yieldVector)+" creator: "+ this.creator;
	}

}
