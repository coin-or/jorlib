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
package org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.model;

import org.jorlib.frameworks.columnGeneration.model.ModelInterface;
import org.jorlib.io.tspLibReader.TSPLibInstance;
import org.jorlib.io.tspLibReader.TSPLibTour;
import org.jorlib.io.tspLibReader.graph.*;

import java.io.File;
import java.io.IOException;

/**
 * Defines a TSP problem. This is a simple wrapper class for a TSPLibInstance
 * For simplicity we assume that the problem is defined on a undirected, complete, weighted graph.
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 * 
 */
public class TSP implements ModelInterface{

	/** Number of vertices **/
	public final int N;
	/** TSP Lib instance **/
	public final TSPLibInstance tspLibInstance; //
	
	public TSP(String tspInstanceLocation) throws IOException{
		tspLibInstance= new TSPLibInstance(new File(tspInstanceLocation));
		this.N=tspLibInstance.getDimension();
	}

	/**
	 * Gets the weight of an edge (i,j)
	 * @param i vertex i
	 * @param j vertex j
	 * @return weight
	 */
	public int getEdgeWeight(int i, int j){
		return (int)tspLibInstance.getDistanceTable().getDistanceBetween(i, j);
	}

	/**
	 * Gets weight of an Edge
	 * @param edge edge
	 * @return weight
	 */
	public int getEdgeWeight(Edge edge){
		return (int)tspLibInstance.getDistanceTable().getDistanceBetween(edge.getId1(), edge.getId2());
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
