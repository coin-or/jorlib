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
 * SubtourInequalityGenerator.java
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
package org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.master.cuts;

import ilog.concert.IloException;

import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import java.util.*;
import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jorlib.alg.tsp.separation.SubtourSeparator;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.Matching;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.master.TSPMasterData;

import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.model.MatchingColor;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.model.TSP;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutGenerator;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.Inequality;

import org.jorlib.io.tspLibReader.graph.Edge;


/**
 * Checks for violated subtour inequalities in the master problem. Any violated inqualities are added to the master problem.
 *
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public class SubtourInequalityGenerator extends CutGenerator<TSP, TSPMasterData> {

	//Graph which is used to calculate any violated subtour inequalities
	private final Graph<Integer, DefaultEdge> completeGraph;
	//We use the subtour separator provided in jORLib
	private final SubtourSeparator<Integer, DefaultEdge> separator;

	/**
	 * Creates a new subtour inequality generator
	 * @param modelData
	 */
	public SubtourInequalityGenerator(TSP modelData) {
		super(modelData);
		
		//Create a complete graph using the CompleteGraphGenerator in the JGraphT package
		completeGraph=new SimpleGraph<>(DefaultEdge.class);
		CompleteGraphGenerator<Integer, DefaultEdge> completeGenerator =new CompleteGraphGenerator<Integer, DefaultEdge>(modelData.N);
		completeGenerator.generateGraph(completeGraph, new IntegerVertexFactory(), null);
		//Create a subtour separator 
		separator=new SubtourSeparator<Integer, DefaultEdge>(completeGraph);
	}

	/**
	 * Generate inequalities
	 * @return Returns true if a violated inquality has been found
	 */
	@Override
	public boolean generateInqualities() {
		//Get the edge weights as a map
		double[][] edgeValues=masterData.getEdgeValues();
		Map<DefaultEdge,Double> edgeValueMap=new HashMap<>();
		for(int i=0; i<modelData.N-1; i++){
			for(int j=i+1; j<modelData.N; j++){
				edgeValueMap.put(completeGraph.getEdge(i, j), edgeValues[i][j]);
			}
		}

		//Check for violated subtours. When found, generate an inequality
		separator.separateSubtour(edgeValueMap);
		if(separator.hasSubtour()){
			Set<Integer> cutSet=separator.getCutSet();
			SubtourInequality inequality=new SubtourInequality(this, cutSet);
			this.addCut(inequality);
			return true;
		}
		return false;
	}

	/**
	 * If a violated inequality has been found add it to the master problem.
	 * @param subtourInequality
	 */
	private void addCut(SubtourInequality subtourInequality){
		if(masterData.subtourInequalities.containsKey(subtourInequality))
			throw new RuntimeException("Error, duplicate subtour cut is being generated! This cut should already exist in the master problem: "+subtourInequality);
		//Create the inequality in cplex
		try {
			IloLinearNumExpr expr=masterData.cplex.linearNumExpr();
			//Register the columns with this constraint.
			for(MatchingColor color : MatchingColor.values()){
				for(Matching matching: masterData.matchingVars.get(color).keyList()){
					//Test how many edges in the matching enter/leave the cutSet (edges with exactly one endpoint in the cutSet)
					int crossings=0;
					for(Edge edge: matching.edges){
						if(subtourInequality.cutSet.contains(edge.getId1()) ^ subtourInequality.cutSet.contains(edge.getId2()))
							crossings++;
					}
					if(crossings>0){
						IloNumVar var=masterData.matchingVars.get(color).get(matching);
						expr.addTerm(crossings, var);
					}
				}
			}
			IloRange subtourConstraint = masterData.cplex.addGe(expr, 2, "subtour");
			masterData.subtourInequalities.put(subtourInequality, subtourConstraint);
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add a subtour inequality to the master problem
	 * @param cut Inequality
	 */
	@Override
	public void addCut(Inequality cut) {
		if(!(cut instanceof SubtourInequality))
			throw new IllegalArgumentException("This CutGenerator can ONLY add SubtourInequalities");
		SubtourInequality subtourInequality=(SubtourInequality) cut;
		this.addCut(subtourInequality);
	}

	/**
	 * Retuns a list of inequalities that have been generated.
	 * @return Retuns a list of inequalities that have been generated.
	 */
	@Override
	public List<Inequality> getCuts() {
		return new ArrayList<>(masterData.subtourInequalities.keySet());
	}

	/**
	 * Close the generator
	 */
	@Override
	public void close() {} //Nothing to do here


	/**
	 * Simple factory class which produces integers as vertices
	 */
	private class IntegerVertexFactory implements VertexFactory<Integer>{
		private int counter=0;
		@Override
		public Integer createVertex() {
			return new Integer(counter++);
		}
		
	}
}
