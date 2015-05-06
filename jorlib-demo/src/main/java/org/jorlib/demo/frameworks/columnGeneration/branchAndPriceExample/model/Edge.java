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
 * Edge.java
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

/**
 * Undirected edge
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public class Edge {

	public final int i;
	public final int j;
	
	public Edge(int i, int j){
		this.i=i;
		this.j=j;
	}
	
	@Override
	public boolean equals(Object o){
		if(this==o)
			return true;
		else if(!(o instanceof Edge))
			return false;
		Edge other=(Edge) o;
		return (this.i==other.i && this.j==other.j) || (this.i==other.j && this.j==other.i);
	}
	
	
	@Override
	public int hashCode(){
		return i+j;
	}
	
	@Override
	public String toString(){
		return "("+i+","+j+")";
	}
}
