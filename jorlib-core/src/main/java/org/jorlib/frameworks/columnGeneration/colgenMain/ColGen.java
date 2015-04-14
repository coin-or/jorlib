package org.jorlib.frameworks.columnGeneration.colgenMain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.master.Master;
import org.jorlib.frameworks.columnGeneration.master.MasterData;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutHandler;
import org.jorlib.frameworks.columnGeneration.master.cuts.Inequality;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblem;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemBunddle;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemManager;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemSolver;
import org.jorlib.frameworks.columnGeneration.pricing.DefaultPricingProblemSolverFactory;
import org.jorlib.frameworks.columnGeneration.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Solves geoxam through column generation
 * 
 * U is reserved for columns
 * T is reserved for the model
 * 
 * @author jkinable
 */
public class ColGen<T, U extends Column<T,U,V>, V extends PricingProblem<T,U,V>> {
	
	final Logger logger = LoggerFactory.getLogger(ColGen.class);
	static final Configuration config=Configuration.getConfiguration();

	private final T dataModel;
	
	//Define the master problem
	private final Master<T, V, U, ? extends MasterData> master;
	//Define the pricing problems
	private final List<V> pricingProblems;
	//Maintain the classes which can be used to solve the pricing problems
	private final List<Class<? extends PricingProblemSolver<T, U, V>>> solvers;
	//For each solver, we maintain an instance for each pricing problem. This gives a |solvers|x|pricingProblems| array
	private final List<PricingProblemBunddle<T, U, V>> pricingProblemBunddles;
	//Manages parallel execution of pricing problems
	private final PricingProblemManager<T,U, V> pricingProblemManager;
	
	//Objective value of column generation procedure
	private double objective; 
	//Colgen is terminated if objective exceeds upperBound. Upperbound is set equal to the best incumbent integer solution
	private int upperBound=Integer.MAX_VALUE;
	//Lower bound on the objective. If lowerbound > upperBound, this node can be pruned.
	private double lowerBound=0;
	//Total runtime of column generation solve procedure
	private long runtime;
	//Total number of iterations.
	private int nrOfIterations=0;
	//Total time spent on solving the master problem
	private long masterSolveTime=0;
	//Total time spent on solving the pricing problem
	private long pricingSolveTime=0;
	//Total number of columns generated and added to the master problem
	private int nrGeneratedColumns=0;
	
	public ColGen(T dataModel, 
					Master<T,V,U, ? extends MasterData> master, 
					List<V> pricingProblems,
					List<Class<? extends PricingProblemSolver<T, U, V>>> solvers,
					List<U> initSolution,
					int upperBound){
		this.dataModel=dataModel;
		this.master=master;
		this.pricingProblems=pricingProblems;
		this.solvers=solvers;//solvers;
		master.addColumns(initSolution);
		this.upperBound=upperBound;
		
		//Generate the pricing problem instances
		pricingProblemBunddles=new ArrayList<>();
		for(Class<? extends PricingProblemSolver<T, U, V>> solverClass : solvers){
			DefaultPricingProblemSolverFactory<T, U, V> factory=new DefaultPricingProblemSolverFactory<T, U, V>(solverClass, solverClass.getName(), dataModel);
			PricingProblemBunddle<T, U, V> bunddle=new PricingProblemBunddle<>(solverClass, pricingProblems, factory);
			pricingProblemBunddles.add(bunddle);
		}
		
		pricingProblemManager=new PricingProblemManager<T,U, V>(pricingProblems, pricingProblemBunddles);
	}
	
