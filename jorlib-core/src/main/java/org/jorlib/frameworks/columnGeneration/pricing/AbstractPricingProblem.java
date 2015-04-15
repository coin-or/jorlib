package org.jorlib.frameworks.columnGeneration.pricing;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jorlib.frameworks.columnGeneration.colgenMain.Column;

/**
 * Often, Column generation models decouple in a single Master problem and multiple Pricing problems. The pricing problems can be solved
 * independently. This class models a pricing problem.
 * @author jkinable
 *
 */
public abstract class AbstractPricingProblem<T, U extends Column<T,U,V>, V extends AbstractPricingProblem<T,U,V>> {

	protected final T modelData;
	
	public final String name;
	//Set of columns active for this pricing problem
	protected final Set<U> activeColumns;
	//List of new columns generated for this pricing problem
//	protected final List<U> newColumns;
	
	//Information coming from the master problem
	public double[] modifiedCosts;
	public double dualConstant;
		
	public AbstractPricingProblem(T modelData, String name){
		this.modelData=modelData;
		this.name=name;
		activeColumns=new LinkedHashSet<>();
//		newColumns=new ArrayList<>();
	}
	
	public void initPricingProblem(double[] modifiedCosts){
		this.initPricingProblem(modifiedCosts,0);
	}
	public void initPricingProblem(double[] modifiedCosts, double dualConstant){
		this.modifiedCosts=modifiedCosts;
		this.dualConstant=dualConstant;
//		newColumns.clear();
	}
	
	public int getNrColumns(){
		return activeColumns.size();
	}
	
	public boolean removeColumn(U column){
		return activeColumns.remove(column);
	}
	
	public void addColumn(U column){
		if(activeColumns.contains(column))
			throw new RuntimeException("Duplicate column has been generated for pricing problem: "+this.toString()+"! This column already exists and by definition should not have negative reduced cost: "+column);
		else
			activeColumns.add(column);
	}
	
//	protected void addNewColumns(List<U> newColumns){
//		for(U column : newColumns){
//			if(activeColumns.contains(column))
//				throw new RuntimeException("Duplicate column has been generated for pricing problem: "+this.toString()+"! This column already exists and by definition should not have negative reduced cost: "+column);
//			else
//				activeColumns.add(column);
//		}
//		this.newColumns.addAll(newColumns);
//	}
	
//	public List<U> getNewColumns(){
//		return newColumns;
//	}

	public String toString(){
		return name;
	}
}
