package org.jorlib.demo.frameworks.columnGeneration.example2.cg.master.cuts;

import java.util.Set;

import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutGenerator;
import org.jorlib.frameworks.columnGeneration.master.cuts.Inequality;

public class SubtourInequality extends Inequality{

	public final Set<Integer> cutSet;
	
	public SubtourInequality(CutGenerator maintainingGenerator, Set<Integer> cutSet) {
		super(maintainingGenerator);
		this.cutSet=cutSet;
	}

	@Override
	public boolean equals(Object o) {
		if(this==o)
			return true;
		else if(!(o instanceof SubtourInequality))
			return false;
		SubtourInequality other=(SubtourInequality)o;
		return this.cutSet.equals(other.cutSet);
	}

	@Override
	public int hashCode() {
		return cutSet.hashCode();
	}

}
