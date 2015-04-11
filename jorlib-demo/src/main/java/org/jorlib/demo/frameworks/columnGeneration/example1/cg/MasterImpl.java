package org.jorlib.demo.frameworks.columnGeneration.example1.cg;

import java.util.List;

import org.jorlib.demo.frameworks.columnGeneration.example1.model.CuttingStock;
import org.jorlib.frameworks.columnGeneration.master.Master;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutHandler;

/**
 * Implementation of Master class
 * @author jkinable
 *
 */
public class MasterImpl extends Master<CuttingStock, PricingProblemImpl, CuttingPattern> {

	public MasterImpl(CuttingStock modelData, CutHandler cutHandler) {
		super(modelData, cutHandler);
	}

	@Override
	protected boolean solveMasterProblem(long timeLimit) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public  double[] getReducedCostVector(
			PricingProblemImpl pricingProblem) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getDualConstant(PricingProblemImpl pricingProblem) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPatternCount(PricingProblemImpl pricingProblem) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void buildModel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addColumn(CuttingPattern column) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<CuttingPattern> getSolution() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
