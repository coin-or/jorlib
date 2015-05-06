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

	public final int N; //nr of vertices
	public final TSPLibInstance tspLibInstance;
	
	public TSP(String tspInstanceLocation) throws IOException{
		tspLibInstance= new TSPLibInstance(new File(tspInstanceLocation));
		this.N=tspLibInstance.getDimension();
	}

	public int getEdgeWeight(int i, int j){
		return (int)tspLibInstance.getDistanceTable().getDistanceBetween(i, j);
	}
	public int getEdgeWeight(Edge edge){
		return (int)tspLibInstance.getDistanceTable().getDistanceBetween(edge.getId1(), edge.getId2());
	}

	public int getTourLength(TSPLibTour tour){
		return (int)tour.distance(tspLibInstance);
	}

	@Override
	public String getName() {
		return tspLibInstance.getName();
	}
}
