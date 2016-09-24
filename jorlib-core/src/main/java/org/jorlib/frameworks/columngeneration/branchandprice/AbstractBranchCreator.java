/* ==========================================
 * jORLib : Java Operations Research Library
 * ==========================================
 *
 * Project Info:  http://www.coin-or.org/projects/jORLib.xml
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015-2016, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 * as published by the Free Software Foundation.
 */
package org.jorlib.frameworks.columngeneration.branchandprice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jorlib.frameworks.columngeneration.branchandprice.branchingdecisions.BranchingDecision;
import org.jorlib.frameworks.columngeneration.colgenmain.AbstractColumn;
import org.jorlib.frameworks.columngeneration.master.MasterData;
import org.jorlib.frameworks.columngeneration.master.cutGeneration.AbstractInequality;
import org.jorlib.frameworks.columngeneration.model.ModelInterface;
import org.jorlib.frameworks.columngeneration.pricing.AbstractPricingProblem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Given a fractional solution obtained after solving a node in the Branch-and-Price tree, this
 * class creates a number of branches, thereby spawning 2 or more child nodes.
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public abstract class AbstractBranchCreator<T extends ModelInterface, U extends AbstractColumn<T, V>,
    V extends AbstractPricingProblem<T, U>>
{

    /** Logger for this class **/
    protected final Logger logger = LoggerFactory.getLogger(AbstractBranchCreator.class);

    /** Data model **/
    protected final T dataModel;
    /** Pricing problems **/
    protected final List<V> pricingProblems;
    /** Branch-and-Price class **/
    protected AbstractBranchAndPrice<T,U,V> bap = null;

    /**
     * Creates a new BranchCreator
     * 
     * @param dataModel data model
     * @param pricingProblem pricing problem
     */
    public AbstractBranchCreator(T dataModel, V pricingProblem)
    {
        this(dataModel, Collections.singletonList(pricingProblem));
    }

    /**
     * Creates a new BranchCreator
     * 
     * @param dataModel data model
     * @param pricingProblems pricing problems
     */
    public AbstractBranchCreator(T dataModel, List<V> pricingProblems)
    {
        this.dataModel = dataModel;
        this.pricingProblems = pricingProblems;
    }

    /**
     * Registers the Branch-and-Price problem for which this class creates branches.
     * 
     * @param bap Branch-and-Price class
     */
    protected void registerBAP(AbstractBranchAndPrice<T,U,V> bap)
    {
        if (this.bap != null)
            throw new RuntimeException(
                "This class can only be associated with a Branch-and-Price problem once!");
        this.bap = bap;
    }

    /**
     * Main method of this class which performs the branching. The method first invokes
     * {@link #canPerformBranching(List) canPerformBranching} to check whether branches can be
     * created. If the latter returns true, the method
     * {@link #createBranch(BAPNode, BranchingDecision, List, List)} is invoked.
     * 
     * @param parentNode Node on which we branch
     * @return List of child nodes if branches could be created, and empty list otherwise
     */
    public List<BAPNode<T, U>> branch(BAPNode<T, U> parentNode)
    {
        // 1. Decide whether we can branch, and if so, on what we can branch.
        if (!this.canPerformBranching(parentNode.solution))
            return Collections.emptyList();
        // 2. If we can branch, create the child nodes
        return this.getBranches(parentNode);
    }

    /**
     * This method decides whether the branching can be performed. To reduce overhead, this method
     * should also store on which aspect of the problem it should branch, e.g. an edge or a
     * variable.
     * 
     * @param solution Fractional column generation solution
     * @return Returns true if the branching can be performed, false otherwise
     */
    protected abstract boolean canPerformBranching(List<U> solution);

    /**
     * Method which returns a list of child nodes after branching on the parentNode
     * 
     * @param parentNode Fractional node on which we branch
     * @return List of child nodes
     */
    protected abstract List<BAPNode<T, U>> getBranches(BAPNode<T, U> parentNode);

    /**
     * Helper method which creates a new child node from a given parent node and a BranchingDecision
     * 
     * @param parentNode Fractional node on which we branch
     * @param branchingDecision Branching decision (i.e the edge between the parent node and its
     *        child node)
     * @param solution Fractional solution
     * @param inequalities Valid inequalities active at the parent node
     * @param <B> branching decision
     * @return a child node
     */
    protected <B extends BranchingDecision<T, U>> BAPNode<T, U> createBranch(
        BAPNode<T, U> parentNode, B branchingDecision, List<U> solution,
        List<AbstractInequality<T, ? extends MasterData<T, U, ? extends AbstractPricingProblem<T, U>, ?>>> inequalities)
    {
        int childNodeID = bap.getUniqueNodeID();
        List<Integer> rootPath1 = new ArrayList<>(parentNode.rootPath);
        rootPath1.add(childNodeID);
        // Copy columns from the parent to the child. The columns need to comply with the Branching
        // Decision. Artificial columns are ignored
        List<U> initSolution = solution
            .stream()
            .filter(
                column -> !column.isArtificialColumn
                    && branchingDecision.columnIsCompatibleWithBranchingDecision(column))
            .collect(Collectors.toList());
        // Copy inequalities to the child node whenever applicable
        List<AbstractInequality<T, ? extends MasterData<T, U, ? extends AbstractPricingProblem<T, U>, ?>>> initCuts = inequalities
            .stream()
            .filter(
                inequality -> branchingDecision
                    .inEqualityIsCompatibleWithBranchingDecision(inequality))
            .collect(Collectors.toList());

        List<BranchingDecision<T,U>> branchingDecisions = new ArrayList<>(parentNode.branchingDecisions);
        branchingDecisions.add(branchingDecision);
        return new BAPNode<T, U>(
            childNodeID, rootPath1, initSolution, initCuts, parentNode.bound, branchingDecisions);
    }

}
