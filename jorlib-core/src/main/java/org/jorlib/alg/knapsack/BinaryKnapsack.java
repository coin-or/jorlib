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
 * Knapsack.java
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
package org.jorlib.alg.knapsack;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Memory efficient Branch and bound implementation of knapsack. 
 * 
 * Solves the problem:<br>
 * {@code max \sum_i c_i x_i}<br>
 * {@code s.t. \sum_i a_i x_i <= b}<br>
 * {@code x_i binary}<br>
 * 
 * The implementation is memory efficient: it does not rely on large matrices.
 * The knapsack problem is solved as a binary tree problem. Each level of the tree corresponds to a specific item. Each time we branch on a particular node, two child-nodes 
 * are created, reflecting whether the item at the level of the child nodes is selected, or not. As a result, at most 2^n nodes, where n is the number of items, are created.
 * In practice, the number of generated nodes is significantly smaller, as the number of items one can choose depends on the knapsack weight. Furthermore, the search tree is pruned using
 * bounds.<p>
 * Consider replacing this implementation by a faster one such as MT2<br><br>
 * 
 * NOTE: All item weights, as well as the maxKnapsackWeight have to be integers. The item weights can be fractional, both positive and negative. Obviously, since this is a
 * maximization problem, items with a value smaller or equal to 0 are never selected.
 * 
 *
 * @author Joris Kinable
 * @since April 8, 2015
 */
public class BinaryKnapsack implements KnapsackAlgorithm{
	
	//Define the knapsack parameters
	private int nrItems; //number of items in the knapsack
	private int maxKnapsackWeight; //max allowed wait of the knapsack
	private double[] itemValues; //Values of the knapsack items
	private int[] itemWeights; //Weights of the knapsack items
	
	//Solution
	private double knapsackValue=0;
	private int knapsackWeight=0;
	private boolean[] knapsackItems;
	
	/**
	 * Calculates a greedy solution for the knapsack problem. This solution is a valid lower bound and is used for pruning.
	 * @param itemOrder Order in which the items are considered by the greedy algorithm. The items are sorted ascending, based on their value/weight ratio
	 */
	private void getGreedyKnapsackSolution(Integer[] itemOrder){
		double value=0;
		int remainingWeight=maxKnapsackWeight;
		boolean[] selectedItems=new boolean[nrItems];
		//Greedily take a single item until the knapsack is full
		for(int i=0; i<nrItems; i++){
			if(itemWeights[itemOrder[i]]<=remainingWeight){
				value+=itemValues[itemOrder[i]];
				remainingWeight-=itemWeights[itemOrder[i]];
				selectedItems[itemOrder[i]]=true;
			}
		}
		this.knapsackValue=value;
		this.knapsackWeight=maxKnapsackWeight-remainingWeight;
		this.knapsackItems=selectedItems;
	}
	
	
	/**
	 * Solve the knapsack problem.
	 * @param nrItems nr of items in the knapsack
	 * @param maxKnapsackWeight max size/weight of the knapsack
	 * @param itemValues item values
	 * @param itemWeights item weights
	 * @return The value of the knapsack solution
	 */
	public double solveKnapsackProblem(int nrItems, int maxKnapsackWeight, double[] itemValues, int[] itemWeights){
		//Initialize
		this.nrItems=nrItems;
		this.maxKnapsackWeight=maxKnapsackWeight;
		this.itemValues=itemValues;
		this.itemWeights=itemWeights;
		
		this.knapsackValue=0;
		this.knapsackWeight=0;
		
		Queue<KnapsackNode> queue=new PriorityQueue<>();
		
		//Define the order in which items will be processed. The items are sorted based on their value/weight ratio, thereby considering proportionally more valuable items first.
		Integer[] itemOrder=new Integer[nrItems];
		for(int i=0; i<nrItems; i++) itemOrder[i]=i;
		//Sort the times in ascending order, based on their value/weigth ratio
		Arrays.sort(itemOrder, new Comparator<Integer>() {
			@Override
			public int compare(Integer item1, Integer item2) {
				return -1*Double.compare(itemValues[item1]/itemWeights[item1], itemValues[item2]/itemWeights[item2]);
			}
		});
		
		
		//Create initial node
		KnapsackNode kn=new KnapsackNode(nrItems);
		kn.bound=calcBound(itemOrder, kn.level+1, maxKnapsackWeight-kn.weight, kn.value);
		queue.add(kn);
		
		//Get initial greedy solution
		this.getGreedyKnapsackSolution(itemOrder);
		
		//Maintain a reference to the best node
		double bestValue=this.knapsackValue;
		KnapsackNode bestNode=null;
		
		while(!queue.isEmpty()){
			kn=queue.poll();
			if(kn.bound>bestValue && kn.level<nrItems-1){
				kn.level++;
				//Create 2 new nodes, one where item <itemToAdd> is used, and one where item <itemToAdd> is skipped.
				int itemToAdd=itemOrder[kn.level];
				if(kn.weight+itemWeights[itemToAdd]<=maxKnapsackWeight && itemValues[itemToAdd]>0){ //Check whether we can add the next item and whether its value is positive
					KnapsackNode knCopy=kn.copy();
					knCopy.addItem(itemToAdd, itemWeights[itemToAdd], itemValues[itemToAdd]);
					knCopy.bound=calcBound(itemOrder, knCopy.level+1, maxKnapsackWeight-knCopy.weight, knCopy.value);
					
					if(knCopy.value>bestValue){
						bestValue=knCopy.value;
						bestNode=knCopy;
					}
					queue.add(knCopy);
				}
				//Dont use item[kn.level]
				kn.bound=calcBound(itemOrder, kn.level+1, maxKnapsackWeight-kn.weight, kn.value);
				queue.add(kn);
			}
		}
		
		//Update the results based on the best solution found
		if(bestNode!=null){ 
			this.knapsackValue=bestNode.value;
			this.knapsackWeight=bestNode.weight;
			this.knapsackItems=bestNode.selectedItems;
		}
		
		return this.knapsackValue;
	}
	
