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
 * AbstractPricingProblem.java
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
package org.jorlib.frameworks.columnGeneration.pricing;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.Matching;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecisionListener;
import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines a Pricing Problem.
 * Often, Column generation models decouple in a single Master problem and one or more Pricing problems. The pricing problems can be solved
 * independently. This class models a pricing problem.
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public abstract class AbstractPricingProblem<T, U extends AbstractColumn<T,U,V>, V extends AbstractPricingProblem<T,U,V>> implements BranchingDecisionListener{

	protected final Logger logger = LoggerFactory.getLogger(AbstractPricingProblem.class);
	
	//Data object
	protected final T modelData;
	
	//Name of this pricing problem
	public final String name;
	//Set of columns generated for this pricing problem
//	protected final Set<U> activeColumns;
	
	//Information coming from the master problem
	public double[] modifiedCosts;
	public double dualConstant;
		
	public AbstractPricingProblem(T modelData, String name){
		this.modelData=modelData;
		this.name=name;
//		activeColumns=new LinkedHashSet<>();
	}
	
	public void initPricingProblem(double[] modifiedCosts){
		this.initPricingProblem(modifiedCosts,0);
	}
	public void initPricingProblem(double[] modifiedCosts, double dualConstant){
		this.modifiedCosts=modifiedCosts;
		this.dualConstant=dualConstant;
	}
	
//	public int getNrColumnsForPricingProblem(){
//		return activeColumns.size();
//	}
	
//	public boolean removeColumn(U column){
//		return activeColumns.remove(column);
//	}
	
//	public void addColumn(U column){
//		if(activeColumns.contains(column))
//			throw new RuntimeException("Duplicate column has been generated for pricing problem: "+this.toString()+"! This column already exists and by definition should not have negative reduced cost: "+column);
//		else
//			activeColumns.add(column);
//	}
	
	public String toString(){
		return name;
	}

	@Override
	public void branchingDecisionPerformed(BranchingDecision bd) {
//		Iterator<U> it=this.activeColumns.iterator();
//		while(it.hasNext()){
//			U column=it.next();
//			if(!bd.columnIsCompatibleWithBranchingDecision(column))
//				it.remove();
//		}
//		this.activeColumns.clear();

		//Nothing to do here
	}

	@Override
	public void branchingDecisionRewinded(BranchingDecision bd) {
		//Nothing to do here
	}
}
