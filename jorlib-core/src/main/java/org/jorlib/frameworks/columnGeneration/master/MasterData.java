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
 * MasterData.java
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
package org.jorlib.frameworks.columnGeneration.master;

import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblem;
import org.jorlib.frameworks.columnGeneration.util.OrderedBiMap;

import java.util.*;

/**
 * This is a data object which is being managed by the Master problem. The same data object is passed
 * to the cutHandlers. Therefore, the object can be used to pass information from the master problem to
 * the classes which separate valid inequalities. 
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public class MasterData<U extends AbstractColumn<?,U,V>, V extends AbstractPricingProblem<?,U,V>, W> {

	//Objective value of the current master problem
	public double objectiveValue;
	//Number of times the master problem has been solved
	public int iterations=0;
	//Indicates whether the master problem has been solved to optimality
	public boolean optimal=false;

	//Storage of the variables representing the columns in the master problem
	protected final Map<V, OrderedBiMap<U, W>> varMap;

	public MasterData(Map<V, OrderedBiMap<U, W>> varMap){
		this.varMap=varMap;
	}

	public void addColumn(U column, W variable){
		if(varMap.get(column.associatedPricingProblem).containsKey(column))
			throw new RuntimeException("Duplicate column has been generated for pricing problem: "+column.associatedPricingProblem.toString()+"! This column already exists and by definition should not have negative reduced cost: "+column);
		else
			varMap.get(column.associatedPricingProblem).put(column, variable);
	}

	//Single Pricing Problem methods

	public Set<U> getColumns(){
		if(varMap.size()!= 1)
			throw new UnsupportedOperationException("This method can only be used if there's only a single pricing problem! Use getColumnsForPricingProblem(V pricingProblem) instead.");
		return this.getColumnsForPricingProblem(varMap.keySet().iterator().next());
	}

	public List<U> getColumnsForPricingProblemAsList(){
		if(varMap.size()!= 1)
			throw new UnsupportedOperationException("This method can only be used if there's only a single pricing problem! Use getColumnsForPricingProblemAsList(V pricingProblem) instead.");
		return varMap.get(varMap.keySet().iterator().next()).keyList();
	}

	public int getNrColumns(){
		if(varMap.size()!= 1)
			throw new UnsupportedOperationException("This method can only be used if there's only a single pricing problem! Use getNrColumnsForPricingProblem(V pricingProblem) instead.");
		return this.getNrColumnsForPricingProblem(varMap.keySet().iterator().next());
	}

	public OrderedBiMap<U, W> getVarMap(){
		if(varMap.size() != 1)
			throw new UnsupportedOperationException("This method can only be used if there's only a single pricing problem! Use getVarMapForPricingProblem(V pricingProblem) instead.");
		return this.getVarMapForPricingProblem(varMap.keySet().iterator().next());
	}

	public W getVar(U column){
		if(varMap.size() != 1)
			throw new UnsupportedOperationException("This method can only be used if there's only a single pricing problem! Use getVar(V pricingProblem, U column) instead.");
		return varMap.get(varMap.keySet().iterator().next()).get(column);
	}

	//Multiple Pricing Problems

	public Set<U> getColumnsForPricingProblem(V pricingProblem){
		return varMap.get(pricingProblem).keySet();
	}

	public List<U> getColumnsForPricingProblemAsList(V pricingProblem){
		return varMap.get(pricingProblem).keyList();
	}

	public int getNrColumnsForPricingProblem(V pricingProblem){
		return varMap.get(pricingProblem).size();
	}

	public OrderedBiMap<U, W> getVarMapForPricingProblem(V pricingProblem){
		return  varMap.get(pricingProblem);
	}

	public W getVar(V pricingProblem, U column){
		return varMap.get(pricingProblem).get(column);
	}
}
