/* ==========================================
 * jORLib : a free Java OR library
 * ==========================================
 *
 * Project Info:  https://github.com/jkinable/jorlib
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under GPLv3
 *
 */
/* -----------------
 * AbstractBranchAndPrice.java
 * -----------------
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * Original Author:  Joris Kinable
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 *
 */
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

/**
 * Main class defining the Branch and Price Framework
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public abstract class AbstractBranchAndPrice<T extends ModelInterface, U extends AbstractColumn<T, V>, V extends AbstractPricingProblem<T>> {
	/** Logger attached to this class **/
	protected final Logger logger = LoggerFactory.getLogger(AbstractBranchAndPrice.class);
	/** Helper class which notifies BAPListeners **/
	protected final BAPNotifier notifier;
	/** Configuration file **/
	protected final Configuration config=Configuration.getConfiguration();

	/** Data model **/
	protected final T dataModel;
	/** Master problem **/
	protected AbstractMaster<T, U, V, ? extends MasterData> master;
	/** Branch creators which determine how to branch **/
	protected final List<? extends AbstractBranchCreator<T, U, V>> branchCreators;
	/** Pricing problems **/
	protected List<V> pricingProblems;
	/** Solvers for the pricing problems **/
	protected List<Class<? extends PricingProblemSolver<T, U, V>>> solvers;
	/** Pricing problem manager which solves pricing problems in parallel **/
	protected final PricingProblemManager<T, U, V> pricingProblemManager;

	/** Stores the objective of the best (integer) solution, or an upper bound thereof **/
	protected int bestObjective=Integer.MAX_VALUE;
	/** List containing the columns corresponding to the best integer solution (empty list when no feasible solution has been found) **/
	protected List<U> bestSolution=null;
	/** Indicator whether the best solution is optimal **/
	protected boolean isOptimal=false;

	/** Special class which manages the branch and price tree **/
	protected GraphManipulator graphManipulator;
	/** Queue containing the unexplored nodes in the branch and price tree **/
	protected Queue<BAPNode<T,U>> queue;
	/** Counter used to provide a unique ID for each node (counter gets incremented each time a new node is created) **/
	protected int nodeCounter=0;

	/** Lower bound on the optimal solution **/
	protected double lowerBoundOnObjective=0;
	/** Number of nodes fully explored **/
	protected int nodesProcessed=0;
	/** Total time spent solving master problems **/
	protected long timeSolvingMaster=0;
	/** Total time spent solving pricing problems **/
	protected long timeSolvingPricing=0;
	/** Counts how many columns have been generated over the entire branch and price tree **/
	protected int totalGeneratedColumns=0;
	/** Counts how many column generation iterations have been made. **/
	protected int totalNrIterations=0;

	/**
	 * Creates a new Branch and price instance, thereby initializing the data structures, and the root node.
	 * @param dataModel data model
	 * @param master master problem
	 * @param pricingProblems pricing problems
	 * @param solvers Pricing problem solvers
	 * @param branchCreators Branch creators
	 * @param upperBoundOnObjective upper bound on the objective value
	 * @param initialSolution initial solution
	 */
	public AbstractBranchAndPrice(T dataModel,
								  AbstractMaster<T, U, V, ? extends MasterData> master,
								  List<V> pricingProblems,
								  List<Class<? extends PricingProblemSolver<T, U, V>>> solvers,
								  List<? extends AbstractBranchCreator<T, U, V>> branchCreators,
								  int upperBoundOnObjective,
								  List<U> initialSolution){
		this.dataModel = dataModel;
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
			DefaultPricingProblemSolverFactory<T, U, V> factory=new DefaultPricingProblemSolverFactory<>(solverClass, dataModel);
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

	/**
	 * Creates a new Branch and price instance, thereby initializing the data structures, and the root node.
	 * @param dataModel Data model
	 * @param master Master problem
	 * @param pricingProblem Pricing problem
	 * @param solvers Pricing problem solvers
	 * @param branchCreators Branch creators
	 * @param upperBoundOnObjective Upper bound on objective value
	 * @param initialSolution Initial solution
	 */
	public AbstractBranchAndPrice(T dataModel,
								  AbstractMaster<T, U, V, ? extends MasterData> master,
								  V pricingProblem,
								  List<Class<? extends PricingProblemSolver<T, U, V>>> solvers,
								  List<? extends AbstractBranchCreator<T, U, V>> branchCreators,
								  int upperBoundOnObjective,
								  List<U> initialSolution){
		this(dataModel, master, Collections.singletonList(pricingProblem), solvers, branchCreators, upperBoundOnObjective, initialSolution);
	}

	/**
	 * Creates a new Branch and price instance, thereby initializing the data structures, and the root node.
	 * @param dataModel Data model
	 * @param master Master problem
	 * @param pricingProblems Pricing problems
	 * @param solvers Pricing problem solvers
	 * @param branchCreators Branch creators
	 * @param upperBoundOnObjective Upper bound on objective value
	 */
	public AbstractBranchAndPrice(T dataModel,
								  AbstractMaster<T, U, V, ? extends MasterData> master,
								  List<V> pricingProblems,
								  List<Class<? extends PricingProblemSolver<T, U, V>>> solvers,
								  List<? extends AbstractBranchCreator<T, U, V>> branchCreators,
								  int upperBoundOnObjective){
		this(dataModel, master, pricingProblems, solvers, branchCreators, upperBoundOnObjective, Collections.emptyList());
		queue.peek().columns.addAll(this.generateArtificialSolution());
	}

	/**
	 * Creates a new Branch and price instance, thereby initializing the data structures, and the root node.
	 * @param dataModel Data model
	 * @param master Master problem
	 * @param pricingProblem Pricing problem
	 * @param solvers Pricing problem solvers
	 * @param branchCreators Branch creators
	 * @param upperBoundOnObjective Upper bound on objective value
	 */
	public AbstractBranchAndPrice(T dataModel,
								  AbstractMaster<T, U, V, ? extends MasterData> master,
								  V pricingProblem,
								  List<Class<? extends PricingProblemSolver<T, U, V>>> solvers,
								  List<? extends AbstractBranchCreator<T, U, V>> branchCreators,
								  int upperBoundOnObjective){
		this(dataModel, master, Collections.singletonList(pricingProblem), solvers, branchCreators, upperBoundOnObjective, Collections.emptyList());
		queue.peek().columns.addAll(this.generateArtificialSolution());
	}

	/**
	 * Starts running the branch and price algorithm.
	 * Note: In the current version of the code, one should not invoke this function multiple times on the same instance!
	 * @param timeLimit Future point in time by which the algorithm should finish
	 */
	public void runBranchAndPrice(long timeLimit){
		notifier.fireStartBAPEvent(); //Signal start Branch and Price process
		
		processNextNode: while(!queue.isEmpty()){ //Start processing nodes until the queue is empty
			BAPNode<T, U> bapNode = queue.poll();
			notifier.fireNextNodeEvent(bapNode);

			//Prune this node if the bound exceeds the best found solution (minimization problem). Since all solutions are integral, we may round upwards
			if(Math.ceil(bapNode.bound) >= bestObjective){
				notifier.firePruneNodeEvent(bapNode, bapNode.bound);
				continue;
			}
			
			graphManipulator.next(bapNode); //Prepare data structures for the next node

			//Generate artificial solution for this node to guarantee that the master problem is feasible
			if(bapNode.nodeID != 0){
				bapNode.columns.addAll(this.generateArtificialSolution());
			}

			ColGen<T,U,V> cg=null;
			try {
				cg = new ColGen<>(dataModel, master, pricingProblems, solvers, pricingProblemManager, bapNode.columns, bestObjective); //Solve the node
				cg.solve(timeLimit);
			} catch (TimeLimitExceededException e) {
				queue.add(bapNode);
				notifier.fireTimeOutEvent(bapNode);
				break;
			}finally{
				//Update statistics
				if(cg != null) {
					timeSolvingMaster += cg.getMasterSolveTime();
					timeSolvingPricing += cg.getPricingSolveTime();
					totalNrIterations += cg.getNumberOfIterations();
					totalGeneratedColumns += cg.getNrGeneratedColumns();
					notifier.fireFinishCGEvent(bapNode, cg.getLowerBound(), cg.getObjective(), cg.getNumberOfIterations(), cg.getMasterSolveTime(), cg.getPricingSolveTime(), cg.getNrGeneratedColumns());
				}
			}

			bapNode.bound=cg.getLowerBound(); //When node is solved to optimality, lowerBound equals the optimal solution of the column generation procedure
			
			//Check whether the node's bound exceeds the best integer solution, if so we can prune this node (no branching required)
			if(bapNode.bound >= bestObjective){ //Do not bother to create a branch even though the node is fractional. Bound is worse than best solution
				notifier.firePruneNodeEvent(bapNode, bapNode.bound);
				nodesProcessed++;
				continue;
			}
			
			//Query the solution
			List<U> solution= cg.getSolution();

			//Check if node is infeasible, i.e. whether there are artifical columns in the solution. If so, ignore it and continue with the next node.
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
				else {
					queue.addAll(newBranches);
					notifier.fireBranchEvent(bapNode, Collections.unmodifiableList(newBranches));
				}
			}

			nodesProcessed++;
		}
		
		//Update statistics
		if(queue.isEmpty()){ //Problem solved to optimality
			if(this.hasSolution())
				lowerBoundOnObjective=this.bestObjective;
			this.isOptimal=true;
		}else{ //Problem NOT solved to optimality
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
	 * Returns the objective value of the best solution found
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
	 * @param solution List of columns forming the solution
	 * @return Returns true if solution is an integer solution, false otherwise
	 */
	protected abstract boolean isIntegralSolution(List<U> solution);

	/**
	 * Define how the nodes in the Branch-and-Price tree are processed. By default, the tree is processed in a Depth-First-Search manner but any other (custom)
	 * approach may be specified. This method may also be invoked during the search. The nodes already present in the queue will be reordered. As an example, one could prefer to
	 * process the first layers of the Branch-and-Price tree in a Breath-First-Search manner, thereby improving the bound of the nodes and then process the remaining nodes in a DFS manner.
	 * This example can also be achieved throuh a custom comparator.
	 * @param comparator comparator
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

	/**
	 * Adds a BranchingDecisionListener
	 * @param listener listener
	 */
	public void addBranchingDecisionListener(BranchingDecisionListener listener){
		graphManipulator.addBranchingDecisionListener(listener);
	}

	/**
	 * Removes a BranchingDecisionListener
	 * @param listener listener
	 */
	public void removeBranchingDecisionListener(BranchingDecisionListener listener){
		graphManipulator.removeBranchingDecisionListener(listener);
	}

	/**
	 * Adds a BAPListener
	 * @param listener listener
	 */
	public void addBranchAndPriceEventListener(BAPListener listener){
		notifier.addListener(listener);
	}

	/**
	 * Removes a BAPListener
	 * @param listener listener
	 */
	public void removeBranchAndPriceEventListener(BAPListener listener){
		notifier.removeListener(listener);
	}


	/**
	 * Inner Class which notifies BAPListeners
	 */
	protected class BAPNotifier{
		/** Listeners **/
		private Set<BAPListener> listeners;

		/**
		 * Creates a new BAPNotifier
		 */
		public BAPNotifier(){
			listeners=new LinkedHashSet<>();
		}

		/**
		 * Adds a listener
		 * @param listener listener
		 */
		public void addListener(BAPListener listener){
			this.listeners.add(listener);
		}

		/**
		 * Removes a listener
		 * @param listener listener
		 */
		public void removeListener(BAPListener listener){
			this.listeners.remove(listener);
		}

		/**
		 * Fires a StartEvent
		 */
		public void fireStartBAPEvent(){
			StartEvent startEvent =null;
			for(BAPListener listener : listeners){
				if(startEvent ==null)
					startEvent =new StartEvent(AbstractBranchAndPrice.this, dataModel.getName(), bestObjective);
				listener.startBAP(startEvent);
			}
		}

		/**
		 * Fires a FinishEvent
		 */
		public void fireStopBAPEvent(){
			FinishEvent finishEvent =null;
			for(BAPListener listener : listeners){
				if(finishEvent ==null)
					finishEvent =new FinishEvent(AbstractBranchAndPrice.this);
				listener.finishBAP(finishEvent);
			}
		}

		/**
		 * Fires a NodeIsFractionalEvent
		 * @param node Node which is fractional
		 * @param nodeBound Bound on the node
		 * @param nodeValue Objective value of the node
		 */
		public void fireNodeIsFractionalEvent(BAPNode node, double nodeBound, double nodeValue){
			logger.debug("Node {} is fractional. Solution: {}, bound: {}", new Object[]{node.nodeID, nodeValue, nodeBound});
			NodeIsFractionalEvent nodeIsFractionalEvent=null;
			for(BAPListener listener : listeners){
				if(nodeIsFractionalEvent==null)
					nodeIsFractionalEvent=new NodeIsFractionalEvent(AbstractBranchAndPrice.this, node, nodeBound, nodeValue);
				listener.nodeIsFractional(nodeIsFractionalEvent);
			}
		}

		/**
		 * Fires a NodeIsIntegerEvent
		 * @param node Node which is integer
		 * @param nodeBound Bound on the node
		 * @param nodeValue Objective value of the node
		 */
		public void fireNodeIsIntegerEvent(BAPNode node, double nodeBound, int nodeValue){
			logger.debug("Node {} is integer. Solution: {}, new best: {}", new Object[]{node.nodeID, nodeValue, nodeValue < bestObjective});
			NodeIsIntegerEvent nodeIsIntegerEvent=null;
			for(BAPListener listener : listeners){
				if(nodeIsIntegerEvent==null)
					nodeIsIntegerEvent=new NodeIsIntegerEvent(AbstractBranchAndPrice.this, node, nodeBound, nodeValue);
				listener.nodeIsInteger(nodeIsIntegerEvent);
			}
		}

		/**
		 * Fires a NodeIsInfeasibleEvent
		 * @param node Node which is infeasible
		 */
		public void fireNodeIsInfeasibleEvent(BAPNode node){
			logger.debug("Node {} is infeasible.", node.nodeID);
			NodeIsInfeasibleEvent nodeIsInfeasibleEvent=null;
			for(BAPListener listener : listeners){
				if(nodeIsInfeasibleEvent==null)
					nodeIsInfeasibleEvent=new NodeIsInfeasibleEvent(AbstractBranchAndPrice.this, node);
				listener.nodeIsInfeasible(nodeIsInfeasibleEvent);
			}
		}

		/**
		 * Fires a PruneNodeEvent
		 * @param node Node being pruned
		 * @param nodeBound Bound on the node
		 */
		public void firePruneNodeEvent(BAPNode node, double nodeBound){
			logger.debug("Pruning node {}. Bound: {}, best incumbent: {}", new Object[]{node.nodeID, nodeBound, bestObjective});
			PruneNodeEvent pruneNodeEvent=null;
			for(BAPListener listener : listeners){
				if(pruneNodeEvent==null)
					pruneNodeEvent=new PruneNodeEvent(AbstractBranchAndPrice.this, node, nodeBound, bestObjective);
				listener.pruneNode(pruneNodeEvent);
			}
		}

		/**
		 * Fires a ProcessingNextNodeEvent
		 * @param node Node which will be processed
		 */
		public  void fireNextNodeEvent(BAPNode node){
			logger.debug("Processing node {}",node.nodeID);
			ProcessingNextNodeEvent processingNextNodeEvent=null;
			for(BAPListener listener : listeners){
				if(processingNextNodeEvent==null)
					processingNextNodeEvent=new ProcessingNextNodeEvent(AbstractBranchAndPrice.this, node, queue.size(), bestObjective);
				listener.processNextNode(processingNextNodeEvent);
			}
		}

		/**
		 * Fires a FinishProcessingNodeEvent
		 * @param node Node which has been solved
		 * @param nodeBound Bound on node
		 * @param nodeValue Objective value of node
		 * @param numberOfCGIterations Number of Column Generation iterations it took to solve the node
		 * @param masterSolveTime Total time spent on solving master problems for this node
		 * @param pricingSolveTime Total time spent on solving pricing problems for this node
		 * @param nrGeneratedColumns Total number of columns generated for this node
		 */
		public void fireFinishCGEvent(BAPNode node, double nodeBound, double nodeValue, int numberOfCGIterations, long masterSolveTime, long pricingSolveTime, int nrGeneratedColumns){
			FinishProcessingNodeEvent finishProcessingNodeEvent =null;
			for(BAPListener listener : listeners){
				if(finishProcessingNodeEvent ==null)
					finishProcessingNodeEvent =new FinishProcessingNodeEvent(AbstractBranchAndPrice.this, node, nodeBound, nodeValue, numberOfCGIterations, masterSolveTime, pricingSolveTime, nrGeneratedColumns);
				listener.finishedColumnGenerationForNode(finishProcessingNodeEvent);
			}
		}

		/**
		 * Fires a BranchEvent
		 * @param parentNode Parent node
		 * @param childNodes Child nodes spawned from the branching process
		 */
		public void fireBranchEvent(BAPNode parentNode, List<BAPNode> childNodes){
			BranchEvent branchEvent=null;
			for(BAPListener listener : listeners){
				if(branchEvent==null)
					branchEvent=new BranchEvent(AbstractBranchAndPrice.this, childNodes.size(), parentNode, childNodes);
				listener.branchCreated(branchEvent);
			}
		}

		/**
		 * Fires a TimeLimitExceededEvent
		 * @param node Node which was being processed when the event occurred
		 */
		public  void fireTimeOutEvent(BAPNode node){
			logger.debug("Caught timeout exception while processing node {}",node.nodeID);
			TimeLimitExceededEvent timeLimitExceededEvent =null;
			for(BAPListener listener : listeners){
				if(timeLimitExceededEvent ==null)
					timeLimitExceededEvent =new TimeLimitExceededEvent(AbstractBranchAndPrice.this, node);
				listener.timeLimitExceeded(timeLimitExceededEvent);
			}
		}
	}
}
