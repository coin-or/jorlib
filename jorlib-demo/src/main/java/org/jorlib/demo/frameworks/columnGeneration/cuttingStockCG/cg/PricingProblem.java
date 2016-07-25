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
 * PricingProblem.java
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
package org.jorlib.demo.frameworks.columnGeneration.cuttingStockCG.cg;

import org.jorlib.demo.frameworks.columnGeneration.cuttingStockCG.model.CuttingStock;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblem;

/**
 * Definition of the pricing problem. Since there's only 1 pricing problem in the cutting stock,
 * we can simply extend the pricing problem included in the framework with no further modifications.
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 */
public final class PricingProblem extends AbstractPricingProblem<CuttingStock> {

	public PricingProblem(CuttingStock modelData, String name) {
		super(modelData, name);
	}

}
