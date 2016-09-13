/* ==========================================
 * jORLib : Java Operations Research Library
 * ==========================================
 *
 * Project Info:  http://www.coin-or.org/projects/jORLib.xml
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2016-2016, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 * as published by the Free Software Foundation.
 */
package org.jorlib.demo.frameworks.columngeneration.graphcoloringbap.cg;

import org.jorlib.demo.frameworks.columngeneration.graphcoloringbap.model.ColoringGraph;
import org.jorlib.frameworks.columngeneration.pricing.AbstractPricingProblem;

/**
 * Define the pricing problem
 *
 * @author Joris Kinable
 * @version 29-6-2016
 */
public final class ChromaticNumberPricingProblem
    extends AbstractPricingProblem<ColoringGraph>
{
    /**
     * Create a new Pricing Problem
     *
     * @param dataModel Data model
     * @param name Name of the pricing problem
     */
    public ChromaticNumberPricingProblem(ColoringGraph dataModel, String name)
    {
        super(dataModel, name);
    }
}
