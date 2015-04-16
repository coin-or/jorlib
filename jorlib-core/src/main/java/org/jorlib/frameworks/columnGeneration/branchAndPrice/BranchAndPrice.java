package org.jorlib.frameworks.columnGeneration.branchAndPrice;

import ilog.concert.IloException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;
import org.jorlib.frameworks.columnGeneration.colgenMain.ColGen;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutHandler;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.Inequality;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblem;
import org.jorlib.frameworks.columnGeneration.util.Configuration;
import org.jorlib.frameworks.columnGeneration.util.CplexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class BranchAndPrice<T, U extends AbstractColumn<T,U,V>, V extends AbstractPricingProblem<T,U,V>> {
	protected final Logger logger = LoggerFactory.getLogger(BranchAndPrice.class);
	protected final Configuration config=Configuration.getConfiguration();
	
//	private final BPStatsWriter stats= BPStatsWriter.getStatistics();
	
	protected final T modelData;
//	private final PricingSolvers pricingAlgorithms[];
	//Define the pricing problems
	protected final List<V> pricingProblems;
		
	protected final EnumMap<PricingSolvers, List<PricingProblem>> pricingProblems;
	protected final PricingProblemManager pricingProblemManager;
//	private final CutHandler cutHandler;
	//Handle to a cutHandler which performs separation
	protected CutHandler<T,W> cutHandler;
	
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
	protected int totalNrRestarts=0; //Counts how many times a master has been restarted (i.e. increased its penalty functions)
	protected int totalNrIterations=0; // Counts how many column generation iterations have been made.
	protected boolean presolveInfeas=false; //Flag indicating whether the presolver determined that the instance is infeasible,.
	
	public BranchAndPrice(T modelData){
		this.modelData=modelData;
		stack=new ArrayDeque<BAPNode>();
		
		//Create the root node
		List<Integer> rootPath=new ArrayList<Integer>();
		int nodeID=nodeCounter++;
		rootPath.add(nodeID);
		List<U> rootNodeColumns=new ArrayList<>();
		rootNodeColumns.addAll(this.generateArtificialSolution());
		
		
//		if(initSolution != null){ //Start from real solution
//			//Set initial solution as best incumbent solution
//			bestObjective=(int)initSolution.objective;
//			bestSolution=this.convertInitSolToColumns(initSolution);
//			upperBoundOnObjective=bestObjective;
//			for(int i=0; i<geoxam.exams.size(); i++){
//				rootNodeColumns.get(i).add(bestSolution.get(i).get(0));
//			}
//			
//		}else{ //Create artificial solution
//			//Compute weak upper bound
//			upperBoundOnObjective=this.calcUpperBoundOnObj();
//			//Set artificial solution as best incumbent solution
//			bestObjective=upperBoundOnObjective;
//			//For each exam, generate an artificial column
//			for(Exam e : geoxam.exams){
//				Column es=this.generateArtificialColumn(e);
//				rootNodeColumns.get(e.ID).add(es);
//			}
//		}
//		logger.debug("Best objective: {}",bestObjective);
		
		//Initialize pricing algorithms
		pricingAlgorithms=Configuration.pricingAlgorithms;
		//Generate the pricing problems
		pricingProblems=new EnumMap<PricingSolvers, List<PricingProblem>>(PricingSolvers.class);
		for(PricingSolvers pa : pricingAlgorithms){
			List<PricingProblem> pricingProblemGroup=new ArrayList<PricingProblem>(geoxam.exams.size());
			for(Exam e: geoxam.exams){
				pricingProblemGroup.add(PricingProblemSolverFactory.buildPricingProblem(geoxam, e, pa));
			}
			pricingProblems.put(pa, pricingProblemGroup);
		}
		//Create the pricingProblem manager
		pricingProblemManager=new PricingProblemManager(pricingProblems, geoxam.exams.size());
		//Create the cutHandler when cuts are enabled
		if(config.CUTSENABLED){
			cutHandler=new CutHandler(geoxam);
//			cutHandler.addCutGenerator(new CoverInequalityGenerator2(geoxam));
			cutHandler.addCutGenerator(new LiftedCoverInequalityGenerator(geoxam));
//			cutHandler.addCutGenerator(new CoverInequalityGenerator(geoxam));
		}else{
			cutHandler=null;
		}
		
		//Create root node
		BAPNode rootNode=new BAPNode(nodeID, rootPath, rootNodeColumns, new ArrayList<Inequality>(), 0);
		if(!presolveInfeas)
			stack.add(rootNode);
		graphManipulator=new GraphManipulator(rootNode);
	}
	
	
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
			
			ColGen cg=null;
			try {
				cg = new ColGen(geoxam, cutHandler, pricingAlgorithms, pricingProblems, pricingProblemManager, bapNode.columns, bapNode.inequalities, timeLimit, bestObjective); //Solve the node
				//Update statistics
				timeSolvingMaster+=cg.getMasterSolveTime();
				timeSolvingPricing+=cg.getPricingSolveTime();
				totalNrIterations+=cg.getNumberOfIterations();
				totalGeneratedColumns+=cg.getNrGeneratedColumns();
				totalNrRestarts+=0;//cg.getNrRestartsMaster();
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
				if(config.WRITE_STATS) stats.update(UpdateEnum.BAP_NODE_ISNONINTEGER, new Object[]{cg.getObjective(), bapNode.bound});
				List<Inequality> cuts=cg.getCuts();
				boolean branch= branchOnLocation(bapNode, solution, cuts); //First, try to branch on a location
				if(!branch)
					branch= branchOnRoomAssignment(bapNode, solution, cuts); //Second, try to branch on a room
				if(!branch){	//Third, throw an exception if the previous branching attempts didn't work
					for(Exam e : geoxam.exams){
						System.out.println(e);
						for(Column es : solution.get(e.ID)){
							System.out.println(es);
						}
					}
					throw new RuntimeException("Failed to branch, but solution is still fractional!");
				}
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
	
	
	private boolean branchOnRoomAssignment(BAPNode parentNode, List<U> solution, List<Inequality> inequalities){
		//1. Find the 'most fractional' room assignment for an exam. We will branch on this room: the room may be used for the exam, or it may nog be used. 
		Exam examForBranching=null;
		Room roomForBranching=null;
		double fractionalRoomValue=0;
		for(Exam e: geoxam.exams){
			double[] roomValues=new double[geoxam.rooms.size()];
			//Aggregate room values
			for(Column es : solution.get(e.ID)){
				for(Room r : es.roomsUsed)
					roomValues[r.ID]+=es.value;
			}
			//Check whether any of the values is fractional. Values closer to 0.5 are preferred
			for(int i=0; i<geoxam.rooms.size(); i++){
				if(Math.abs(0.5-roomValues[i]) < Math.abs(0.5-fractionalRoomValue)){
					examForBranching=e;
					roomForBranching=geoxam.rooms.get(i);
					fractionalRoomValue=roomValues[i];
				}
			}
		}
		
		if(fractionalRoomValue < Configuration.EPSILON || (1.0-fractionalRoomValue) < Configuration.EPSILON){ //Could not find a Exam/Room pair to branch on.
			logger.debug("Branching on Exam/Room pair failed");
			return false;
		}//else: we found an edge. create two branches.
		Object[] o={examForBranching.ID, roomForBranching.ID, fractionalRoomValue};
		logger.debug("Branching on Exam: {}, room: {}, value: {}",o);
		
		//2. Branch on Exam/Room pair. This involves creating two BAP nodes
		
		//2a. Branch 1: enforce that the exam uses that particular room
		BranchingDecision bd1=new FixRoom(pricingProblems, examForBranching, roomForBranching);
		int nodeID1=nodeCounter++;
		List<Integer> rootPath1=new ArrayList<Integer>(parentNode.rootPath);
		rootPath1.add(nodeID1);
		List<List<Column>> initSolution1=new ArrayList<List<Column>>();
		for(Exam e: geoxam.exams){
			List<Column> schedulesForExam=new ArrayList<Column>();
			if(e!=examForBranching){ //Copy all schedules, except the artificial ones.
				for(Column es: solution.get(e.ID)){
					if(!es.isArtificialColumn){
						schedulesForExam.add(es);
					}
				}
			}else{ //only copy schedules containing roomForBranching
				for(Column es: solution.get(e.ID)){
					if(es.roomsUsed.contains(roomForBranching) && !es.isArtificialColumn){
						schedulesForExam.add(es);
					}
				}
			}
			initSolution1.add(schedulesForExam);
		}
		//Copy inequalities from parent node
		List<Inequality> inequalities1=new ArrayList<Inequality>(inequalities); //All inequalities from the parent are valid in this node
		BAPNode node1=new BAPNode(nodeID1, rootPath1, initSolution1, inequalities1, parentNode.bound);
		node1.branchingDecisions.addAll(parentNode.branchingDecisions);
		node1.branchingDecisions.add(bd1);
		
		//2b. Branch 2: room removed
		BranchingDecision bd2=new RemoveRoom(pricingProblems, examForBranching, roomForBranching);
		int nodeID2=nodeCounter++;
		List<Integer> rootPath2=new ArrayList<Integer>(parentNode.rootPath);
		rootPath2.add(nodeID2);
		//Copy columns from parent node
		List<List<Column>> initSolution2=new ArrayList<List<Column>>();
		for(Exam e: geoxam.exams){
			List<Column> schedulesForExam=new ArrayList<Column>();
			if(e!=examForBranching){ //Copy schedules except artificial ones
				for(Column es: solution.get(e.ID)){
					if(!es.isArtificialColumn){
						schedulesForExam.add(es);
					}
				}
			}else{ //Copy matchings which do not contain roomForBranching. Also artificial columns are removed
				for(Column es: solution.get(e.ID)){
					if(!es.roomsUsed.contains(roomForBranching)  && !es.isArtificialColumn){
						schedulesForExam.add(es);
					}
				}
			}
			initSolution2.add(schedulesForExam);
		}
		//Copy inequalities from parent node
		List<Inequality> inequalities2=new ArrayList<Inequality>();
		for(Inequality inequality : inequalities){
			switch (inequality.type) {
			case COVERINEQUALITY:
				CoverInequality coverInequality=(CoverInequality)inequality;
				if(coverInequality.room != roomForBranching)
					inequalities2.add(inequality);
				break;
			case LIFTEDCOVERINEQUALITY:
				LiftedCoverInequality liftedCoverInequality=(LiftedCoverInequality)inequality;
				if(liftedCoverInequality.room != roomForBranching)
					inequalities2.add(inequality);
				break;
			default:
				break;
			}
		}
		//Create child node 2
		BAPNode node2=new BAPNode(nodeID2, rootPath2, initSolution2, inequalities2, parentNode.bound);
		node2.branchingDecisions.addAll(parentNode.branchingDecisions);
		node2.branchingDecisions.add(bd2);
		
		//Add both nodes to the stack
		//1. BFS;
//		stack.add(node1);
//		stack.add(node2);
		//2. DFS (put the most constraint node, node 1 on top. This node will be explorered first):
		stack.push(node2);
		stack.push(node1);
		
		logger.debug("Finished branching. Stack size: {}",stack.size());
		
		return true;
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
	 * Counts how many times a master has been restarted (i.e. increased its penalty functions)
	 **/
	public int getTotalNrRestarts(){ 
		return totalNrRestarts;
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
	
}
