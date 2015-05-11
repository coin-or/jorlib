package org.jorlib.demo.frameworks.columnGeneration.example1.cg;

import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import org.jorlib.frameworks.columnGeneration.master.MasterData;
import org.jorlib.frameworks.columnGeneration.util.OrderedBiMap;

import java.util.Map;

/**
 * Created by jkinable on 5/11/15.
 */
public class CuttingStockMasterData extends MasterData<CuttingPattern, PricingProblem, IloNumVar>{

    public CuttingStockMasterData(Map<PricingProblem, OrderedBiMap<CuttingPattern, IloNumVar>> varMap) {
        super(varMap);
    }
}
