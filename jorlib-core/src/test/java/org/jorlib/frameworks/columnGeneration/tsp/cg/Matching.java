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
package org.jorlib.frameworks.columnGeneration.tsp.cg;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;
import org.jorlib.frameworks.columnGeneration.tsp.model.TSP;

import java.util.Arrays;
import java.util.Set;


/**
 * Definition of a column. For the TSP example, each column is a perfect matching.
 *
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public final class Matching extends AbstractColumn<TSP, PricingProblemByColor> {

	/** Edges in the matching **/
	public final Set<DefaultWeightedEdge> edges;
	/** successor array. succ[2]=4 and succ[4]=2 models that edge (2,4) is contained in the matching. **/
	public final int[] succ;
	/** Weighted cost of the matching **/
	public final int cost;

	/**
	 * Creates a new column (matching)
	 * @param creator who created the matching
	 * @param isArtificial indicates whether its an artificial column
	 * @param associatedPricingProblem pricing problem for which the matching is created
	 * @param edges edges in the matching
	 * @param succ successor array
	 * @param cost cost of matching (sum of edge lengths)
	 */
	public Matching(String creator, boolean isArtificial,	PricingProblemByColor associatedPricingProblem,
			Set<DefaultWeightedEdge> edges,
			int[] succ,
			int cost) {
		super(associatedPricingProblem, isArtificial, creator);
		this.edges=edges;
		this.succ=succ;
		this.cost=cost;
	}

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
