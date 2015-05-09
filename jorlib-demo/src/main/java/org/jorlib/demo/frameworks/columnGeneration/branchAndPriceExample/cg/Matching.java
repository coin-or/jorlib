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
 * Matching.java
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
package org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg;

import java.util.Arrays;
import java.util.Set;

import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.model.TSP;
import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblem;
import org.jorlib.io.tspLibReader.graph.Edge;


/**
 * Definition of a column. For the TSP example, each column is a perfect matching.
 *
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public class Matching extends AbstractColumn<TSP, Matching, PricingProblemByColor> {

	/**
	 * Creates a new column (matching)
	 * @param creator
	 * @param isArtificial
	 * @param associatedPricingProblem
	 * @param edges
	 * @param succ
	 * @param cost
	 */
	public Matching(String creator, boolean isArtificial,	PricingProblemByColor associatedPricingProblem,
			Set<Edge> edges,
			int[] succ,
			int cost) {
		super(creator, isArtificial, associatedPricingProblem);
		this.edges=edges;
		this.succ=succ;
		this.cost=cost;
	}

	//Edges in the matching
	public final Set<Edge> edges;
	//successor array. succ[2]=4 and succ[4]=2 models that edge (2,4) is contained in the matching.
	public final int[] succ;
	//Weighted cost of the matching
	public final int cost;
		

	@Override
	public boolean equals(Object o) {
		if(this==o)
			return true;
		else if(!(o instanceof Matching))
			return false;
		Matching other=(Matching) o;			
		return Arrays.equals(this.succ, other.succ) && this.isArtificialColumn == other.isArtificialColumn;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(succ);
	}

	@Override
	public String toString() {
		String s="Value: "+this.value+" cost: "+this.cost+" color: "+associatedPricingProblem.color+" artificial: "+isArtificialColumn+" edges: "+edges+" succ: "+Arrays.toString(succ);
		return s;
	}

}
