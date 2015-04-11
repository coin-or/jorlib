package org.jorlib.frameworks.columnGeneration.master.cuts;

public abstract class Inequality {
	public final InequalityType type;
	
	public Inequality(InequalityType type){
		this.type=type;
	}
	
	@Override
	public abstract boolean equals(Object o);
	
	@Override
	public abstract int hashCode();
	
}
