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

import ilog.concert.IloNumVar;
import org.jorlib.demo.frameworks.columngeneration.cuttingstockcg.model.CuttingStock;
import org.jorlib.frameworks.columngeneration.master.MasterData;
import org.jorlib.frameworks.columngeneration.util.OrderedBiMap;

import java.util.Map;

/**
 * Data container for the master problem.
 *
 * @author Joris Kinable
 * @version 11-5-2015
 */
public final class CuttingStockMasterData
    extends MasterData<CuttingStock, CuttingPattern, PricingProblem, IloNumVar>
{

    public CuttingStockMasterData(
        Map<PricingProblem, OrderedBiMap<CuttingPattern, IloNumVar>> varMap)
    {
        super(varMap);
    }
}
