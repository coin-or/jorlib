package org.jorlib.frameworks.columnGeneration.master.cuts;

import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutGenerator;

public abstract class Inequality {
	
	public final CutGenerator maintainingGenerator;
	
	public Inequality(CutGenerator maintainingGenerator){
		this.maintainingGenerator=maintainingGenerator;
	}
	
	@Override
	public abstract boolean equals(Object o);
	
	@Override
	public abstract int hashCode();
	
}
