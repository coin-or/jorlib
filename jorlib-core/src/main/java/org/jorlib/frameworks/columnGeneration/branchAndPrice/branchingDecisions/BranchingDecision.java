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
 * BranchingDecision.java
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
package org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions;

import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.AbstractInequality;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblem;

/**
 * Interface defining a BranchingDecision.
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public interface BranchingDecision<T,U extends AbstractColumn<T, ? extends AbstractPricingProblem>> {

	/**
	 * Determine whether a particular column from the parent node is feasible for the child node resulting from the Branching Decision
	 * and hence can be transferred.
	 *
	 * @param column column
	 * @return true if the column is feasible, false otherwise
	 */
	boolean columnIsCompatibleWithBranchingDecision(U column);

	/**
	 * Determine whether a particular inequality from the parent node is feasible for the child node resulting from the Branching Decision
	 * and hence can be transferred.
	 *
	 * @param inequality inequality
	 * @return true if the inequality is feasible, false otherwise
	 */
	boolean inEqualityIsCompatibleWithBranchingDecision(AbstractInequality inequality);
	
}
