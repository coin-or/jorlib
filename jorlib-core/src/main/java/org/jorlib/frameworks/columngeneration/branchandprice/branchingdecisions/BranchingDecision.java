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
import org.jorlib.frameworks.columngeneration.master.cutGeneration.AbstractInequality;
import org.jorlib.frameworks.columngeneration.pricing.AbstractPricingProblem;

/**
 * Interface defining a BranchingDecision.
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public interface BranchingDecision<T, U extends AbstractColumn<T, ? extends AbstractPricingProblem>>
{

    /**
     * Determine whether a particular column from the parent node is feasible for the child node
     * resulting from the Branching Decision and hence can be transferred.
     *
     * @param column column
     * @return true if the column is feasible, false otherwise
     */
    boolean columnIsCompatibleWithBranchingDecision(U column);

    /**
     * Determine whether a particular inequality from the parent node is feasible for the child node
     * resulting from the Branching Decision and hence can be transferred.
     *
     * @param inequality inequality
     * @return true if the inequality is feasible, false otherwise
     */
    boolean inEqualityIsCompatibleWithBranchingDecision(AbstractInequality inequality);

}
