package org.jorlib.frameworks.columnGeneration.master;

import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutHandler;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblem;

public interface MasterFactory {
	public <T, U extends AbstractColumn<T,U,V>, V extends AbstractPricingProblem<T,U,V>, W extends MasterData> AbstractMaster<T, V, U, ? extends MasterData> createMaster(T modelData, CutHandler<T,W> cutHandler);
}
