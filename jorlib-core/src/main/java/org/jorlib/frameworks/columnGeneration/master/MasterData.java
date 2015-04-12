package org.jorlib.frameworks.columnGeneration.master;

public class MasterData {

	//Objective value of the current master problem
	public double objectiveValue;
	//Number of times the master problem has been solved
	public int iterations=0;
	//Indicates whether the master problem has been solved to optimality
	public boolean optimal=false;

}
