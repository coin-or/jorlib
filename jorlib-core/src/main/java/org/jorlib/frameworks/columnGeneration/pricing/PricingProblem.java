package org.jorlib.frameworks.columnGeneration.pricing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jorlib.frameworks.columnGeneration.colgenMain.Column;

/**
 * Often, Column generation models decouple in a single Master problem and multiple Pricing problems. The pricing problems can be solved
 * independently. This class models a pricing problem.
 * @author jkinable
 *
 */
public abstract class PricingProblem<T, U extends Column> {

	protected final T modelData;
	//Set of columns active for this pricing problem
	protected final Set<U> activeColumns;
	//List of new columns generated for this pricing problem
	protected final List<U> newColumns;
	
	//Information coming from the master problem
	protected double[] modifiedCosts;
	protected double dualConstant;
		
	public PricingProblem(T modelData){
		this.modelData=modelData;
		activeColumns=new LinkedHashSet<>();
		newColumns=new ArrayList<>();
	}
	
	public void setDualConstant(double dualConstant){
		this.dualConstant=dualConstant;
	}
	public void setModifiedCosts(double[] modifiedCosts){
		this.modifiedCosts=modifiedCosts;
	}
	
	public int getNrColumns(){
		return activeColumns.size();
	}
	
	protected boolean removeColumn(U column){
		return activeColumns.remove(column);
	}
	
	protected void addNewColumns(List<U> newColumns){
		for(U column : newColumns){
			if(activeColumns.contains(column))
				throw new RuntimeException("Duplicate column has been generated for pricing problem: "+this.toString()+"! This column already exists and by definition should not have negative reduced cost: "+column);
			else
				activeColumns.add(column);
		}
		this.newColumns.addAll(newColumns);
	}

	public abstract String toString();
}
