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
package org.jorlib.frameworks.columngeneration.tsp.cg;

import org.jorlib.frameworks.columngeneration.pricing.AbstractPricingProblem;
import org.jorlib.frameworks.columngeneration.tsp.model.MatchingColor;
import org.jorlib.frameworks.columngeneration.tsp.model.TSP;

/**
 * Define a pricing problem which is unique for every color: a pricing problem for the blue
 * matchings and a pricing problem for the red matchings.
 *
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public final class PricingProblemByColor
    extends AbstractPricingProblem<TSP>
{

    /** Color of the matching group. Can be either Red or Blue **/
    public final MatchingColor color;

    /**
     * Creates a new Pricing problem instance
     * 
     * @param modelData data model
     * @param name name of pricing problem
     * @param color color
     */
    public PricingProblemByColor(TSP modelData, String name, MatchingColor color)
    {
        super(modelData, name);
        this.color = color;
    }
}