	/**
	 * Calculate a bound on the best solution attainable for a given partial solution. 
	 * @return bound on the best value attainable  by the partially filled knapsack
	 */
	private double calcBound(Integer[] itemOrder, int level, int remainingSize, double value){
		double bound=value;
		while(level<nrItems && remainingSize-itemWeights[itemOrder[level]]>=0){
			remainingSize-=itemWeights[itemOrder[level]];
			bound+=itemValues[itemOrder[level]];
			level++;
		}
		if(level<nrItems){
			bound+=itemValues[itemOrder[level]]*(remainingSize/(double)itemWeights[itemOrder[level]]);
		}
		return bound;
	}
	
	/**
	 * Get the value of the knapsack
	 * @return Get the value of the knapsack
	 */
	public double getKnapsackValue(){
		return knapsackValue;
	}
	/**
	 * Get the total weight of the knapsack
	 * @return Get the total weight of the knapsack
	 */
	public int getKnapsackWeight(){
		return knapsackWeight;
	}
	/**
	 * Get the items in the knapsack
	 * @return Get the items in the knapsack
	 */
	public boolean[] getKnapsackItems(){
		return knapsackItems;
	}
	
	/**
	 * Knapsack nodes represent partial solutions for the knapsack problem. A subset of the variables, starting from the root node of the tree up to <level> level,
	 * have been fixed. 
	 * @author jkinable
	 *
	 */
	private final class KnapsackNode implements Comparable<KnapsackNode>{
		public final int nrItems; //Max number of items in the problem
		public final boolean[] selectedItems; //Selected items
		public int level; //Depth of the knapsack node in the search tree; Each level of the search tree corresponds with a single item.
		public double bound; //Bound on the optimum value atainable by this node
		public double value; //Total value of the items in this knapsack 
		public int weight; //Total weight of the items in this knapsack
		
		public KnapsackNode(int nrItems){
			this.nrItems=nrItems;
			selectedItems=new boolean[nrItems];
			this.bound=0;
			this.value=0;
			this.weight=0;
			this.level=-1;
		}
		
		public KnapsackNode(int nrItems, int level, double bound, double value, int weight, boolean[] selectedItems) {
			this.nrItems=nrItems;
			this.level=level;
			this.bound=bound;
			this.value=value;
			this.weight=weight;
			this.selectedItems=selectedItems;
		}
	
		public KnapsackNode copy(){
			boolean[] selectedItemsCopy=new boolean[nrItems];
			System.arraycopy(selectedItems, 0, selectedItemsCopy, 0, nrItems);
			return new KnapsackNode(nrItems, this.level, this.bound, this.value, this.weight, selectedItemsCopy);
		}
		
		public void addItem(int itemID, int itemWeight, double itemValue){
			selectedItems[itemID]=true;
			weight+=itemWeight;
			value+=itemValue;
		}
		
		@Override
		public int compareTo(KnapsackNode otherNode) {
			if(this.bound==otherNode.bound)
				return 0;
			else if(this.bound>otherNode.bound)
				return -1;
			else
				return 1;
		}
		
		public String toString(){
			return "Level: "+level+" bound: "+bound+" value: "+value+" weight: "+weight+" items: "+Arrays.toString(selectedItems)+" \n";
		}
	}
}

