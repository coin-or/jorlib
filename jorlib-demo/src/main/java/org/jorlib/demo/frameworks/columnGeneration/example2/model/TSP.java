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
package org.jorlib.demo.frameworks.columnGeneration.example2.model;

/**
 * Defines a TSP problem.
 * For simplicity we assume that the problem is defined on a undirected, complete, weighted graph.
 * In addition we assume that N is even!
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 * 
 */
public class TSP {

	public final int N; //nr of vertices
	public final int[][] distanceMatrix; //distances between vertices
	
	public TSP(int N, int[][] distanceMatrix){
		this.N=N;
		this.distanceMatrix=distanceMatrix;
	}
	
	public TSP(){
		//Optimal tour: 229
		this.N=6;
		this.distanceMatrix=new int[][]{{0, 36, 32, 54, 20, 40},
											{36, 0, 22, 58, 54, 67},
											{32, 22, 0, 36, 42, 71},
											{54, 58, 36, 0, 50, 92},
											{20, 54, 42, 50, 0, 45},
											{40, 67, 71, 92, 45, 0}};
	}
}
