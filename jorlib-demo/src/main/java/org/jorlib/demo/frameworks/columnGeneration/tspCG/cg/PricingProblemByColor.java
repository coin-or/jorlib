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
 * PricingProblemByColor.java
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
package org.jorlib.demo.frameworks.columnGeneration.tspCG.cg;

import org.jorlib.demo.frameworks.columnGeneration.tspCG.model.MatchingColor;
import org.jorlib.demo.frameworks.columnGeneration.tspCG.model.TSP;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblem;

/**
 * Define a pricing problem which is unique for every color: a pricing problem for the blue matchings and a pricing problem
 * for the red matchings.
 *
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public final class PricingProblemByColor extends AbstractPricingProblem<TSP> {
	
	/** Color of the matching group. Can be either Red or Blue **/
	public final MatchingColor color;

	/**
	 * Creates a new Pricing problem instance
	 * @param modelData data model
	 * @param name name of pricing problem
	 * @param color color
	 */
	public PricingProblemByColor(TSP modelData, String name, MatchingColor color) {
		super(modelData, name);
		this.color=color;
	}
}
