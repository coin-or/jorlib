/* ==========================================
 * jORLib : a free Java OR library
 * ==========================================
 *
 * Project Info:  https://github.com/jkinable/jorlib
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under GPLv3
 *
 */
/* -----------------
 * CuttingStockMasterData.java
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
package org.jorlib.demo.frameworks.columnGeneration.cgExample1.cg;

import ilog.concert.IloNumVar;
import org.jorlib.demo.frameworks.columnGeneration.cgExample1.model.CuttingStock;
import org.jorlib.frameworks.columnGeneration.master.MasterData;
import org.jorlib.frameworks.columnGeneration.util.OrderedBiMap;

import java.util.Map;

/**
 * Data container for the master problem.
 *
 * @author Joris Kinable
 * @version 11-5-2015
 */
public class CuttingStockMasterData extends MasterData<CuttingStock, CuttingPattern, PricingProblem, IloNumVar>{

    public CuttingStockMasterData(Map<PricingProblem, OrderedBiMap<CuttingPattern, IloNumVar>> varMap) {
        super(varMap);
    }
}
