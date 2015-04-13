package org.jorlib.demo.frameworks.columnGeneration.example2.model;

/**
 * Define a TSP problem.
 * For simplicity we assume that the problem is defined on a undirected, complete, weighted graph.
 * In addition we assume that N is even!
 */
public class TSP {

	public final int N; //nr of vertices
	public final int[][] distanceMatrix; //distances between vertices
	
	public TSP(int N, int[][] distanceMatrix){
		this.N=N;
		this.distanceMatrix=distanceMatrix;
	}
}
