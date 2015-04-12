package org.jorlib.demo.frameworks.columnGeneration.example1.cg;

import java.util.Arrays;

import org.jorlib.demo.frameworks.columnGeneration.example1.model.CuttingStock;
import org.jorlib.frameworks.columnGeneration.colgenMain.Column;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblem;

public class CuttingPattern extends Column<CuttingStock, CuttingPattern> {

	//Denotes the number of times each final is cut out of the raw. 
	public final int[] yieldVector;
	
	public CuttingPattern(String creator, boolean isArtificial, int[] pattern, CuttingStockPricingProblem pricingProblem) {
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
