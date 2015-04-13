package org.jorlib.demo.frameworks.columnGeneration.example2.cg.master;

import ilog.concert.IloColumn;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.UnknownObjectException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jorlib.demo.frameworks.columnGeneration.example1.cg.CuttingPattern;
import org.jorlib.demo.frameworks.columnGeneration.example2.cg.Matching;
import org.jorlib.demo.frameworks.columnGeneration.example2.cg.MatchingGroup;
import org.jorlib.demo.frameworks.columnGeneration.example2.model.Color;
import org.jorlib.demo.frameworks.columnGeneration.example2.model.Edge;
import org.jorlib.demo.frameworks.columnGeneration.example2.model.TSP;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.master.Master;
import org.jorlib.frameworks.columnGeneration.master.MasterData;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutHandler;
import org.jorlib.frameworks.columnGeneration.util.OrderedBiMap;

public class MasterImpl extends Master<TSP, MatchingGroup, Matching> {

	IloCplex master; //Cplex instance
	private IloObjective obj; //Objective function
	private IloRange exactlyOneRedMatchingConstr; //Constraint
	private IloRange exactlyOneBlueMatchingConstr; //Constraint
	private Map<Edge, IloRange> edgeOnlyUsedOnceConstr;
	public OrderedBiMap<Matching, IloNumVar> matchingVars; //Variables
	
	public MasterImpl(TSP modelData, MasterData masterData, CutHandler cutHandler) {
		super(modelData, masterData, cutHandler);
		this.buildModel();
	}

	@Override
	protected boolean solveMasterProblem(long timeLimit)	throws TimeLimitExceededException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void initializePricingProblem(MatchingGroup pricingProblem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void buildModel() {
		try {
			master=new IloCplex(); //Create cplex instance
			master.setOut(null); //Disable cplex output
			master.setParam(IloCplex.IntParam.Threads,config.MAXTHREADS); //Set number of threads that may be used by the master
			
			//Define objective
			obj=master.addMinimize();
			
			//Define constraints
			exactlyOneRedMatchingConstr=master.addRange(1, 1, "exactlyOneRed");
			exactlyOneBlueMatchingConstr=master.addRange(1, 1, "exactlyOneBlue");
			
			edgeOnlyUsedOnceConstr=new LinkedHashMap<Edge, IloRange>();
			for(int i=0; i<modelData.N-1; i++){
				for(int j=i+1; j<modelData.N; j++){
					Edge edge=new Edge(i, j);
					IloRange constr=master.addRange(1, 1, "edgeOnlyUsedOnce_"+i+"_"+j);
					edgeOnlyUsedOnceConstr.put(edge,  constr);
				}
			}
			
			//Define a container for the variables
			matchingVars=new OrderedBiMap<>();
		} catch (IloException e) {
			e.printStackTrace();
		}
		logger.info("Finished building master");
		
	}

	@Override
	public void addColumn(Matching column) {
		Color matchingColor= column.associatedPricingProblem.color;
		//Register column with objective
		IloColumn iloColumn=master.column(obj,1);
		//Register column with exactlyOneRedMatching/exactlyOneBlueMatching constr
		if(column.associatedPricingProblem.color=Color.RED){
			iloColumn=iloColumn.and(master.column(exactlyOneRedMatchingConstr, 1));
		}else{
			iloColumn=iloColumn.and(master.column(exactlyOneBlueMatchingConstr, 1));
		}
		//Register column with edgeOnlyUsedOnce constraints
		for(int i=0; i<modelData.N-1; i++){
			for(int j=i+1; j<modelData.N; j++){
				if(column.succ[i]==j){
					Edge edge=new Edge(i, j);
					iloColumn=iloColumn.and(master.column(edgeOnlyUsedOnceConstr.get(edge), 1));
				}
			}
		}
		//Register column with subtour elimination constraints
		
		//Create the variable and store it
		IloNumVar var=master.numVar(iloColumn, 0, Double.MAX_VALUE, "z_"+matchingColor.name()+"_"+column.associatedPricingProblem.getNrColumns());
		master.add(var);
		matchingVars.put(column, var);
	}

	@Override
	public List<Matching> getSolution() {
		List<Matching> solution=new ArrayList<>();
		try {
			Matching[] matchings=matchingVars.getKeysAsArray(new Matching[matchingVars.size()]);
			IloNumVar[] vars=matchingVars.getValuesAsArray(new IloNumVar[matchingVars.size()]);
			double[] values=master.getValues(vars);
			
			//Iterate over each column and add it to the solution if it has a non-zero value
			for(int i=0; i<matchings.length; i++){
				matchings[i].value=values[i];
				if(values[i]>=config.PRECISION){
					solution.add(matchings[i]);
				}
			}
		} catch (UnknownObjectException e) {
			e.printStackTrace();
		} catch (IloException e) {
			e.printStackTrace();
		}
		return solution;
	}

	@Override
	public void printSolution() {
		List<Matching> solution=this.getSolution();
		for(Matching m : solution)
			System.out.println(m);
	}

	@Override
	public void close() {
		master.end();
	}

}
