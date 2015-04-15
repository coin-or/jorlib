package org.jorlib.demo.frameworks.columnGeneration.example1.cg;

import java.util.Arrays;

import org.jorlib.demo.frameworks.columnGeneration.example1.model.CuttingStock;
import org.jorlib.frameworks.columnGeneration.colgenMain.Column;

/**
 * Implementation of a column in the cutting stock problem.
 * A column is a pattern defining how to cut a specific raw.
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 */
public class CuttingPattern extends Column<CuttingStock, CuttingPattern, PricingProblem> {

	//Denotes the number of times each final is cut out of the raw. 
	public final int[] yieldVector;
	
	public CuttingPattern(String creator, boolean isArtificial, int[] pattern, PricingProblem pricingProblem) {
		super(creator, isArtificial, pricingProblem);
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
		String s="Value: "+this.value+" Cutting pattern: "+Arrays.toString(yieldVector)+" creator: "+this.creator;
		return s;
	}

}
