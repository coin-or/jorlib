package geoxam.algorithms.exact.columnGeneration.branchAndPrice;

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

import geoxam.algorithms.exact.columnGeneration.ExamSchedule;
import geoxam.algorithms.exact.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import geoxam.algorithms.exact.columnGeneration.branchAndPrice.branchingDecisions.FixLocation;
import geoxam.algorithms.exact.columnGeneration.branchAndPrice.branchingDecisions.FixRoom;
import geoxam.algorithms.exact.columnGeneration.branchAndPrice.branchingDecisions.RemoveLocation;
import geoxam.algorithms.exact.columnGeneration.branchAndPrice.branchingDecisions.RemoveRoom;
import geoxam.algorithms.exact.columnGeneration.colgenMain.ColGen;
import geoxam.algorithms.exact.columnGeneration.master.cutGeneration.CoverInequalityGenerator;
import geoxam.algorithms.exact.columnGeneration.master.cutGeneration.CoverInequalityGenerator2;
import geoxam.algorithms.exact.columnGeneration.master.cutGeneration.CutHandler;
import geoxam.algorithms.exact.columnGeneration.master.cutGeneration.LiftedCoverInequalityGenerator;
import geoxam.algorithms.exact.columnGeneration.master.cuts.CoverInequality;
import geoxam.algorithms.exact.columnGeneration.master.cuts.LiftedCoverInequality;
import geoxam.algorithms.exact.columnGeneration.pricing.PricingAlgorithm;
import geoxam.algorithms.exact.columnGeneration.pricing.PricingProblem;
import geoxam.algorithms.exact.columnGeneration.pricing.PricingProblemFactory;
import geoxam.algorithms.exact.columnGeneration.pricing.PricingProblemManager;
import geoxam.algorithms.exact.columnGeneration.pricing.MIP.MipPricing;
import geoxam.io.BPStatsWriter;
import geoxam.io.TimeLimitExceededException;
import geoxam.io.UpdateEnum;
import geoxam.model.Exam;
import geoxam.model.Participant;
import geoxam.model.Problem;
import geoxam.model.Room;
import geoxam.model.ScheduledExam;
import geoxam.model.Site;
import geoxam.model.Solution;
import geoxam.tools.Constants;
import geoxam.tools.CplexUtil;

