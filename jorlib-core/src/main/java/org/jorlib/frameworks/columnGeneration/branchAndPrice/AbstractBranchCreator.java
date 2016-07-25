/* ==========================================
 * jORLib : a free Java OR library
 * ==========================================
 *
 * Project Info:  https://github.com/jkinable/jorlib
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 *
 */
/* -----------------
 * AbstractBranchCreator.java
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
package org.jorlib.frameworks.columnGeneration.branchAndPrice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.AbstractInequality;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Given a fractional solution obtained after solving a node in the Branch-and-Price tree, this class creates a number
 * of branches, thereby spawning 2 or more child nodes.
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public abstract class AbstractBranchCreator<T,U extends AbstractColumn<T, V>,V extends AbstractPricingProblem<T>> {

	/** Logger for this class **/
	protected final Logger logger = LoggerFactory.getLogger(AbstractBranchCreator.class);

	/** Data model **/
	protected final T dataModel;
	/** Pricing problems **/
	protected final List<V> pricingProblems;
	/** Branch-and-Price class **/
	protected AbstractBranchAndPrice bap=null;

	/**
	 * Creates a new BranchCreator
	 * @param dataModel data model
	 * @param pricingProblem pricing problem
	 */
	public AbstractBranchCreator(T dataModel, V pricingProblem){
		this(dataModel, Collections.singletonList(pricingProblem));
	}

	/**
	 * Creates a new BranchCreator
	 * @param dataModel data model
	 * @param pricingProblems pricing problems
	 */
	public AbstractBranchCreator(T dataModel, List<V> pricingProblems){
		this.dataModel = dataModel;
		this.pricingProblems=pricingProblems;
	}

	/**
	 * Registers the Branch-and-Price problem for which this class creates branches.
	 * @param bap Branch-and-Price class
	 */
	protected void registerBAP(AbstractBranchAndPrice bap){
		if(this.bap != null)
			throw new RuntimeException("This class can only be associated with a Branch-and-Price problem once!");
		this.bap=bap;
	}

	/**
	 * Main method of this class which performs the branching. The method first invokes {@link #canPerformBranching(List) canPerformBranching}
	 * to check whether branches can be created. If the latter returns true, the method
	 * {@link #createBranch(BAPNode, BranchingDecision, List, List)} is invoked.
	 * @param parentNode Node on which we branch
	 * @return List of child nodes if branches could be created, and empty list otherwise
	 */
	public List<BAPNode<T,U>> branch(BAPNode<T,U> parentNode){
		//1. Decide whether we can branch, and if so, on what we can branch. 
		if(!this.canPerformBranching(parentNode.solution))
			return Collections.emptyList();
		//2. If we can branch, create the child nodes
		return this.getBranches(parentNode);
	}

	/**
	 * This method decides whether the branching can be performed. To reduce overhead, this method should also store on which aspect of the problem
	 * it should branch, e.g. an edge or a variable.
	 * @param solution Fractional column generation solution
	 * @return Returns true if the branching can be performed, false otherwise
	 */
	protected abstract boolean canPerformBranching(List<U> solution);

	/**
	 * Method which returns a list of child nodes after branching on the parentNode
	 * @param parentNode Fractional node on which we branch
	 * @return List of child nodes
	 */
	protected abstract List<BAPNode<T,U>> getBranches(BAPNode<T,U> parentNode);

	/**
	 * Helper method which creates a new child node from a given parent node and a BranchingDecision
	 * @param parentNode Fractional node on which we branch
	 * @param branchingDecision Branching decision (i.e the edge between the parent node and its child node)
	 * @param solution Fractional solution
	 * @param inequalities Valid inequalities active at the parent node
	 * @param <B> branching decision
	 * @return a child node
	 */
	protected <B extends BranchingDecision<T,U>> BAPNode<T,U> createBranch(BAPNode<T,U> parentNode, B branchingDecision, List<U> solution, List<AbstractInequality> inequalities){
		int childNodeID= bap.getUniqueNodeID();
		List<Integer> rootPath1=new ArrayList<>(parentNode.rootPath);
		rootPath1.add(childNodeID);
		//Copy columns from the parent to the child. The columns need to comply with the Branching Decision. Artificial columns are ignored
		List<U> initSolution= solution.stream().filter(column -> !column.isArtificialColumn && branchingDecision.columnIsCompatibleWithBranchingDecision(column)).collect(Collectors.toList());
		//Copy inequalities to the child node whenever applicable
		List<AbstractInequality> initCuts= inequalities.stream().filter(inequality -> branchingDecision.inEqualityIsCompatibleWithBranchingDecision(inequality)).collect(Collectors.toList());

		List<BranchingDecision> branchingDecisions=new ArrayList<>(parentNode.branchingDecisions);
		branchingDecisions.add(branchingDecision);
		return new BAPNode<>(childNodeID, rootPath1, initSolution, initCuts, parentNode.bound, branchingDecisions);
	}

}
