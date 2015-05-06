package org.jorlib.frameworks.columnGeneration.branchAndPrice;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecisionListener;
import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;
import org.jorlib.frameworks.columnGeneration.colgenMain.ColGen;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.master.AbstractMaster;
import org.jorlib.frameworks.columnGeneration.master.MasterData;
import org.jorlib.frameworks.columnGeneration.master.MasterFactory;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutHandler;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.Inequality;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblem;
import org.jorlib.frameworks.columnGeneration.pricing.DefaultPricingProblemSolverFactory;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemBundle;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemManager;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemSolver;
import org.jorlib.frameworks.columnGeneration.util.Configuration;
import org.jorlib.frameworks.columnGeneration.util.CplexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBranchAndPrice<T, U extends AbstractColumn<T,U,V>, V extends AbstractPricingProblem<T,U,V>> {
	protected final Logger logger = LoggerFactory.getLogger(AbstractBranchAndPrice.class);
	protected final Configuration config=Configuration.getConfiguration();
	
//	private final BPStatsWriter stats= BPStatsWriter.getStatistics();
	
	protected final T modelData;
	protected final MasterFactory masterFactory;
	protected final List<? extends AbstractBranchCreator<T, U, V>> branchCreators;
	protected List<V> pricingProblems;
	protected List<Class<? extends PricingProblemSolver<T, U, V>>> solvers;
		
	protected final PricingProblemManager pricingProblemManager;
	//Handle to a cutHandler which performs separation
	protected CutHandler<T,? extends MasterData> cutHandler;
	
	//Store the best integer solution.
	protected int bestObjective=Integer.MAX_VALUE;
	protected boolean isOptimal=false; //Indicator whether the solution is optimal
	protected List<U> bestSolution=null;
	
	protected GraphManipulator graphManipulator;
	protected Deque<BAPNode> stack;
	protected static int nodeCounter=0;
	
	protected int upperBoundOnObjective=Integer.MAX_VALUE;
	protected double lowerBoundOnObjective=0;
	protected int nodesProcessed=0; //Counts how many branch-and-price nodes have been processed.
	protected long timeSolvingMaster=0; //Counts how much time is spend on solving master problems
	protected long timeSolvingPricing=0; //Counts how much time is spend on solving pricing problems
	protected int totalGeneratedColumns=0; //Counts how many columns have been generated over the entire branch and price tree
	protected int totalNrIterations=0; // Counts how many column generation iterations have been made.

	//TODO: add artifical solution to nodes to ensure feasibility
	
	public AbstractBranchAndPrice(T modelData,
								  MasterFactory masterFactory,
								  List<V> pricingProblems,
								  List<Class<? extends PricingProblemSolver<T, U, V>>> solvers,
								  List<? extends AbstractBranchCreator<T, U, V>> branchCreators,
								  int upperBoundOnObjective,
								  List<U> initialSolution){
		this.modelData=modelData;
		this.masterFactory=masterFactory;
		this.branchCreators=branchCreators;
		this.pricingProblems=pricingProblems;
		this.solvers=solvers;
		this.upperBoundOnObjective=upperBoundOnObjective;
		stack=new ArrayDeque<BAPNode>();
		
		//Create the root node
		List<Integer> rootPath=new ArrayList<Integer>();
		int nodeID=nodeCounter++;
		rootPath.add(nodeID);
		List<U> rootNodeColumns=new ArrayList<>();
		if(initialSolution != null)
			rootNodeColumns.addAll(initialSolution);
		BAPNode rootNode=new BAPNode(nodeID, rootPath, rootNodeColumns, new ArrayList<Inequality>(), 0, Collections.emptyList());
		stack.add(rootNode);
		graphManipulator=new GraphManipulator(rootNode);
		
		//Initialize pricing algorithms
		List<PricingProblemBundle<T, U, V>> pricingProblemBunddles=new ArrayList<>();
		for(Class<? extends PricingProblemSolver<T, U, V>> solverClass : solvers){
			DefaultPricingProblemSolverFactory<T, U, V> factory=new DefaultPricingProblemSolverFactory<T, U, V>(solverClass, modelData);
			PricingProblemBundle<T, U, V> bunddle=new PricingProblemBundle<>(solverClass, pricingProblems, factory);
			pricingProblemBunddles.add(bunddle);
		}
		
		//Create a pricing problem manager for parallel execution of the pricing problems
		pricingProblemManager=new PricingProblemManager<T,U, V>(pricingProblems, pricingProblemBunddles);
		
		
		
	}
	
	//TODO: do something with the cuts
	public void runBranchAndPrice(long timeLimit){
//		if(config.WRITE_STATS) stats.update(UpdateEnum.START_BAP, new Object[]{geoxam.name});
		
		while(!stack.isEmpty()){
			logger.debug("Processing next node");
			BAPNode bapNode=stack.pop();
			logger.debug("Processing node: {}",bapNode.nodeID);
//			if(config.WRITE_STATS) stats.update(UpdateEnum.SOLVE_BAP_NODE, new Object[]{bapNode.nodeID, bapNode.branchingDecisions.size(), bestObjective});
			
			//Prune this node if the bound exceeds the best found solution (minimization problem). Since all solutions are integral, we may round upwards
			if(Math.ceil(bapNode.bound) >= bestObjective){
				logger.debug("Pruning node. Bound: {}, best incumbent: {}", bapNode.bound, bestObjective);
//				if(config.WRITE_STATS) stats.update(UpdateEnum.BAP_NODE_PRUNE, new Object[]{bapNode.bound});
				continue;
			}
			
			//Solve the next node.
			graphManipulator.next(bapNode); //Prepare data structures for the next node
			//Generate artificial solution for this node to guarantee a solution.
			if(bapNode.nodeID != 0){
				bapNode.columns.addAll(this.generateArtificialSolution());
			}
			
//			ColGen(T dataModel, 
//					AbstractMaster<T,V,U, ? extends MasterData> master, 
//					List<V> pricingProblems,
//					List<Class<? extends PricingProblemSolver<T, U, V>>> solvers,
//					PricingProblemManager<T,U, V> pricingProblemManager,
//					List<U> initSolution,
//					int upperBound)
			
			//Create a new master problem 
			AbstractMaster<T, V, U, ? extends MasterData> master=masterFactory.createMaster(modelData, cutHandler);
			ColGen cg=null;
			try {
				cg = new ColGen(modelData, master, pricingProblems, solvers, pricingProblemManager, bapNode.columns, bestObjective); //Solve the node
				cg.solve(timeLimit);
				//Update statistics
				timeSolvingMaster+=cg.getMasterSolveTime();
				timeSolvingPricing+=cg.getPricingSolveTime();
				totalNrIterations+=cg.getNumberOfIterations();
				totalGeneratedColumns+=cg.getNrGeneratedColumns();
//				if(config.WRITE_STATS) stats.update(UpdateEnum.SOLVE_STATS, new Object[]{cg.getNumberOfIterations(), cg.getMasterSolveTime(), cg.getPricingSolveTime(), cg.getNrGeneratedColumns()});
			} catch (TimeLimitExceededException e) {
				stack.push(bapNode);
				break;
			}
			
			bapNode.bound=cg.getLowerBound(); //When node is solved to optimality, lowerBound equals the optimal solution of the column generation procedure
			
			//Check whether the node's bound exceeds the best integer solution, if so we can skip this node (no branching required)
			//NOTE: based on this, we may no longer need to check whether there are artificial columns in the solution.
			if(bapNode.bound >= bestObjective){ //Do not bother to create a branch even though the node is fractional. Bound is worse than best solution
				logger.debug("Lower bound ({}) of node is worse than best solution ({}). Do not branch.",bapNode.bound, bestObjective);
//				if(config.WRITE_STATS) stats.update(UpdateEnum.BAP_NODE_PRUNE, new Object[]{bapNode.bound});
				nodesProcessed++;
				cg.closeMaster();
				continue;
			}
			
			//Query the solution
			List<U> solution= cg.getSolution();
			
			//Check if node was infeasible, i.e. whether there are artifical columns in the solution. If so, ignore it and continue with the next node.
			boolean hasArtificalColsInSol=false;
			for(U column : solution){
				hasArtificalColsInSol |=column.isArtificialColumn;
				if(hasArtificalColsInSol) break;
			}
			if(hasArtificalColsInSol){
				logger.debug("Solution is artificial: Node is infeasible");
//				if(config.WRITE_STATS) stats.update(UpdateEnum.BAP_NODE_ISINFEAS, null);
				nodesProcessed++;
				cg.closeMaster();
				continue;
			}
			
			//Check if solution is integral (in an integral solution, each exam should only have 1 schedule);
			boolean isIntegral=this.isIntegralSolution(solution);
			
			//If solution is integral, check whether it is better than the current best solution
			if(isIntegral){
				logger.debug("Integer solution found with obj: {}", cg.getObjective());
//				if(config.WRITE_STATS) stats.update(UpdateEnum.BAP_NODE_ISINTEGER, new Object[]{cg.getObjective(), bapNode.bound});
				if(cg.getObjective() < this.bestObjective){
					logger.debug("Solution is new best");
					this.bestObjective=CplexUtil.doubleToInt(cg.getObjective());
					this.bestSolution=solution;
				}
			}else{ //We need to branch
				logger.debug("Attempting to branch");
//				if(config.WRITE_STATS) stats.update(UpdateEnum.BAP_NODE_ISNONINTEGER, new Object[]{cg.getObjective(), bapNode.bound});
				List<Inequality> cuts=cg.getCuts();
				List<BAPNode<T, U>> newBranches=null;
				for(AbstractBranchCreator<T, U, V> bc : branchCreators){
					newBranches=bc.branch(bapNode, solution, cuts);
					if(!newBranches.isEmpty()) break;
				}
				
				if(newBranches.isEmpty())
					throw new RuntimeException("BAP encountered fractional solution, but non of the BranchCreators produced any new branches?");
				else
					stack.addAll(newBranches);
			}
			
			//Close the master problem: it has been finished
			cg.closeMaster();
			nodesProcessed++;
		}
		
		//Restore the graph to its original form. Not really needed.
		//graphManipulator.restore();
		//Close the pricing problems
		pricingProblemManager.close();
		if(cutHandler != null) cutHandler.close();
		
		//Update statistics
		if(stack.isEmpty()){
			if(this.isFeasible())
				lowerBoundOnObjective=this.bestObjective;
			this.isOptimal=true;
		}else{
			this.isOptimal=false;
			lowerBoundOnObjective=stack.peek().bound;
			for(BAPNode bapNode : stack){
				lowerBoundOnObjective=Math.min(lowerBoundOnObjective, bapNode.bound);
			}
		}
//		if(config.WRITE_STATS) stats.update(UpdateEnum.FINISH_BAP, null);
	}
	
	
	/**
	 * Returns the objective of the best solution found
	 */
	public int getObjective(){
		return this.bestObjective;
	}
	
	/**
	 * Return whether a solution has been found
	 * @return
	 */
	public boolean isFeasible(){
		return bestSolution != null;
	}
	
	/**
	 * Returns whether the solution is optimal, i.e. the entire tree branch-and-price tree has been processed
	 */
	public boolean isOptimal(){
		return isOptimal;
	}
	
	/**
	 * Returns lower bound on the optimal solution
	 */
	public double getBound(){
		return this.lowerBoundOnObjective;
	}
	
	/**
	 * Returns the number of processed nodes
	 */
	public int getNumberOfProcessedNodes(){
		return nodesProcessed;
	}
	
	/**
	 * Total time spend on solving master problems
	 */
	public long getMasterSolveTime(){
		return timeSolvingMaster;
	}
	/**
	 * Total time spend on solving pricing problems
	 */
	public long getPricingSolveTime(){
		return timeSolvingPricing;
	}
	/**
	 * Counts how many columns have been generated over the entire branch and price tree
	 */
	public int getTotalGeneratedColumns(){
		return totalGeneratedColumns;
	}
	/**
	 * Counts how many column generation iterations have been made over the entire branch and price tree
	 **/
	public int getTotalNrIterations(){
		return totalNrIterations;
	}
	/**
	 * Returns the best solution found (converts the columns to a solution object)
	 */
	public List<U> getSolution(){
		return bestSolution;
	}
	
	/**
	 * Create an artificial solution which satisfies the node's master problem and hence constitutes a feasible initial solution.
	 * The columns are not necessary feasible or meet the definition of a column; it is undesirable that these columns end up in a final solution.
	 * To prevent them from ending up in a final solution, a high cost is associated with them.  
	 */
	protected abstract List<U> generateArtificialSolution();
	
	protected abstract boolean isIntegralSolution(List<U> solution);

	public void addBranchingDecisionListener(BranchingDecisionListener listener){
		graphManipulator.addBranchingDecisionListener(listener);
	}
	public void removeBranchingDecisionListener(BranchingDecisionListener listener){
		graphManipulator.removeBranchingDecisionListener(listener);
	}
}