import org.jorlib.frameworks.columnGeneration.master.cuts.Inequality;
import org.jorlib.frameworks.columnGeneration.master.cuts.InequalityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BranchAndPrice {
	private final Logger logger = LoggerFactory.getLogger(BranchAndPrice.class);
	private final BPStatsWriter stats= BPStatsWriter.getStatistics();
	
	private final Problem geoxam;
	private final PricingSolvers pricingAlgorithms[];
	private final EnumMap<PricingSolvers, List<PricingProblem>> pricingProblems;
	private final PricingProblemManager pricingProblemManager;
	private final CutHandler cutHandler;
	
	//Store the best integer solution, and the node where this solution is discovered.
	private int bestObjective=Integer.MAX_VALUE;
	private boolean isOptimal=false; //Indicator whether the solution is optimal
	private List<List<Column>> bestSolution=null;
	
	private GraphManipulator graphManipulator;
	private Deque<BAPNode> stack;
	private int nodeCounter=0;
	
	private int upperBoundOnObjective=Integer.MAX_VALUE;
	private double lowerBoundOnObjective=0;
	private int nodesProcessed=0; //Counts how many branch-and-price nodes have been processed.
	private long timeSolvingMaster=0; //Counts how much time is spend on solving master problems
	private long timeSolvingPricing=0; //Counts how much time is spend on solving pricing problems
	private int totalGeneratedColumns=0; //Counts how many columns have been generated over the entire branch and price tree
	private int totalNrRestarts=0; //Counts how many times a master has been restarted (i.e. increased its penalty functions)
	private int totalNrIterations=0; // Counts how many column generation iterations have been made.
	private boolean presolveInfeas=false; //Flag indicating whether the presolver determined that the instance is infeasible,.
	
	public BranchAndPrice(Problem geoxam, Solution initSolution){
		this.geoxam=geoxam;
		stack=new ArrayDeque<BAPNode>();
		
		//Create the first root node
		List<Integer> rootPath=new ArrayList<Integer>();
		int nodeID=nodeCounter++;
		rootPath.add(nodeID);
		List<List<Column>> columns=new ArrayList<List<Column>>();
		for(Exam e: geoxam.exams)
			columns.add(new ArrayList<Column>());
		
		if(initSolution != null){ //Start from real solution
			//Set initial solution as best incumbent solution
			bestObjective=(int)initSolution.objective;
			bestSolution=this.convertInitSolToColumns(initSolution);
			upperBoundOnObjective=bestObjective;
			for(int i=0; i<geoxam.exams.size(); i++){
				columns.get(i).add(bestSolution.get(i).get(0));
			}
			
		}else{ //Create artificial solution
			//Compute weak upper bound
			upperBoundOnObjective=this.calcUpperBoundOnObj();
			//Set artificial solution as best incumbent solution
			bestObjective=upperBoundOnObjective;
			//For each exam, generate an artificial column
			for(Exam e : geoxam.exams){
				Column es=this.generateArtificialColumn(e);
				columns.get(e.ID).add(es);
			}
		}
		logger.debug("Best objective: {}",bestObjective);
		
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
		if(Configuration.CUTSENABLED){
			cutHandler=new CutHandler(geoxam);
//			cutHandler.addCutGenerator(new CoverInequalityGenerator2(geoxam));
			cutHandler.addCutGenerator(new LiftedCoverInequalityGenerator(geoxam));
//			cutHandler.addCutGenerator(new CoverInequalityGenerator(geoxam));
		}else{
			cutHandler=null;
		}
		
		//Create root node
		BAPNode rootNode=new BAPNode(nodeID, rootPath, columns, new ArrayList<Inequality>(), 0);
		if(!presolveInfeas)
			stack.add(rootNode);
		graphManipulator=new GraphManipulator(rootNode);
	}
	
	
	public void runBranchAndPrice(long timeLimit){
		if(Configuration.WRITE_STATS) stats.update(UpdateEnum.START_BAP, new Object[]{geoxam.name});
		
		
//		pricingProblems=new PricingProblem[pricingAlgorithms.length][geoxam.exams.size()];
//		for(int i=0; i<pricingAlgorithms.length; i++){
//			for(Exam e: geoxam.exams){
//				pricingProblems[i][e.ID]=PricingProblemFactory.buildPricingProblem(geoxam, e, pricingAlgorithms[i]);
//			}
//		}
		
		
		
		while(!stack.isEmpty()){
			System.out.println("NEXT NODE");
			BAPNode bapNode=stack.pop();
			logger.debug("Processing node: {}",bapNode.nodeID);
			if(Configuration.WRITE_STATS) stats.update(UpdateEnum.SOLVE_BAP_NODE, new Object[]{bapNode.nodeID, bapNode.branchingDecisions.size(), bestObjective});
			
			//Prune this node if the bound exceeds the best found solution (minimization problem). Since all solutions are integral, we may round upwards
			if(Math.ceil(bapNode.bound) >= bestObjective){
				logger.debug("Pruning node. Bound: {}, best incumbent: {}", bapNode.bound, bestObjective);
				if(Configuration.WRITE_STATS) stats.update(UpdateEnum.BAP_NODE_PRUNE, new Object[]{bapNode.bound});
				continue;
			}
			
			//Solve the next node.
			graphManipulator.next(bapNode); //Prepare data structures for the next node
			//Generate artificial solution for this node to guarantee a solution.
			if(bapNode.nodeID != 0){
				for(Exam e : geoxam.exams){
					Column es=this.generateArtificialColumn(e);
					bapNode.columns.get(e.ID).add(es);
				}
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
				if(Configuration.WRITE_STATS) stats.update(UpdateEnum.SOLVE_STATS, new Object[]{cg.getNumberOfIterations(), cg.getMasterSolveTime(), cg.getPricingSolveTime(), cg.getNrGeneratedColumns()});
			} catch (TimeLimitExceededException e) {
				stack.push(bapNode);
				break;
			}
			
			bapNode.bound=cg.getLowerBound(); //When node is solved to optimality, lowerBound equals the optimal solution of the column generation procedure
			
			//Check whether the node's bound exceeds the best integer solution, if so we can skip this node (no branching required)
			//NOTE: based on this, we may no longer need to check whether there are artificial columns in the solution.
			if(bapNode.bound >= bestObjective){ //Do not bother to create a branch even though the node is fractional. Bound is worse than best solution
				logger.debug("Lower bound ({}) of node is worse than best solution ({}). Do not branch.",bapNode.bound, bestObjective);
				if(Configuration.WRITE_STATS) stats.update(UpdateEnum.BAP_NODE_PRUNE, new Object[]{bapNode.bound});
				nodesProcessed++;
				cg.closeMaster();
				continue;
			}
			
			//Query the solution
			List<List<Column>> solution= cg.getSolution();
			
			//Check if node was infeasible, i.e. whether there are artifical columns in the solution. If so, ignore it and continue with the next node.
			boolean hasArtificalColsInSol=false;
			for(Exam e: geoxam.exams){
				for(Column es : solution.get(e.ID)){
					hasArtificalColsInSol |=es.isArtificialColumn;
					if(hasArtificalColsInSol) break;
				}
				if(hasArtificalColsInSol) break;
			}
			if(hasArtificalColsInSol){
				logger.debug("Solution is artificial: Node is infeasible");
//				System.out.println("Solution is artificial: Node is infeasible nodeID: "+bapNode.nodeID);
				if(Configuration.WRITE_STATS) stats.update(UpdateEnum.BAP_NODE_ISINFEAS, null);
				nodesProcessed++;
				cg.closeMaster();
				continue;
			}
			
			//Check if solution is integral (in an integral solution, each exam should only have 1 schedule);
			boolean isIntegral=true;
			for(int i=0; i<geoxam.exams.size() && isIntegral; i++){
				isIntegral &=solution.get(i).size()==1;
			}
			if(logger.isDebugEnabled() && isIntegral){
				logger.debug("Found integral solution");
			}
			
			//If solution is integral, check whether it is the best incumbent solution
			if(isIntegral){
				logger.debug("Integer solution found with obj: {}", cg.getObjective());
				if(Configuration.WRITE_STATS) stats.update(UpdateEnum.BAP_NODE_ISINTEGER, new Object[]{cg.getObjective(), bapNode.bound});
				if(cg.getObjective() < this.bestObjective){
					logger.debug("Solution is new best");
					this.bestObjective=CplexUtil.doubleToInt(cg.getObjective());
					this.bestSolution=solution;
				}
			}else{ //We need to branch
				logger.debug("Attempting to branch");
				if(Configuration.WRITE_STATS) stats.update(UpdateEnum.BAP_NODE_ISNONINTEGER, new Object[]{cg.getObjective(), bapNode.bound});
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
		if(Configuration.WRITE_STATS) stats.update(UpdateEnum.FINISH_BAP, null);
	}
	
	
	private boolean branchOnRoomAssignment(BAPNode parentNode, List<List<Column>> solution, List<Inequality> inequalities){
		//TEMP
//		for(Exam e: geoxam.exams){
//			double[] roomValues=new double[geoxam.rooms.size()];
//			for(ExamSchedule es : solution.get(e.ID)){
//				for(Room r : es.roomsUsed)
//					roomValues[r.ID]+=es.value;
//			}
//			
//			double[] siteValues=new double[geoxam.sites.size()];
//			for(int i=0; i<geoxam.sites.size(); i++){
//				Site s=geoxam.sites.get(i);
//				for(Room r: s.rooms){
//					siteValues[i]+=roomValues[r.ID];
//				}
//			}
//			double sumSiteValues=0;
//			for(double d : siteValues)
//				sumSiteValues+=d;
//			System.out.println("Exam "+e.ID+" sumSiteValues: "+sumSiteValues+"<"+geoxam.k);
//			System.out.println("Roomvalues: "+Arrays.toString(roomValues));
//			System.out.println("Sitevalues: "+Arrays.toString(siteValues));
//		}
		
		//END TEMP
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
	
	
	
	private boolean branchOnLocation(BAPNode parentNode, List<List<Column>> solution, List<Inequality> inequalities){
		//TEMP
//		for(Exam e: geoxam.exams){
//			double[] roomValues=new double[geoxam.rooms.size()];
//			for(ExamSchedule es : solution.get(e.ID)){
//				for(Room r : es.roomsUsed)
//					roomValues[r.ID]+=es.value;
//			}
//			
//			double[] siteValues=new double[geoxam.sites.size()];
//			for(int i=0; i<geoxam.sites.size(); i++){
//				Site s=geoxam.sites.get(i);
//				for(Room r: s.rooms){
//					siteValues[i]+=roomValues[r.ID];
//				}
//			}
//			double sumSiteValues=0;
//			for(double d : siteValues)
//				sumSiteValues+=d;
//			System.out.println("Exam "+e.ID+" sumSiteValues: "+sumSiteValues+"<"+geoxam.k);
//			System.out.println("Roomvalues: "+Arrays.toString(roomValues));
//			System.out.println("Sitevalues: "+Arrays.toString(siteValues));
//		}
		
		//END TEMP
		
		//Cannot use this type of branching when somewhere heigher up the tree there is a branch on a single room. This has to do with the way the branching is implemented
		if(!parentNode.branchingDecisions.isEmpty()){
			BranchingDecision bd=parentNode.branchingDecisions.get(parentNode.branchingDecisions.size()-1);
			if(bd instanceof FixRoom || bd instanceof RemoveRoom)
				return false;
		}
		
		//1. Find the 'most fractional' location for an exam. We will branch on this location: the location may be used for an exam, or it may not be used. 
		Exam examForBranching=null;
		Site locationForBranching=null;
		double fractionalLocationValue=0;
		for(Exam e: geoxam.exams){
			
			double[] siteValues=new double[geoxam.sites.size()];
			for(Column es : solution.get(e.ID)){
				for(Room r : es.roomsUsed)
					siteValues[r.site.ID]+=es.value;
			}
			
			//Check whether any of the values is fractional. Values closer to 0.5 are preferred
			for(int i=0; i<geoxam.sites.size(); i++){
				if(Math.abs(0.5-siteValues[i]) < Math.abs(0.5-fractionalLocationValue)){
					examForBranching=e;
					locationForBranching=geoxam.sites.get(i);
					fractionalLocationValue=siteValues[i];
				}
			}
		}
		
		if(fractionalLocationValue < Configuration.EPSILON || (1.0-fractionalLocationValue) < Configuration.EPSILON){ //Could not find a Exam/Location pair to branch on.
			logger.debug("Branching on Exam/Location pair failed");
			return false;
		}//else: we found an edge. create two branches.
		Object[] o={examForBranching.ID, locationForBranching.ID, fractionalLocationValue};
		logger.debug("Branching on Exam: {}, location: {}, value: {}",o);
		
		//2. Branch on Exam/Location pair. This involves creating two BAP nodes
		
		//2a. Branch 1: enforce that the exam uses the location
		BranchingDecision bd1=new FixLocation(pricingProblems, examForBranching, locationForBranching);
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
			}else{ //only copy schedules which use the selected location
				for(Column es: solution.get(e.ID)){
					if(es.isArtificialColumn)
						continue;
					for(Room r: es.roomsUsed){
						if(r.site==locationForBranching){
							schedulesForExam.add(es);
							break;
						}
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
		
		//2b. Branch 2: location removed
		BranchingDecision bd2=new RemoveLocation(pricingProblems, examForBranching, locationForBranching);
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
			}else{ //Copy matchings which do NOT contain rooms at the selected location. Also artificial columns are removed
				for(Column es: solution.get(e.ID)){
					if(es.isArtificialColumn)
						continue;
					boolean columnHasRoomAtLocation=false;
					for(Room r : es.roomsUsed){
						columnHasRoomAtLocation |=r.site==locationForBranching;
					}
					if(!columnHasRoomAtLocation){
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
				if(coverInequality.room.site != locationForBranching)
					inequalities2.add(inequality);
				break;
			case LIFTEDCOVERINEQUALITY:
				LiftedCoverInequality liftedCoverInequality=(LiftedCoverInequality)inequality;
				if(liftedCoverInequality.room.site != locationForBranching)
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
	 * Converts solution object to a set of columns
	 */
	private List<List<Column>> convertInitSolToColumns(Solution initSolution){
		logger.debug("Setting initial solution");
		List<List<Column>> columns=new ArrayList<List<Column>>(geoxam.exams.size());

		for(Exam e: geoxam.exams){
			Column es=new Column(e, "initSol");
			columns.add(Arrays.asList(es));
		}
		
		//Convert the solution in the series of ExamSchedules
		for(ScheduledExam se : initSolution.scheduledExams){
			Column es=columns.get(se.exam.ID).get(0);
			//Add room
			Room r=se.assignedRoom;
			Site s=r.site;
			es.roomsUsed.add(r); //Room
			es.roomVariableCost+=se.assignedRoom.variableCost*se.exam.durationInMinutes*geoxam.w_cost; //Variable room cost
			//Add participants
			for(Participant p : se.participants){
				es.participantAssignment.put(p,s);
				es.participantAssignmentCost+=geoxam.getDistance(p, s)*geoxam.w_dist;
			}
		}

		return columns;
	}
	
	/**
	 * Returns the best solution found (converts the columns to a solution object)
	 */
	public Solution getSolution(){
		if(bestSolution == null) //Graph is infeasible
			return null;
		
		boolean[] roomsUsed=new boolean[geoxam.rooms.size()];
		
		Solution sol =new Solution(geoxam);
		sol.objective=bestObjective;
		for(Exam e : geoxam.exams){
			Column es = bestSolution.get(e.ID).get(0);
			
			//Update objective values
			sol.participantAssignmentCost+=es.participantAssignmentCost;
			sol.roomVariableCost+=es.roomVariableCost;
			
			Map<Site, ScheduledExam> siteToScheduledExamMap=new HashMap<Site, ScheduledExam>();
			for(Room r : es.roomsUsed){
				ScheduledExam se=new ScheduledExam(e, r);
				sol.addScheduledExam(se);
				siteToScheduledExamMap.put(r.site,se);
				roomsUsed[r.ID]=true;
			}
			for(Participant p : e.participants){
				Site s=es.participantAssignment.get(p);
				ScheduledExam se=siteToScheduledExamMap.get(s);
				se.addParticipant(p);
			}
		}
		for(int i=0; i<geoxam.rooms.size(); i++){
			if(roomsUsed[i]){
				sol.roomFixedCost+=geoxam.w_cost*geoxam.rooms.get(i).fixedCost;
			}
		}
		return sol;
	}
	
	/**
	 * Create an artificial column with a high price. Due to the high cost of the column, they won't up in an optimal solution.
	 */
	private Column generateArtificialColumn(Exam e){
		Column es=new Column(e, "artificial");
		//int cost=(int)Math.ceil((1.0*upperBoundOnObjective)/geoxam.exams.size());
		int cost=upperBoundOnObjective;
		es.participantAssignmentCost=cost;
		es.isArtificialColumn=true;
		return es;
	}
	
	/**
	 * Calculates a weak upper bound on the objective
	 */
	private int calcUpperBoundOnObj(){
		int upperBound=0;
		//Bound on assignment cost
		for(Participant p : geoxam.participants){ 
			int maxDistance=0; //for each participant, calculate the distance to the site furthest away from the participant
			for(Site s: geoxam.sites){
				int dist=geoxam.getDistance(p, s);
				maxDistance=Math.max(dist, maxDistance);
			}
			upperBound+=maxDistance*geoxam.w_dist;
		}
		
		//Bound on room usage cost
		Room mostExpensiveRoom=null;
		double costMostExensiveRoom=0;
		for(Room r: geoxam.rooms){
			double cost= 1.0*(r.fixedCost+r.variableCost)/r.capacity;
			if(cost > costMostExensiveRoom){
				mostExpensiveRoom=r;	
				costMostExensiveRoom=cost;
			}
		}
		int maxRoomUsageCost=(int)Math.ceil(1.0*geoxam.participants.size()/mostExpensiveRoom.capacity)*(mostExpensiveRoom.fixedCost+mostExpensiveRoom.variableCost);
		upperBound+=maxRoomUsageCost*geoxam.w_cost;
		return upperBound;
	}
	
	/**
	 * This class modifies the data structures according to the branching decisions. A branching decision modifies the master problem or pricing problems. This class
	 * performs these changes. Whenever a backtrack occurs in the tree, all changes are reverted.
	 *
	 */
	protected class GraphManipulator{
		
		private boolean ignoreNextEvent=false; //Reverting an event triggers a new event. If this method invoked the reversal of an invent it shouldn't react on the next event. This to protect against a cascading effect.
		
		private BAPNode previousNode; //The previous node that has been solved.
		
		/**
		 * This Stack keeps track of all the changes that have been made to the data structures due to the execution of branching decisions.
		 * Each frame on the stack corresponds to all the changes caused by a single branching decision. The number of frames on the stack
		 * equals the depth of <previousNode> in the search tree.
		 */
		private Stack<BranchingDecision> changeHistory;
		
		public GraphManipulator(BAPNode rootNode){
			this.previousNode=rootNode;
//			btsp.addGraphListener(this);
			changeHistory=new Stack<BranchingDecision>();
		}
		
		/**
		 * Prepares the data structures for the next node to be solved.
		 */
		public void next(BAPNode nextNode){
			logger.debug("Previous node: {}, history: {}", previousNode.nodeID, previousNode.rootPath);
			logger.debug("Next node: {}, history: {}, nrBranchingDec: {}", nextNode.nodeID, nextNode.rootPath);
			
			//1. Revert state of the data structures back to the first mutual ancestor of <previousNode> and <nextNode>
			//1a. Find the number of mutual ancestors.
			int mutualNodesOnPath=0;
			for(int i=0; i<Math.min(previousNode.rootPath.size(), nextNode.rootPath.size()); i++){
				if(previousNode.rootPath.get(i) != nextNode.rootPath.get(i))
					break;
				mutualNodesOnPath++;
			}
			logger.debug("mutualNodesOnPath: {}", mutualNodesOnPath);
			
			//1b. revert until the first mutual ancestor
			while(changeHistory.size() > mutualNodesOnPath-1){
				logger.debug("Reverting 1 branch lvl");
				//List<RevertibleEvent> changes= changeHistory.pop();
				BranchingDecision bd=changeHistory.pop();
				//Revert the branching decision!
				bd.revertDecision();
			}
			/* 2. Modify the data structures by performing the branching decisions. Each branch will add a stack to the history.
			 * Each branching decision will trigger a number of modifications to the data structures. These are collected by the stacks.
			 */
			logger.debug("Next node nrBranchingDec: {}, changeHist.size: {}", nextNode.branchingDecisions.size(), changeHistory.size());
			for(int i=changeHistory.size(); i< nextNode.branchingDecisions.size(); i++){
				//Get the next branching decision and add it to the changeHistory
				BranchingDecision bd=nextNode.branchingDecisions.get(i);
				changeHistory.add(bd);
				//Execute the decision
				logger.debug("BAP exec branchingDecision: {}",bd);
				bd.executeDecision();
			}
			this.previousNode=nextNode;
		}
		
		/**
		 * Revert all currently active branching decisions, thereby restoring all data structures to their original state (i.e. the state they were in at the root node)
		 */
		public void restore(){
			while(!changeHistory.isEmpty()){
				BranchingDecision bd = changeHistory.pop();
				bd.revertDecision();
			}
		}
	}
	
	
	protected class BAPNode{
		public final int nodeID;
		
		
		protected final List<Integer> rootPath; //Sequence of nodeIDs from the root to this node. rootPath[0]=0, rootPath[last(rootPath)]=this.nodeID
		protected final List<BranchingDecision> branchingDecisions; //List of branching decisions that lead to this node. NOTE: Integer is just a place holder, should be replaced!
		protected final List<List<Column>> columns; //Columns used to initialize the master problem
		protected final List<Inequality> inequalities; //Valid inequalities used to initialize the master problem
		
		protected double bound; //Best bound on the optimum solution. If best incumbent int solution has a smaller objective than this bound, this node may be pruned.
		
		
		public BAPNode(int nodeID, List<Integer> rootPath, List<List<Column>> columns, List<Inequality> inequalities, double bound){
			this.nodeID=nodeID;
			this.columns=columns;
			this.inequalities=inequalities;
			this.branchingDecisions=new ArrayList<BranchingDecision>();
			this.rootPath=rootPath;
			this.bound=bound;
		}
		
		
		public String toString(){
			return "BAP node: "+nodeID;
		}
	}
}
