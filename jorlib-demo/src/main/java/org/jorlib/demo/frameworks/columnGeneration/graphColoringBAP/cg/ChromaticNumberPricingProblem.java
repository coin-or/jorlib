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
 * ChromaticNumberPricingProblem.java
 * -----------------
 * (C) Copyright 2016, by Joris Kinable and Contributors.
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
package org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.cg;

import org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.model.ColoringGraph;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblem;

/**
 * Define the pricing problem
 *
 * @author Joris Kinable
 * @version 29-6-2016
 */
public final class ChromaticNumberPricingProblem extends AbstractPricingProblem<ColoringGraph> {
    /**
     * Create a new Pricing Problem
     *
     * @param dataModel Data model
     * @param name      Name of the pricing problem
     */
    public ChromaticNumberPricingProblem(ColoringGraph dataModel, String name) {
        super(dataModel, name);
    }
}
