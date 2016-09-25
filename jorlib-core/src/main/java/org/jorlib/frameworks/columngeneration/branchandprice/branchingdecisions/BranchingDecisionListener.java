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
package org.jorlib.frameworks.columngeneration.branchandprice.branchingdecisions;

import org.jorlib.frameworks.columngeneration.colgenmain.AbstractColumn;
import org.jorlib.frameworks.columngeneration.master.MasterData;
import org.jorlib.frameworks.columngeneration.model.ModelInterface;
import org.jorlib.frameworks.columngeneration.pricing.AbstractPricingProblem;

/**
 * Interface defining a BranchingDecision Listener. Every BranchingDecision listener is informed
 * about branching decisions which are executed.
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public interface BranchingDecisionListener<T extends ModelInterface, U extends AbstractColumn<T, ? extends AbstractPricingProblem<T, U>>>
{
    /**
     * This method is called when a branching decision is executed
     * 
     * @param bd branching decision
     */
    void branchingDecisionPerformed(BranchingDecision<T,U> bd);

    /**
     * This method is called when a branching decision is reversed (backtracking in the
     * Branch-and-Price tree)
     * 
     * @param bd branching decision
     */
    void branchingDecisionReversed(BranchingDecision<T,U> bd);
}
