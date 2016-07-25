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
 * SubtourSeparator.java
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
package org.jorlib.alg.tsp.separation;

import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.StoerWagnerMinimumCut;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 * This class separates subtours. These subtours may be used to generate Dantzig Fulkerson Johnson (DFJ) subtour elimination constraints.
 * Let {@code G(V,E)} be a undirected graph with vertex set {@code V}, edge set {@code E}. A valid TSP solution (i.e. a solution without subtours) should satisfy
 * the following constraint: {@code \sum_{e\in \delta{S}} x_e >=2} for all {@code S\subset V, S \noteq \emptyset}. Here {@code \delta{S}\subset E} is the set of
 * edges where each edge has exactly one endpoint in {@code S}, and one endpoint in {@code V\setminus S}. {@code x_e} is a binary variable indicating
 * whether edge {@code e\in E} is used in the TSP solution. Obviously, if there is a set {@code S'\subset V, S' \noteq \emptyset} such that
 * {@code \sum_{e\in \delta{S'}} x_e <2}, then there is a subtour through the vertices in set S'. It may be the case that multiple subtours exist
 * within a fractional TSP solution. This class identifies the subtour with the highest amount of violation, i.e 
 * {@code \min_{S\subset V, S \noteq \emptyset} \sum_{e\in \delta{S}} x_e}<br><p>
 * 
 * Note: the graph must be provided as a JgraphT graph. The graph representing the problem can be directed, undirected, or mixed,
 * complete or incomplete, weighted or without weights. The directed graphs are often useful to model cases where a vehicle can only
 * drive from one city to the other in a particular direction.<p>
 * Note2: To separate the subtours, we rely on the StoerWagnerMinimumCut implementation from the JgraphT package. 
 * 		This implementation deterministically computes the minimum cut in a graph in {@code O(|V||E| + |V|log|V|)} time, see
 * 		{@literal M. Stoer and F. Wagner, "A Simple Min-Cut Algorithm", Journal of the ACM, volume 44, number 4. pp 585-591, 1997.}<p>
 * 
 * WARNING: if the input graph is modified, i.e. edges or vertices are added/removed then the behavior of this class is undefined!
 * 			A new instance should of this class should be made if this happens! A future extension of this class could add a graph
 * 			listener.
 * 
 * @author Joris Kinable
 * @since April 9, 2015
 *
 */
public class SubtourSeparator<V, E> {

	/** Precision 0.000001**/
	public static final double PRECISION=0.000001;
	
	//Solution
	private double minCutValue=-1;
	private boolean hasSubtour=false;
	private Set<V> cutSet;
	
	private Graph<V,E> inputGraph; //Original graph which defines the TSP problem
	private SimpleWeightedGraph<V, DefaultWeightedEdge> workingGraph; //Undirected graph
	
	/**
	 * This method instantiates the Subtour Separator. The input can be any type of graph: directed, undirected, or mixed,
	 * complete or incomplete, weighted or without weights. Internally, this class converts the given graph to a undirected graph. 
	 * Multiple edges between two vertices i,j, for example two direct arc (i,j) and (j,i)) are aggregated into in undirected edge (i,j).
	 * WARNING: if the input graph is modified, i.e. edges or vertices are added/removed then the behavior of this class is undefined!
	 * 			A new instance should of this class should be made if this happens!
	 * @param inputGraph input graph
	 */
	public SubtourSeparator(Graph<V,E> inputGraph){
		this.inputGraph=inputGraph;
		this.workingGraph=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(workingGraph, inputGraph.vertexSet());
		for(E edge : inputGraph.edgeSet())
			Graphs.addEdge(workingGraph, inputGraph.getEdgeSource(edge), inputGraph.getEdgeTarget(edge),0);
	}
	
	/**
	 * Starts the subtour separation.
	 * @param edgeValueMap Mapping of edges to their corresponding values, i.e. the x_e variable values for all e \in E. It suffices to provide the values
	 *                     of the non-zero edges. All other edges are presumed to have the value 0.
	 */
	public void separateSubtour(Map<E, Double> edgeValueMap){
		//Update the weights of our working graph
		//a. Reset all weights to zero
		for(DefaultWeightedEdge edge : workingGraph.edgeSet())
			workingGraph.setEdgeWeight(edge, 0);

		//b. Update the edge values with the values supplied in the edgeValueMap
		for(Map.Entry<E, Double> entry : edgeValueMap.entrySet()){
			if(entry.getValue() > PRECISION){
				V i=inputGraph.getEdgeSource(entry.getKey());
				V j=inputGraph.getEdgeTarget(entry.getKey());
				DefaultWeightedEdge edge = workingGraph.getEdge(i,j);
				workingGraph.setEdgeWeight(edge, entry.getValue() + workingGraph.getEdgeWeight(edge));
			}
		}
		//Compute the min cut in the graph
		//WARNING: The StoerWagnerMinimumCut class copies the workingGraph each time it is invoked! This is expensive and may be avoided.
		StoerWagnerMinimumCut<V,DefaultWeightedEdge> mc= new StoerWagnerMinimumCut<>(workingGraph);
		minCutValue=mc.minCutWeight();
		cutSet=mc.minCut();
		
		//If the cut value is smaller than 2, a subtour constraint has been violated
		hasSubtour= minCutValue<2-PRECISION;
	}

	/**
	 * Returns whether a subtour exists in the fractional TSP solution
	 * @return whether a subtour exists in the fractional TSP solution
	 */
	public boolean hasSubtour(){
		return hasSubtour;
	}
	
	/**
	 * Returns \sum_{e\in \delta{S'}} x_e for the separated subtour through S'.
	 * @return \sum_{e\in \delta{S'}} x_e for the separated subtour through S'.
	 */
	public double getCutValue(){
		return minCutValue;
	}
	
	/**
	 * Returns the set S' where {@code \sum_{e\in \delta{S'}} x_e <2, S'\subset V, S' \noteq \emptyset}
	 * @return the set S' where {@code \sum_{e\in \delta{S'}} x_e <2, S'\subset V, S' \noteq \emptyset}
	 */
	public Set<V> getCutSet(){
		return cutSet;
	}
}