	public ColGen(T dataModel, 
			Master<T,V,U,? extends MasterData> master, 
			V pricingProblem,
			List<Class<? extends PricingProblemSolver<T, U, V>>> solvers,
			List<U> initSolution,
			int upperBound){
		this(dataModel, master, Arrays.asList(pricingProblem), solvers, initSolution, upperBound);
	}
	
	
	/*public <U extends Column> ColGen(T dataModel, CutHandler cutHandler, PricingSolvers[] pricingAlgorithms, EnumMap<PricingSolvers, List<PricingProblem>> pricingProblems, PricingProblemManager pricingProblemManager, List<U> initSolution, List<Inequality> initialCuts, long timeLimit, int upperBound) throws TimeLimitExceededException{
		this.dataModel=dataModel;
		this.cutHandler=cutHandler;
		master=new MasterImplementation(geoxam, cutHandler);
		master.addCuts(initialCuts);
		if(initSolution != null){
			master.setInitialSolution(initSolution);
			this.upperBound=upperBound;
		}
		this.pricingAlgorithms=pricingAlgorithms;
		this.pricingProblems=pricingProblems;
		this.pricingProblemManager=pricingProblemManager;
		
		this.solve(timeLimit);
	}*/
	
	public void solve(long timeLimit) throws TimeLimitExceededException{
		//set time limit pricing problems
		pricingProblemManager.setTimeLimit(timeLimit);
		
		runtime=System.currentTimeMillis();
//		if(config.EXPORT_MODEL) master.exportModel("master0.lp");
//			List<List<ExamSchedule>> newColumns=new ArrayList<List<ExamSchedule>>();
//			for(int i=0; i<geoxam.exams.size(); i++)
//				newColumns.add(new ArrayList<ExamSchedule>());
		List<U> newColumns=new ArrayList<>();
		boolean foundNewColumns=false;
		boolean hasNewCuts=false;
		do{
			nrOfIterations++;
			hasNewCuts=false;
			//Solve the master
			logger.info("### MASTER "+master.getIterationCount()+" ################################");
			long time=System.currentTimeMillis();
			master.solve(timeLimit);
			objective=master.getObjective();
			masterSolveTime+=(System.currentTimeMillis()-time);
			logger.debug("Objective master: {}",master.getObjective());
			
			//if the objective of the master problem equals 0, we can stop generating columns as 0 is a lower bound on the optimal solution.
			//Alternatively, we can stop when the objective equals the lowerBound
			if(master.getObjective() < config.PRECISION || Math.abs(objective-lowerBound)<config.PRECISION){
				//Check whether there are cuts. Otherwise potentially an infeasible integer solution (e.g. TSP solution with subtours) might be returned.
				if(config.CUTSENABLED && master.hasNewCuts()){  
					hasNewCuts=true;
					nrOfIterations--;
					logger.debug("Colgen quick return canceled: found valid inequalities. Repeating solve");
					continue;
				}else
					break;
			}else if(Math.ceil(lowerBound) > upperBound){ //lower bound exceeds best feasible integer solution (upper bound) -> terminate
				break;
			}
			
			//Get new columns
			logger.info("### PRICING ################################");
			foundNewColumns=false;
			
			time=System.currentTimeMillis();
			
			//Update data in pricing problems
			for(V pricingProblem : pricingProblems){
//				double[] modifiedCosts=master.getReducedCostVector(pricingProblem);
//				double dualConstant=master.getDualConstant(pricingProblem);
//				pricingProblem.initPricingProblem(modifiedCosts, dualConstant);
				master.initializePricingProblem(pricingProblem);
			}
			
			//Solve pricing problems in the order of the pricing algorithms
			for(int solver=0; solver<solvers.size(); solver++){
				newColumns=pricingProblemManager.solvePricingProblems(solver);
				foundNewColumns=!newColumns.isEmpty();
				
				
				
				//Calculate lower bound when the pricing problems are solved using an exact algorithm
				/*if(solver==solvers.size()-1){
					//Check whether all pricing problems are feasible. If not, no feasible solution exists to the pricing problem, probably caused because of branching decisions. Then we quit.
					boolean pricingProblemIsFeasible=true;
					for(PricingProblem pp : pricingProblems.get(pa)){
						pricingProblemIsFeasible &=pp.pricingProblemIsFeasible();
					}
					if(!pricingProblemIsFeasible){
						System.out.println("PRICING INFEASIBLE. RETURNING CG!");
						this.lowerBound=Double.MAX_VALUE;
						break;
					}
					//If all exact pricing problems are solved to optimality, we can compute a bound.
					if(Math.ceil(this.calculateLowerBound(pa)) > upperBound){ //Lower bound exceeds upper bound
						newColumns.clear(); //Dont bother about any possible columns
						break;
					}
//						System.out.println("Updated lower bound! LB: "+lowerBound+" obj: "+objective);
				}*/
				//Stop when we found new columns
				if(foundNewColumns){
					break;
				}
			}
			
			
			pricingSolveTime+=(System.currentTimeMillis()-time);
			nrGeneratedColumns+=newColumns.size();
			for(U column : newColumns){
				master.addColumn(column);
				column.associatedPricingProblem.addColumn(column);
				logger.debug("Adding columns. Found new columns: {}",foundNewColumns);
			}
			
//			if(config.EXPORT_MODEL) master.exportModel(""+master.getIterationCount());
			
			if(System.currentTimeMillis() >= timeLimit){
				//this.closeMaster();
				this.close();
				throw new TimeLimitExceededException();
			}
			if(logger.isDebugEnabled() && !foundNewColumns) master.printSolution();
			
			//Check for cuts. This can only be done if the master problem hasn't changed (no columns can be added).
			if(config.CUTSENABLED && !foundNewColumns && !thisNodeCanBePruned()){
				time=System.currentTimeMillis();
				hasNewCuts=master.hasNewCuts();
				masterSolveTime+=(System.currentTimeMillis()-time); //Generating cuts is considered part of the master problem
			}
			
		}while(foundNewColumns || hasNewCuts);// || !master.isConclusive());
//		if(config.EXPORT_MODEL) master.exportModel(""+master.getIterationCount());
		nrOfIterations=master.getIterationCount();
		runtime=System.currentTimeMillis()-runtime;
		
		logger.debug("Finished colGen loop");
		logger.debug("Objective: {}",objective);
		logger.debug("Number of iterations: {}",nrOfIterations);
	}
	
