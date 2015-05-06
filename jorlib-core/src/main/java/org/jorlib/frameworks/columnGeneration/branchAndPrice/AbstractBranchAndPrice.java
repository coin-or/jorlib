package org.jorlib.frameworks.columnGeneration.branchAndPrice;

import java.util.*;

import org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling.*;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.bapNodeComparators.DFSbapNodeComparator;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecisionListener;
import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;
import org.jorlib.frameworks.columnGeneration.colgenMain.ColGen;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.master.AbstractMaster;
import org.jorlib.frameworks.columnGeneration.master.MasterData;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.Inequality;
import org.jorlib.frameworks.columnGeneration.model.ModelInterface;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblem;
import org.jorlib.frameworks.columnGeneration.pricing.DefaultPricingProblemSolverFactory;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemBundle;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemManager;
import org.jorlib.frameworks.columnGeneration.pricing.PricingProblemSolver;
import org.jorlib.frameworks.columnGeneration.util.Configuration;
import org.jorlib.frameworks.columnGeneration.util.MathProgrammingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBranchAndPrice<T extends ModelInterface, U extends AbstractColumn<T,U,V>, V extends AbstractPricingProblem<T,U,V>> {
	protected final Logger logger = LoggerFactory.getLogger(AbstractBranchAndPrice.class);
	protected final BAPNotifier notifier;
	protected final Configuration config=Configuration.getConfiguration();
	
	protected final T modelData;
	protected AbstractMaster<T,V,U, ? extends MasterData> master;
	protected final List<? extends AbstractBranchCreator<T, U, V>> branchCreators;
	protected List<V> pricingProblems;
	protected List<Class<? extends PricingProblemSolver<T, U, V>>> solvers;
	protected final PricingProblemManager<T, U, V> pricingProblemManager;

	//Store the best integer solution.
	protected int bestObjective=Integer.MAX_VALUE; //Stores the objective of the best solution, or an upper bound thereof
	protected boolean isOptimal=false; //Indicator whether the solution is optimal
	protected List<U> bestSolution=null;
	
	protected GraphManipulator graphManipulator;
	protected Queue<BAPNode<T,U>> queue;
	protected int nodeCounter=0;
	
	protected double lowerBoundOnObjective=0;
	protected int nodesProcessed=0; //Counts how many branch-and-price nodes have been processed.
	protected long timeSolvingMaster=0; //Counts how much time is spend on solving master problems
	protected long timeSolvingPricing=0; //Counts how much time is spend on solving pricing problems
	protected int totalGeneratedColumns=0; //Counts how many columns have been generated over the entire branch and price tree
	protected int totalNrIterations=0; // Counts how many column generation iterations have been made.

	//TODO: add artifical solution to nodes to ensure feasibility
	
	public AbstractBranchAndPrice(T modelData,
								  AbstractMaster<T,V,U, ? extends MasterData> master,//MasterFactory masterFactory,
								  List<V> pricingProblems,
								  List<Class<? extends PricingProblemSolver<T, U, V>>> solvers,
								  List<? extends AbstractBranchCreator<T, U, V>> branchCreators,
								  int upperBoundOnObjective,
								  List<U> initialSolution){
		this.modelData=modelData;
		this.master=master;
		this.branchCreators=branchCreators;
		this.pricingProblems=pricingProblems;
		this.solvers=solvers;
		this.bestObjective=upperBoundOnObjective;
		this.bestSolution=new ArrayList<>(initialSolution);
		queue =new PriorityQueue<>(new DFSbapNodeComparator());
		
		//Create the root node
		List<Integer> rootPath=new ArrayList<>();
		int nodeID=nodeCounter++;
		rootPath.add(nodeID);
		List<U> rootNodeColumns=new ArrayList<>();
		rootNodeColumns.addAll(initialSolution);
		BAPNode<T,U> rootNode=new BAPNode<>(nodeID, rootPath, rootNodeColumns, new ArrayList<>(), 0, Collections.emptyList());
		queue.add(rootNode);
		graphManipulator=new GraphManipulator(rootNode);
		
		//Initialize pricing algorithms
		List<PricingProblemBundle<T, U, V>> pricingProblemBunddles=new ArrayList<>();
		for(Class<? extends PricingProblemSolver<T, U, V>> solverClass : solvers){
			DefaultPricingProblemSolverFactory<T, U, V> factory=new DefaultPricingProblemSolverFactory<>(solverClass, modelData);
			PricingProblemBundle<T, U, V> bunddle=new PricingProblemBundle<>(solverClass, pricingProblems, factory);
			pricingProblemBunddles.add(bunddle);
		}
		
		//Create a pricing problem manager for parallel execution of the pricing problems
		pricingProblemManager=new PricingProblemManager<>(pricingProblems, pricingProblemBunddles);
		
		//Add the master problem and the pricing problem solver instances as BranchingDecisionListeners
		this.addBranchingDecisionListener(master);
		for(V pricingProblem : pricingProblems)
			this.addBranchingDecisionListener(pricingProblem);
		for(PricingProblemBundle<T, U, V> bunddle : pricingProblemBunddles){
			for(PricingProblemSolver solverInstance : bunddle.solverInstances)
				this.addBranchingDecisionListener(solverInstance);
		}

		//Register this class with the branch creators
		for(AbstractBranchCreator<T,U,V> branchCreator : branchCreators)
			branchCreator.registerBAP(this);

		//Create a new notifier which informs associated listeners about events occurring the the Branch and Price procedure
		notifier=new BAPNotifier();
	}
	public AbstractBranchAndPrice(T modelData,
								  AbstractMaster<T,V,U, ? extends MasterData> master,
								  V pricingProblem,
								  List<Class<? extends PricingProblemSolver<T, U, V>>> solvers,
								  List<? extends AbstractBranchCreator<T, U, V>> branchCreators,
								  int upperBoundOnObjective,
								  List<U> initialSolution){
		this(modelData, master, Collections.singletonList(pricingProblem), solvers, branchCreators, upperBoundOnObjective, initialSolution);
	}
	public AbstractBranchAndPrice(T modelData,
								  AbstractMaster<T,V,U, ? extends MasterData> master,
								  List<V> pricingProblems,
								  List<Class<? extends PricingProblemSolver<T, U, V>>> solvers,
								  List<? extends AbstractBranchCreator<T, U, V>> branchCreators,
								  int upperBoundOnObjective){
		this(modelData, master, pricingProblems, solvers, branchCreators, upperBoundOnObjective, Collections.emptyList());
		queue.peek().columns.addAll(this.generateArtificialSolution());
	}
	public AbstractBranchAndPrice(T modelData,
								  AbstractMaster<T,V,U, ? extends MasterData> master,
								  V pricingProblem,
								  List<Class<? extends PricingProblemSolver<T, U, V>>> solvers,
								  List<? extends AbstractBranchCreator<T, U, V>> branchCreators,
								  int upperBoundOnObjective){
		this(modelData, master, Collections.singletonList(pricingProblem), solvers, branchCreators, upperBoundOnObjective, Collections.emptyList());
		queue.peek().columns.addAll(this.generateArtificialSolution());
	}

	public void runBranchAndPrice(long timeLimit){
		notifier.fireStartBAPEvent(modelData.getName()); //Signal start Branch and Price process
		
		processNextNode: while(!queue.isEmpty()){
			BAPNode<T, U> bapNode = queue.poll();
			notifier.fireNextNodeEvent(bapNode);

			//Prune this node if the bound exceeds the best found solution (minimization problem). Since all solutions are integral, we may round upwards
			if(Math.ceil(bapNode.bound) >= bestObjective){
				notifier.firePruneNodeEvent(bapNode, bapNode.bound);
				continue;
			}
			
			//Solve the next node.
			graphManipulator.next(bapNode); //Prepare data structures for the next node
			//Generate artificial solution for this node to guarantee a solution.
			if(bapNode.nodeID != 0){
				bapNode.columns.addAll(this.generateArtificialSolution());
			}

			//TEMP
			System.out.println("Initial columns: ");
			Iterator it=bapNode.columns.iterator();
			while(it.hasNext())
				System.out.println(it.next());
			//END TEMP

			ColGen<T,U,V> cg=null;
			try {
				cg = new ColGen<>(modelData, master, pricingProblems, solvers, pricingProblemManager, bapNode.columns, bestObjective); //Solve the node
				cg.solve(timeLimit);
			} catch (TimeLimitExceededException e) {
				queue.add(bapNode);
				logger.debug("Caught timeout exception");
				break;
			}finally{
				//Update statistics
				if(cg != null) {
					timeSolvingMaster += cg.getMasterSolveTime();
					timeSolvingPricing += cg.getPricingSolveTime();
					totalNrIterations += cg.getNumberOfIterations();
					totalGeneratedColumns += cg.getNrGeneratedColumns();
					notifier.fireFinishCGEvent(bapNode.nodeID, cg.getLowerBound(), cg.getObjective(), cg.getNumberOfIterations(), cg.getMasterSolveTime(), cg.getPricingSolveTime(), cg.getNrGeneratedColumns());
				}
			}

			bapNode.bound=cg.getLowerBound(); //When node is solved to optimality, lowerBound equals the optimal solution of the column generation procedure
			
			//Check whether the node's bound exceeds the best integer solution, if so we can skip this node (no branching required)
			if(bapNode.bound >= bestObjective){ //Do not bother to create a branch even though the node is fractional. Bound is worse than best solution
				notifier.firePruneNodeEvent(bapNode, bapNode.bound);
				nodesProcessed++;
				continue;
			}
			
			//Query the solution
			List<U> solution= cg.getSolution();

			//TEMP
			System.out.println("Solution:");
			for(U column : solution)
				System.out.println(column);
			//END TEMP

			//Check if node was infeasible, i.e. whether there are artifical columns in the solution. If so, ignore it and continue with the next node.
			for(U column : solution){
				if(column.isArtificialColumn) {
					notifier.fireNodeIsInfeasibleEvent(bapNode);
					nodesProcessed++;
					continue processNextNode;
				}
			}

			//If solution is integral, check whether it is better than the current best solution
			if(this.isIntegralSolution(solution)){
				int integerObjective=MathProgrammingUtil.doubleToInt(cg.getObjective());
				notifier.fireNodeIsIntegerEvent(bapNode, bapNode.bound, integerObjective);
				if(integerObjective < this.bestObjective){
					this.bestObjective= integerObjective;
					this.bestSolution=solution;
				}
			}else{ //We need to branch
				notifier.fireNodeIsFractionalEvent(bapNode, bapNode.bound, cg.getObjective());
				List<Inequality> cuts=cg.getCuts();
				List<BAPNode<T, U>> newBranches=new ArrayList<>();
				for(AbstractBranchCreator<T, U, V> bc : branchCreators){
					newBranches.addAll(bc.branch(bapNode, solution, cuts));
					if(!newBranches.isEmpty()) break;
				}
				
				if(newBranches.isEmpty())
					throw new RuntimeException("BAP encountered fractional solution, but non of the BranchCreators produced any new branches?");
				else
					queue.addAll(newBranches);
			}

			nodesProcessed++;
		}
		
		//Restore the graph to its original form. May be used occasionally
		//graphManipulator.restore();

		//Update statistics
		if(queue.isEmpty()){
			if(this.hasSolution())
				lowerBoundOnObjective=this.bestObjective;
			this.isOptimal=true;
		}else{
			this.isOptimal=false;
			lowerBoundOnObjective= queue.peek().bound;
			for(BAPNode bapNode : queue){
				lowerBoundOnObjective=Math.min(lowerBoundOnObjective, bapNode.bound);
			}
		}
		notifier.fireStopBAPEvent(); //Signal that BAP has been completed
	}

	/**
	 * Returns a unique node ID. The internal nodeCounter is incremented by one each time this method is invoked.
	 * @return returns a unique node ID, thereby guaranteeing that none of the nodes in the branch-and-price tree have this ID.
	 */
	protected int getUniqueNodeID(){
		return  nodeCounter++;
	}
	
	/**
	 * Returns the objective of the best solution found
	 */
	public int getObjective(){
		return this.bestObjective;
	}
	
	/**
	 * Return whether a solution has been found
	 * @return true if a feasible solution has been found
	 */
	public boolean hasSolution(){
		return !bestSolution.isEmpty();
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

	/**
	 * Tests whether a given solution is an integer solution
	 * @param solution
	 * @return Returns true if solution is an integer solution, false otherwise
	 */
	protected abstract boolean isIntegralSolution(List<U> solution);

	/**
	 * Define how the nodes in the Branch-and-Price tree are processed. By default, the tree is processed in a Depth-First-Search manner but any other (custom)
	 * approach may be specified. This method may also be invoked during the search. The nodes already present in the queue will be reordered. As an example, one could prefer to
	 * process the first layers of the Branch-and-Price tree in a Breath-First-Search manner, thereby improving the bound of the nodes and then process the remaining nodes in a DFS manner.
	 * This example can also be achieved throuh a custom comparator.
	 * @param comparator
	 */
	public void setNodeOrdering(Comparator<BAPNode> comparator){
		Queue<BAPNode<T,U>> newQueue=new PriorityQueue<>(comparator);
		newQueue.addAll(queue);
		this.queue=newQueue;
	}

	/**
	 * Destroy both the master problem and pricing problems
	 */
	public void close(){
		master.close();
		pricingProblemManager.close();
	}

	public void addBranchingDecisionListener(BranchingDecisionListener listener){
		graphManipulator.addBranchingDecisionListener(listener);
	}
	public void removeBranchingDecisionListener(BranchingDecisionListener listener){
		graphManipulator.removeBranchingDecisionListener(listener);
	}

	public void addBranchAndPriceEventListener(BAPListener listener){
		notifier.addListener(listener);
	}
	public void removeBranchAndPriceEventListener(BAPListener listener){
		notifier.removeListener(listener);
	}
	/**
	 * Inner Class which notifies listeners
	 */
	protected class BAPNotifier{
		private Set<BAPListener> listeners;
		public BAPNotifier(){
			listeners=new LinkedHashSet<>();
		}

		public void addListener(BAPListener listener){
			this.listeners.add(listener);
		}
		public void removeListener(BAPListener listener){
			this.listeners.remove(listener);
		}

		protected void fireStartBAPEvent(String instanceName){
			StartBAPEvent startBAPEvent=null;
			for(BAPListener listener : listeners){
				if(startBAPEvent==null)
					startBAPEvent=new StartBAPEvent(AbstractBranchAndPrice.this, instanceName);
				listener.startBAP(startBAPEvent);
			}
		}
		protected void fireStopBAPEvent(){
			StopBAPEvent stopBAPEvent=null;
			for(BAPListener listener : listeners){
				if(stopBAPEvent==null)
					stopBAPEvent=new StopBAPEvent(AbstractBranchAndPrice.this);
				listener.stopBAP(stopBAPEvent);
			}
		}
		protected void fireNodeIsFractionalEvent(BAPNode node, double nodeBound, double nodeValue){
			logger.debug("Node {} is fractional. Solution: {}, bound: {}", new Object[]{node.nodeID, nodeValue, nodeBound});
			NodeIsFractionalEvent nodeIsFractionalEvent=null;
			for(BAPListener listener : listeners){
				if(nodeIsFractionalEvent==null)
					nodeIsFractionalEvent=new NodeIsFractionalEvent(AbstractBranchAndPrice.this, node, nodeBound, nodeValue);
				listener.nodeIsFractional(nodeIsFractionalEvent);
			}
		}
		protected void fireNodeIsIntegerEvent(BAPNode node, double nodeBound, int nodeValue){
			logger.debug("Node {} is integer. Solution: {}, new best: {}", new Object[]{node.nodeID, nodeValue, nodeValue < bestObjective});
			NodeIsIntegerEvent nodeIsIntegerEvent=null;
			for(BAPListener listener : listeners){
				if(nodeIsIntegerEvent==null)
					nodeIsIntegerEvent=new NodeIsIntegerEvent(AbstractBranchAndPrice.this, node, nodeBound, nodeValue);
				listener.nodeIsInteger(nodeIsIntegerEvent);
			}
		}
		protected void fireNodeIsInfeasibleEvent(BAPNode node){
			logger.debug("Node {} is infeasible.", node.nodeID);
			NodeIsInfeasibleEvent nodeIsInfeasibleEvent=null;
			for(BAPListener listener : listeners){
				if(nodeIsInfeasibleEvent==null)
					nodeIsInfeasibleEvent=new NodeIsInfeasibleEvent(AbstractBranchAndPrice.this, node);
				listener.nodeIsInfeasible(nodeIsInfeasibleEvent);
			}
		}
		protected void firePruneNodeEvent(BAPNode node, double nodeBound){
			logger.debug("Pruning node {}. Bound: {}, best incumbent: {}", new Object[]{node.nodeID, nodeBound, bestObjective});
			PruneNodeEvent pruneNodeEvent=null;
			for(BAPListener listener : listeners){
				if(pruneNodeEvent==null)
					pruneNodeEvent=new PruneNodeEvent(AbstractBranchAndPrice.this, node, nodeBound, bestObjective);
				listener.pruneNode(pruneNodeEvent);
			}
		}

		protected  void fireNextNodeEvent(BAPNode node){
			logger.debug("Processing node {}",node.nodeID);
			ProcessingNextNodeEvent processingNextNodeEvent=null;
			for(BAPListener listener : listeners){
				if(processingNextNodeEvent==null)
					processingNextNodeEvent=new ProcessingNextNodeEvent(AbstractBranchAndPrice.this, node, queue.size(), bestObjective);
				listener.processNextNode(processingNextNodeEvent);
			}
		}

		protected void fireFinishCGEvent(int nodeID, double nodeBound, double nodeValue, int numberOfCGIterations, long masterSolveTime, long pricingSolveTime, int nrGeneratedColumns){
			FinishCGEvent finishCGEvent=null;
			for(BAPListener listener : listeners){
				if(finishCGEvent==null)
					finishCGEvent=new FinishCGEvent(AbstractBranchAndPrice.this, nodeID, nodeBound, nodeValue, numberOfCGIterations, masterSolveTime, pricingSolveTime, nrGeneratedColumns);
				listener.finishedColumnGenerationForNode(finishCGEvent);
			}
		}
	}
}
