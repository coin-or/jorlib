package org.jorlib.demo.frameworks.columnGeneration.example1.cg;

import java.util.Arrays;

import org.jorlib.frameworks.columnGeneration.colgenMain.Column;

public class CuttingPattern extends Column {

	//Denotes the number of times each final is cut out of the raw. 
	public final int[] pattern;
	
	public CuttingPattern(String creator, boolean isArtificial, int[] pattern) {
		super(creator, isArtificial);
		this.pattern=pattern;
	}

	@Override
	public boolean equals(Object o) {
		if(this==o)
			return true;
		if(!(o instanceof CuttingPattern))
			return false;
		CuttingPattern other=(CuttingPattern) o;
		return Arrays.equals(this.pattern, other.pattern);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(pattern);
	}

	@Override
	public String toString() {
		String s="Cutting pattern: "+Arrays.toString(pattern);
		return s;
	}

}