	/**
	 * Compute lower bound on the optimal objective value attainable by the the current master problem. The bound is based on both dual variables from the master,
	 * as well as the optimal pricing problem solutions.
	 * The parameter specifies which Pricing Problem should be used to obtain Upper bound information from.
	 * Returns the best lower bound for the currenst master
	 */
	/*private double calculateLowerBound(PricingSolvers pa){
		double newLowerBound=0;
		double[] upperBoundsOnPricing=pricingProblemManager.getBoundsOnPricingProblems(pa); //Calculate all bounds through parallel execution
		
		int index=0;
		for(PricingProblem pp : pricingProblems.get(pa)){
//			double upperBoundOnPricing=pp.getUpperbound();
			double upperBoundOnPricing=upperBoundsOnPricing[index];
			newLowerBound-=upperBoundOnPricing;
			index++;
		}
		newLowerBound += master.getLowerBoundComponent();
		this.lowerBound=Math.max(this.lowerBound, newLowerBound);
		return lowerBound;
	}*/
	
	public double getObjective(){
		return objective;
	}
	public double getLowerBound(){
		return lowerBound;
	}
	public int getNumberOfIterations(){
		return nrOfIterations;
	}
	public long getRuntime(){
		return runtime;
	}
	public long getMasterSolveTime(){
		return masterSolveTime;
	}
	public long getPricingSolveTime(){
		return pricingSolveTime;
	}
	public int getNrGeneratedColumns(){
		return nrGeneratedColumns;
	}
//	public int getNrRestartsMaster(){
//		return master.getNrRestartsMaster();
//	}
	
	public List<U> getSolution(){
		return master.getSolution();
	}
	public List<Inequality> getCuts(){
		return master.getCuts();
	}
	
	/**
	 * Returns true if the lower bound exceeds the upper bound
	 */
	private boolean thisNodeCanBePruned(){
		return Math.ceil(lowerBound) > upperBound;
	}
	
	/**
	 * Destroy the master problem
	 */
	public void closeMaster(){
		master.close();
	}
	
	/**
	 * Destroy both the master problem and pricing problems
	 */
	public void close(){
		master.close();
		pricingProblemManager.close();
	}
}
