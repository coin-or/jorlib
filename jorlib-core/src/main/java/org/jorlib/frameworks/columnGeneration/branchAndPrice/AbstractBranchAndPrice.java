/* ==========================================
 * jORLib : a free Java OR library
 * ==========================================
 *
 * Project Info:  https://github.com/jkinable/jorlib
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
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
import org.jorlib.frameworks.columnGeneration.master.OptimizationSense;
import org.jorlib.frameworks.columnGeneration.model.ModelInterface;
import org.jorlib.frameworks.columnGeneration.pricing.*;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblemSolver;
import org.jorlib.frameworks.columnGeneration.util.Configuration;
import org.jorlib.frameworks.columnGeneration.util.MathProgrammingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class defining the Branch-and-Price Framework
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public abstract class AbstractBranchAndPrice<T extends ModelInterface, U extends AbstractColumn<T, V>, V extends AbstractPricingProblem<T>> {
	/** Logger attached to this class **/
	protected final Logger logger = LoggerFactory.getLogger(AbstractBranchAndPrice.class);
	/** Helper class which notifies BAPListeners **/
	protected final BAPNotifier notifier;
	/** Listeners for column generation events (CLListener) **/
	protected final Set<CGListener> columnGenerationEventListeners;
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
	protected List<Class<? extends AbstractPricingProblemSolver<T, U, V>>> solvers;
	/** Pricing problem manager which solves pricing problems in parallel **/
	protected final PricingProblemManager<T, U, V> pricingProblemManager;
	/** Defines whether the master problem is a minimization or a maximization problem **/
	protected final OptimizationSense optimizationSenseMaster;

	/** Stores the objective of the best (integer) solution **/
	protected int objectiveIncumbentSolution;
	/** List containing the columns corresponding to the best integer solution (empty list when no feasible solution has been found) **/
	protected List<U> incumbentSolution =new ArrayList<>();
	/** Indicator whether the best solution is optimal **/
	protected boolean isOptimal=false;

	/** Special class which manages the Branch-and-Price tree **/
	protected GraphManipulator graphManipulator;
	/** Queue containing the unexplored nodes in the Branch-and-Price tree **/
	protected Queue<BAPNode<T,U>> queue;
	/** Counter used to provide a unique ID for each node (counter gets incremented each time a new node is created) **/
	protected int nodeCounter=0;
	/** A reference to the root node in the tree **/
	protected BAPNode<T,U> rootNode;

	/** Upper bound on the optimal solution **/
	protected double upperBoundOnObjective=Double.MAX_VALUE;
	/** Lower bound on the optimal solution **/
	protected double lowerBoundOnObjective=-Double.MAX_VALUE;
	/** Number of nodes fully explored (including pruned nodes) **/
	protected int nodesProcessed=0;
	/** Total time spent solving master problems **/
	protected long timeSolvingMaster=0;
	/** Total time spent solving pricing problems **/
	protected long timeSolvingPricing=0;
	/** Total runtime **/
	protected long runtime=0;
	/** Counts how many columns have been generated over the entire Branch-and-Price tree **/
	protected int totalGeneratedColumns=0;
	/** Counts how many column generation iterations have been made. **/
	protected int totalNrIterations=0;

	/**
	 * Creates a new Branch-and-Price instance, thereby initializing the data structures, and the root node.
	 * @param dataModel data model
	 * @param master master problem
	 * @param pricingProblems pricing problems
	 * @param solvers Pricing problem solvers
	 * @param branchCreators Branch creators
	 * @param lowerBoundOnObjective Lower bound on objective value
	 * @param upperBoundOnObjective upper bound on the objective value
	 */
	public AbstractBranchAndPrice(T dataModel,
								  AbstractMaster<T, U, V, ? extends MasterData> master,
								  List<V> pricingProblems,
								  List<Class<? extends AbstractPricingProblemSolver<T, U, V>>> solvers,
								  List<? extends AbstractBranchCreator<T, U, V>> branchCreators,
								  double lowerBoundOnObjective,
								  double upperBoundOnObjective){
		this.dataModel = dataModel;
		this.master=master;
		optimizationSenseMaster=master.getOptimizationSense();
		this.branchCreators=branchCreators;
		this.pricingProblems=pricingProblems;
		this.solvers=solvers;
		queue =new PriorityQueue<>(new DFSbapNodeComparator());
		this.objectiveIncumbentSolution=(optimizationSenseMaster == OptimizationSense.MINIMIZE ? Integer.MAX_VALUE : -Integer.MAX_VALUE);
		this.lowerBoundOnObjective=lowerBoundOnObjective;
		this.upperBoundOnObjective=upperBoundOnObjective;
		
		//Create the root node
		List<Integer> rootPath=new ArrayList<>();
		int nodeID=nodeCounter++;
		rootPath.add(nodeID);
		if(optimizationSenseMaster==OptimizationSense.MINIMIZE)
			rootNode=new BAPNode<>(nodeID, rootPath, new ArrayList<>(), new ArrayList<>(), lowerBoundOnObjective, Collections.emptyList());
		else
			rootNode=new BAPNode<>(nodeID, rootPath, new ArrayList<>(), new ArrayList<>(), upperBoundOnObjective, Collections.emptyList());
		queue.add(rootNode);
		graphManipulator=new GraphManipulator(rootNode);
		
		//Initialize pricing algorithms
		Map<Class<? extends AbstractPricingProblemSolver<T, U, V>>, PricingProblemBundle<T, U, V>> pricingProblemBundles=new HashMap<>();
		for(Class<? extends AbstractPricingProblemSolver<T, U, V>> solverClass : solvers){
			DefaultPricingProblemSolverFactory<T, U, V> factory=new DefaultPricingProblemSolverFactory<>(solverClass, dataModel);
			PricingProblemBundle<T, U, V> bunddle=new PricingProblemBundle<>(solverClass, pricingProblems, factory);
			pricingProblemBundles.put(solverClass, bunddle);
		}
		
		//Create a pricing problem manager for parallel execution of the pricing problems
		pricingProblemManager=new PricingProblemManager<>(pricingProblems, pricingProblemBundles);
		
		//Add the master problem and the pricing problem solver instances as BranchingDecisionListeners
		this.addBranchingDecisionListener(master);
		for(V pricingProblem : pricingProblems)
			this.addBranchingDecisionListener(pricingProblem);
		for(PricingProblemBundle<T, U, V> bunddle : pricingProblemBundles.values()){
			for(AbstractPricingProblemSolver solverInstance : bunddle.solverInstances)
				this.addBranchingDecisionListener(solverInstance);
		}

		//Register this class with the branch creators
		for(AbstractBranchCreator<T,U,V> branchCreator : branchCreators)
			branchCreator.registerBAP(this);

		//Create a new notifier which informs associated listeners about events occurring the the Branch-and-Price procedure
		notifier=new BAPNotifier();
		columnGenerationEventListeners=new LinkedHashSet<>();
	}

	/**
	 * Creates a new Branch-and-Price instance, thereby initializing the data structures, and the root node.
	 * @param dataModel Data model
	 * @param master Master problem
	 * @param pricingProblem Pricing problem
	 * @param solvers Pricing problem solvers
	 * @param branchCreators Branch creators
	 * @param lowerBoundOnObjective Lower bound on objective value
	 * @param upperBoundOnObjective Upper bound on objective value
	 */
	public AbstractBranchAndPrice(T dataModel,
								  AbstractMaster<T, U, V, ? extends MasterData> master,
								  V pricingProblem,
								  List<Class<? extends AbstractPricingProblemSolver<T, U, V>>> solvers,
								  List<? extends AbstractBranchCreator<T, U, V>> branchCreators,
								  double lowerBoundOnObjective,
								  double upperBoundOnObjective){
		this(dataModel, master, Collections.singletonList(pricingProblem), solvers, branchCreators, lowerBoundOnObjective, upperBoundOnObjective);
	}

	/**
	 * Provide an initial solution. This solution will be used as an initial set of columns for the master problem of the root node
	 * @param objectiveInitialSolution objective value of the initial solution
	 * @param initialSolution columns constituting the initial solution
	 */
	public void warmStart(int objectiveInitialSolution, List<U> initialSolution){
		rootNode=queue.peek();
		if(rootNode.nodeID != 0)
			throw new RuntimeException("This method can only be invoked at the start of the Branch-and-Price procedure, before runBranchAndPrice is invoked");
		rootNode.addInitialColumns(initialSolution);
		this.objectiveIncumbentSolution=objectiveInitialSolution;
		this.incumbentSolution=new ArrayList<>(initialSolution);
		if(optimizationSenseMaster==OptimizationSense.MINIMIZE)
			this.upperBoundOnObjective=objectiveInitialSolution;
		else
			this.lowerBoundOnObjective=objectiveInitialSolution;
	}

	/**
	 * Starts running the Branch-and-Price algorithm.
	 * Note: In the current version of the code, one should not invoke this function multiple times on the same instance!
	 * @param timeLimit Future point in time by which the algorithm should finish
	 */
	public void runBranchAndPrice(long timeLimit){
		notifier.fireStartBAPEvent(); //Signal start Branch-and-Price process
		this.runtime=System.currentTimeMillis();

		//Check whether an warm start is provided, if not, invoke generateInitialFeasibleSolution
		BAPNode<T, U> rootNode = queue.peek();
		if(rootNode.getInitialColumns().isEmpty())
			rootNode.addInitialColumns(this.generateInitialFeasibleSolution(rootNode));

		//Start processing nodes until the queue is empty
		while(!queue.isEmpty()){
			BAPNode<T, U> bapNode = queue.poll();
			notifier.fireNextNodeEvent(bapNode);

			//Prune this node if its bound is worse than the best found solution. Since all solutions are integral, we may round up/down, depending on the optimization sense
			if(this.nodeCanBePruned(bapNode)){
				notifier.firePruneNodeEvent(bapNode, bapNode.bound);
				nodesProcessed++;
				continue;
			}
			
			graphManipulator.next(bapNode); //Prepare data structures for the next node

			//Generate an initial solution for this node to guarantee that the master problem is feasible
			if(bapNode.nodeID != 0){
				bapNode.addInitialColumns(this.generateInitialFeasibleSolution(bapNode));
			}

			//Solve the next BAPNode
			try {
				this.solveBAPNode(bapNode, timeLimit);
			} catch (TimeLimitExceededException e) {
				queue.add(bapNode);
				notifier.fireTimeOutEvent(bapNode);
				break;
			}

			//Prune this node if its bound is worse than the best found solution. Since all solutions are integral, we may round up/down, depending on the optimization sense
			if(this.nodeCanBePruned(bapNode)){
				notifier.firePruneNodeEvent(bapNode, bapNode.bound);
				nodesProcessed++;
				continue;
			}
			
			//Check whether the node is infeasible, i.e. whether there are artifical columns in the solution. If so, ignore it and continue with the next node.
			if(this.isInfeasibleNode(bapNode)){
				notifier.fireNodeIsInfeasibleEvent(bapNode);
				nodesProcessed++;
				continue;
			}

			//If solution is integral, check whether it is better than the current best solution
			if(this.isIntegerNode(bapNode)){
				int integerObjective=MathProgrammingUtil.doubleToInt(bapNode.objective);
				notifier.fireNodeIsIntegerEvent(bapNode, bapNode.bound, integerObjective);
				if(optimizationSenseMaster == OptimizationSense.MINIMIZE && integerObjective < this.upperBoundOnObjective){
					this.objectiveIncumbentSolution = integerObjective;
					this.upperBoundOnObjective = integerObjective;
					this.incumbentSolution =bapNode.solution;
				}else if(optimizationSenseMaster == OptimizationSense.MAXIMIZE && integerObjective > this.lowerBoundOnObjective){
					this.objectiveIncumbentSolution = integerObjective;
					this.lowerBoundOnObjective = integerObjective;
					this.incumbentSolution =bapNode.solution;
				}
			}else{ //We need to branch
				notifier.fireNodeIsFractionalEvent(bapNode, bapNode.bound, bapNode.objective);
				List<BAPNode<T, U>> newBranches=new ArrayList<>();
				for(AbstractBranchCreator<T, U, V> bc : branchCreators){
					newBranches.addAll(bc.branch(bapNode));
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
			this.isOptimal=true;
			if(optimizationSenseMaster == OptimizationSense.MINIMIZE)
				this.lowerBoundOnObjective=this.objectiveIncumbentSolution;
			else
				this.upperBoundOnObjective=this.objectiveIncumbentSolution;
		}else{ //Problem NOT solved to optimality
			this.isOptimal=false;
			if(optimizationSenseMaster == OptimizationSense.MINIMIZE) {
				lowerBoundOnObjective = queue.peek().bound;
				for (BAPNode bapNode : queue) {
					lowerBoundOnObjective = Math.min(lowerBoundOnObjective, bapNode.bound);
				}
			}else{
				upperBoundOnObjective = queue.peek().bound;
				for (BAPNode bapNode : queue) {
					upperBoundOnObjective = Math.max(upperBoundOnObjective, bapNode.bound);
				}
			}
		}
		notifier.fireStopBAPEvent(); //Signal that BAP has been completed
		this.runtime=System.currentTimeMillis()-runtime;
	}

	/**
	 * Solve a given Branch-and-Price node
	 * @param bapNode node in Branch-and-Price tree
	 * @param timeLimit future point in time by which the method must be finished
	 * @throws TimeLimitExceededException TimeLimitExceededException
	 */
	protected void solveBAPNode(BAPNode<T,U> bapNode, long timeLimit) throws TimeLimitExceededException {
		ColGen<T,U,V> cg=null;
		try {
			cg = new ColGen<>(dataModel, master, pricingProblems, solvers, pricingProblemManager, bapNode.initialColumns, objectiveIncumbentSolution, bapNode.getBound()); //Solve the node
			for(CGListener listener : columnGenerationEventListeners) cg.addCGEventListener(listener);
			cg.solve(timeLimit);
		}finally{
			//Update statistics
			if(cg != null) {
				timeSolvingMaster += cg.getMasterSolveTime();
				timeSolvingPricing += cg.getPricingSolveTime();
				totalNrIterations += cg.getNumberOfIterations();
				totalGeneratedColumns += cg.getNrGeneratedColumns();
				notifier.fireFinishCGEvent(bapNode, cg.getBound(), cg.getObjective(), cg.getNumberOfIterations(), cg.getMasterSolveTime(), cg.getPricingSolveTime(), cg.getNrGeneratedColumns());
			}
		}
		bapNode.storeSolution(cg.getObjective(), cg.getBound(), cg.getSolution(), cg.getCuts());
	}

	/**
	 * Returns a unique node ID. The internal nodeCounter is incremented by one each time this method is invoked.
	 * @return returns a unique node ID for the purpose of creating new BAPNodes, thereby guaranteeing that none of the nodes in the Branch-and-Price tree have this ID.
	 */
	protected int getUniqueNodeID(){
		return  nodeCounter++;
	}
	
	/**
	 * Returns the objective value of the best solution found
	 * @return the objective of the best integer solution found during the Branch-and-Price search
	 */
	public int getObjective(){
		return this.objectiveIncumbentSolution;
	}

	/**
	 * Returns the strongest available bound after the root node has been solved. Whenever the root node was solved to optimality, the value returned equals the objective of (optimal solution) of the root node. Whenever the
	 * node is not solved to optimality, e.g. due to a time limit,the strongest lower bound (minimization problem) or strongest upper bound (maximization problem) is returned. Not that this function
	 * is equivalent to solving the master problem relaxation through column generation.
	 * @return Strongest available bound after the root node has been solved
	 */
	public double getBoundRootNode(){ return rootNode.getBound();}
	
	/**
	 * Return whether a solution has been found
	 * @return true if a feasible solution has been found
	 */
	public boolean hasSolution(){
		return !incumbentSolution.isEmpty();
	}
	
	/**
	 * Returns whether the solution is optimal, that is, whether the entire Branch-and-Price tree has been processed
	 * @return {@code true} if the problem instance has been solved to optimality. ({@code getBound} and {@code getObjective} methods must yield the same value.
	 */
	public boolean isOptimal(){
		return isOptimal;
	}
	
	/**
	 * Returns strongest available bound on the objective function. If the problem is a minimization problem, the strongest available lower bound is returned,
	 * if the problem is a maximization problem, the strongest available upper bound is returned
	 * @return Returns the best bound on the optimal solution (upper bound if the master is a maximization problem, a lower bound if the master is a minimization problem)
	 */
	public double getBound(){
		return (optimizationSenseMaster == OptimizationSense.MINIMIZE ? this.lowerBoundOnObjective : this.upperBoundOnObjective);
	}
	
	/**
	 * Returns the number of processed nodes
	 * @return the number of nodes processed
	 */
	public int getNumberOfProcessedNodes(){
		return nodesProcessed;
	}

	/**
	 * Total time spent solving the Branch-and-Price problem.
	 * @return total time spent solving the Branch-and-Price problem. This time should equal {@link #getMasterSolveTime()}+{@link #getPricingSolveTime()}+overhead due to branching;
	 */
	public long getSolveTime(){
		return runtime;
	}

	/**
	 * Total time spent on solving master problems
	 * @return total time spent on solving master problems
	 */
	public long getMasterSolveTime(){
		return timeSolvingMaster;
	}
	/**
	 * Total time spent on solving pricing problems
	 * @return total time spent on solving pricing problems
	 */
	public long getPricingSolveTime(){
		return timeSolvingPricing;
	}
	/**
	 * Counts how many columns have been generated over the entire Branch-and-Price tree
	 * @return returns total number of columns generated (summed over all processed nodes)
	 */
	public int getTotalGeneratedColumns(){
		return totalGeneratedColumns;
	}
	/**
	 * Counts how many column generation iterations have been made over the entire Branch-and-Price tree
	 * @return returns the total number of column generation iterations (summed over all processed nodes)
	 **/
	public int getTotalNrIterations(){
		return totalNrIterations;
	}
	/**
	 * Returns the best solution found
	 * @return Returns the columns corresponding with the best solution.
	 */
	public List<U> getSolution(){
		return incumbentSolution;
	}
	
	/**
	 * Create an artificial solution which satisfies the node's master problem and hence constitutes a feasible initial solution.
	 * The columns are not necessary feasible or meet the definition of a column; it is undesirable that these columns end up in a final solution.
	 * To prevent them from ending up in a final solution, a high cost is associated with them.
	 * @return List of columns constituting the artificial solution
	 * @deprecated use {@link #generateInitialFeasibleSolution(BAPNode node)} instead.
	 */
	protected List<U> generateArtificialSolution(){return Collections.emptyList();}

	/**
	 * Creates a (small) set of columns used to initialize a node in the Branch-and-Price tree.
	 * Whenever a node in the Branch-and-Price tree is being solved, its initial master problem needs to be feasible. To ensure feasibility, a minimal
	 * subset of initial columns needs to be provided. This initial subset is generated in this method.
	 *
	 * Any node, other than the root node, will receive a set of initial columns from its parent node. However, because a number of columns which did not comply with the
	 * branching decision may have been filtered out (see {@link org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision#columnIsCompatibleWithBranchingDecision(AbstractColumn)}),
	 * the subset of columns passed by the parent may not constitute a feasible solution. It is up to the developer to very this.
	 *
	 * In general it is hard to come up with a feasible set of columns which satisfy all constraints in the master problem, including potential branching decisions. A common technique
	 * is to add 'artificial' columns. These columns are not necessary feasible columns (they may not satisfy the definition of a feasible column as defined by the pricing problem). Furthermore, these
	 * columns may also violate branching decisions. To prevent artificial columns from ending up in a final solution, a high cost is associated with them (higher than any solution consisting
	 * of normal columns). Whenever a cheaper feasible solution is available, the Master Problem will automatically price artificial columns out. If however artificial columns do end up in the
	 * final solution obtained when the Master problem terminates, we have a proof that the BAPNode is infeasible. Finally note that artificial columns are volatile: they are never passed from
	 * a parent node to any of its children!
	 *
	 * Note 1: This function is not invoked at the root node whenever a {@link #warmStart(int objectiveInitialSolution, List initialSolution) warmStart} is provided.
	 * Note 2: execution of this method is delayed as much as possible so safe computational effort.
	 * @param node node
	 * @return List of columns used to initialize the given BAPNode
	 */
	protected abstract List<U> generateInitialFeasibleSolution(BAPNode<T,U> node);

	/**
	 * Tests whether the given node has an integer solution
	 * @param node node
	 * @return Returns true if solution is an integer solution, false otherwise
	 */
	protected abstract boolean isIntegerNode(BAPNode<T,U> node);

	/**
	 * Test whether the given node can be pruned based on this bounds
	 * @param node node
	 * @return true if the node can be pruned
	 */
	protected boolean nodeCanBePruned(BAPNode<T,U> node){
		return (optimizationSenseMaster == OptimizationSense.MINIMIZE && Math.ceil(node.bound-config.PRECISION) >= upperBoundOnObjective ||
				optimizationSenseMaster == OptimizationSense.MAXIMIZE && Math.floor(node.bound+config.PRECISION) <= lowerBoundOnObjective);
	}

	/**
	 * Tests whether a given node has a feasible solution, i.e. that it does not have artificial columns
	 * @param node node
	 * @return Returns true if solution is infeasible.
	 */
	protected boolean isInfeasibleNode(BAPNode<T,U> node){
		for(U column : node.solution){
			if(column.isArtificialColumn)
				return true;
		}
		return false;
	}

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
	 * Destroy both the master problem and pricing problems. A CutHandler which has been provided to the Constructor will not be destroyed by this method.
	 */
	public void close(){
		master.close();
		pricingProblemManager.close();
	}


	//----------------------------- Listeners and Notifiers -----------------------------

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
	 * Adds a CGListener
	 * @param listener listener
	 */
	public void addColumnGenerationEventListener(CGListener listener){
		this.columnGenerationEventListeners.add(listener);
	}

	/**
	 * Removes a CGListener
	 * @param listener listener
	 */
	public void removeColumnGenerationEventListener(CGListener listener){
		this.columnGenerationEventListeners.add(listener);
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
					startEvent =new StartEvent(AbstractBranchAndPrice.this, dataModel.getName(), objectiveIncumbentSolution);
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
			PruneNodeEvent pruneNodeEvent=null;
			for(BAPListener listener : listeners){
				if(pruneNodeEvent==null)
					pruneNodeEvent=new PruneNodeEvent(AbstractBranchAndPrice.this, node, nodeBound, objectiveIncumbentSolution);
				listener.pruneNode(pruneNodeEvent);
			}
		}

		/**
		 * Fires a ProcessingNextNodeEvent
		 * @param node Node which will be processed
		 */
		public  void fireNextNodeEvent(BAPNode node){
			ProcessingNextNodeEvent processingNextNodeEvent=null;
			for(BAPListener listener : listeners){
				if(processingNextNodeEvent==null)
					processingNextNodeEvent=new ProcessingNextNodeEvent(AbstractBranchAndPrice.this, node, queue.size(), objectiveIncumbentSolution);
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
			TimeLimitExceededEvent timeLimitExceededEvent =null;
			for(BAPListener listener : listeners){
				if(timeLimitExceededEvent ==null)
					timeLimitExceededEvent =new TimeLimitExceededEvent(AbstractBranchAndPrice.this, node);
				listener.timeLimitExceeded(timeLimitExceededEvent);
			}
		}
	}
}
