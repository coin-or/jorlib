package org.jorlib.demo.frameworks.columnGeneration.bapExample2.cg;

import org.jorlib.demo.frameworks.columnGeneration.bapExample2.model.ColoringGraph;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblem;

/**
 * Created by jkinable on 6/27/16.
 */
public class ChromaticNumberPricingProblem extends AbstractPricingProblem<ColoringGraph> {
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
