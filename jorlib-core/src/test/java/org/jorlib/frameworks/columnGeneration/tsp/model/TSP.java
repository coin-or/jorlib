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
 * TSP.java
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
package org.jorlib.frameworks.columnGeneration.tsp.model;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jorlib.frameworks.columnGeneration.model.ModelInterface;
import org.jorlib.io.tspLibReader.TSPLibInstance;
import org.jorlib.io.tspLibReader.TSPLibTour;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Defines a TSP problem. This is a simple wrapper class for a TSPLibInstance
 * For simplicity we assume that the problem is defined on a undirected, complete, weighted graph.
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 * 
 */
public final class TSP extends SimpleWeightedGraph<Integer,DefaultWeightedEdge> implements ModelInterface{

	/** Number of vertices **/
	public final int N;
	/** TSP Lib instance **/
	protected final TSPLibInstance tspLibInstance; //
	
	public TSP(InputStream inputStream) throws IOException{
		super(DefaultWeightedEdge.class);
		tspLibInstance= new TSPLibInstance(inputStream);
		this.N=tspLibInstance.getDimension();

		//Create the graph for jGrapht
		for(int i=0; i<tspLibInstance.getDimension()-1; i++){
			for(int j=i+1; j<tspLibInstance.getDimension(); j++){
				Graphs.addEdgeWithVertices(this, i, j, tspLibInstance.getDistanceTable().getDistanceBetween(i, j));
			}
		}
	}

	/**
	 * Gets the length of a tour
	 * @param tour tour
	 * @return length
	 */
	public int getTourLength(TSPLibTour tour){
		return (int)tour.distance(tspLibInstance);
	}

	@Override
	public String getName() {
		return tspLibInstance.getName();
	}
}
