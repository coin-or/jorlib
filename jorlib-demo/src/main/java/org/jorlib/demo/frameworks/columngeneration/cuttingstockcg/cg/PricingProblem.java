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
package org.jorlib.demo.frameworks.columngeneration.cuttingstockcg.cg;

import org.jorlib.demo.frameworks.columngeneration.cuttingstockcg.model.CuttingStock;
import org.jorlib.frameworks.columngeneration.pricing.AbstractPricingProblem;

/**
 * Definition of the pricing problem. Since there's only 1 pricing problem in the cutting stock, we
 * can simply extend the pricing problem included in the framework with no further modifications.
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 */
public final class PricingProblem
    extends AbstractPricingProblem<CuttingStock>
{

    public PricingProblem(CuttingStock modelData, String name)
    {
        super(modelData, name);
    }

}
