package org.jorlib.frameworks.columnGeneration.master;

public class MasterData {

	//Objective value of the current master problem
	protected double objectiveValue;
	//Number of times the master problem has been solved
	protected int iterations=0;
	//Indicates whether the master problem has been solved to optimality
	protected boolean optimal=false;

}
