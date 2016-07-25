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
 * SubtourSeparatorTest.java
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;

import junit.framework.TestCase;

/**
 * Unit tests for the SubtourSeparator class
 * 
 * @author Joris Kinable
 * @since April 9, 2015
 *
 */
public final class SubtourSeparatorTest extends TestCase{

	public static final double PRECISION=0.000001;
	
	/**
	 * Test 1 - Undirected, incomplete graph with a subtour.
	 */
	public void testUndirectedGraphWithSubtour(){
		//Define a new Undirected Graph. For simplicity we'll use a simple, unweighted graph, but in reality this class is mainly used
		//in combination with weighted graphs for TSP problems.
		Graph<Integer, DefaultEdge> undirectedGraph=new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
		Graphs.addAllVertices(undirectedGraph, Arrays.asList(1,2,3,4,5,6));
		undirectedGraph.addEdge(1, 2);
		undirectedGraph.addEdge(2, 3);
		undirectedGraph.addEdge(3, 4);
		undirectedGraph.addEdge(4, 1);
		undirectedGraph.addEdge(1, 5);
		undirectedGraph.addEdge(4, 5);
		undirectedGraph.addEdge(5, 6);
		undirectedGraph.addEdge(2, 6);
		undirectedGraph.addEdge(3, 6);
		
		//Define the x_e values for every edge e\in E
		Map<DefaultEdge, Double> edgeValueMap=new HashMap<DefaultEdge, Double>();
		edgeValueMap.put(undirectedGraph.getEdge(1,2), 0.0);
		edgeValueMap.put(undirectedGraph.getEdge(2,3), 1.0);
		edgeValueMap.put(undirectedGraph.getEdge(3,4), 0.0);
		edgeValueMap.put(undirectedGraph.getEdge(4,1), 1.0);
		edgeValueMap.put(undirectedGraph.getEdge(1,5), 1.0);
		edgeValueMap.put(undirectedGraph.getEdge(4,5), 1.0);
		edgeValueMap.put(undirectedGraph.getEdge(5,6), 0.0);
		edgeValueMap.put(undirectedGraph.getEdge(2,6), 1.0);
		edgeValueMap.put(undirectedGraph.getEdge(3,6), 1.0);
		
		//Invoke the separator
		SubtourSeparator<Integer, DefaultEdge> separator=new SubtourSeparator<Integer, DefaultEdge>(undirectedGraph);
		separator.separateSubtour(edgeValueMap);
		
		assertTrue(separator.hasSubtour());
		assertEquals(0, separator.getCutValue(), PRECISION);
		assertEquals(new HashSet<Integer>(Arrays.asList(2,3,6)), separator.getCutSet());
	}
	
	/**
	 * Test 2 - Undirected, incomplete graph without a subtour.
	 */
	public void testUndirectedGraphWithoutSubtour(){
		//Define a new Undirected Graph. For simplicity we'll use a simple, unweighted graph, but in reality this class is mainly used
		//in combination with weighted graphs for TSP problems.
		Graph<Integer, DefaultEdge> undirectedGraph=new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
		Graphs.addAllVertices(undirectedGraph, Arrays.asList(1,2,3,4,5,6));
		undirectedGraph.addEdge(1, 2);
		undirectedGraph.addEdge(2, 3);
		undirectedGraph.addEdge(3, 4);
		undirectedGraph.addEdge(4, 1);
		undirectedGraph.addEdge(1, 5);
		undirectedGraph.addEdge(4, 5);
		undirectedGraph.addEdge(5, 6);
		undirectedGraph.addEdge(2, 6);
		undirectedGraph.addEdge(3, 6);
		
		//Define the x_e values for every edge e\in E
		Map<DefaultEdge, Double> edgeValueMap=new HashMap<DefaultEdge, Double>();
		edgeValueMap.put(undirectedGraph.getEdge(1,2), 1.0);
		edgeValueMap.put(undirectedGraph.getEdge(2,3), 0.0);
		edgeValueMap.put(undirectedGraph.getEdge(3,4), 1.0);
		edgeValueMap.put(undirectedGraph.getEdge(4,1), 0.0);
		edgeValueMap.put(undirectedGraph.getEdge(1,5), 1.0);
		edgeValueMap.put(undirectedGraph.getEdge(4,5), 1.0);
		edgeValueMap.put(undirectedGraph.getEdge(5,6), 0.0);
		edgeValueMap.put(undirectedGraph.getEdge(2,6), 1.0);
		edgeValueMap.put(undirectedGraph.getEdge(3,6), 1.0);
		
		//Invoke the separator
		SubtourSeparator<Integer, DefaultEdge> separator=new SubtourSeparator<Integer, DefaultEdge>(undirectedGraph);
		separator.separateSubtour(edgeValueMap);
		
		assertFalse(separator.hasSubtour());
		assertEquals(2, separator.getCutValue(), PRECISION);
	}
	
	/**
	 * Test 3 - Directed, incomplete graph without a subtour.
	 */
	public void testDirectedGraphWithSubtour(){
		//Define a new Directed Graph. For simplicity we'll use a simple, unweighted graph.
		Graph<Integer, DefaultEdge> directedGraph=new SimpleDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);
		Graphs.addAllVertices(directedGraph, Arrays.asList(1,2,3,4,5,6));
		directedGraph.addEdge(1, 2);
		directedGraph.addEdge(2, 3);
		directedGraph.addEdge(3, 4);
		directedGraph.addEdge(4, 1);
		directedGraph.addEdge(1, 4);
		directedGraph.addEdge(1, 5);
		directedGraph.addEdge(5, 1);
		directedGraph.addEdge(4, 5);
		directedGraph.addEdge(5, 4);
		directedGraph.addEdge(6, 2);
		directedGraph.addEdge(3, 6);
		
		//Define the x_e values for every edge e\in E
		Map<DefaultEdge, Double> edgeValueMap=new HashMap<DefaultEdge, Double>();
		edgeValueMap.put(directedGraph.getEdge(1,2), 0.0);
		edgeValueMap.put(directedGraph.getEdge(2,3), 1.0);
		edgeValueMap.put(directedGraph.getEdge(3,4), 0.0);
		edgeValueMap.put(directedGraph.getEdge(4,1), 0.5);
		edgeValueMap.put(directedGraph.getEdge(1,4), 0.5);
		edgeValueMap.put(directedGraph.getEdge(1,5), 0.5);
		edgeValueMap.put(directedGraph.getEdge(5,1), 0.5);
		edgeValueMap.put(directedGraph.getEdge(4,5), 0.5);
		edgeValueMap.put(directedGraph.getEdge(5,4), 0.5);
		edgeValueMap.put(directedGraph.getEdge(6,2), 1.0);
		edgeValueMap.put(directedGraph.getEdge(3,6), 1.0);
		
		//Invoke the separator
		SubtourSeparator<Integer, DefaultEdge> separator=new SubtourSeparator<Integer, DefaultEdge>(directedGraph);
		separator.separateSubtour(edgeValueMap);
		
		assertTrue(separator.hasSubtour());
		assertEquals(0, separator.getCutValue(), PRECISION);
		assertEquals(new HashSet<Integer>(Arrays.asList(2,3,6)), separator.getCutSet());
	}
}
