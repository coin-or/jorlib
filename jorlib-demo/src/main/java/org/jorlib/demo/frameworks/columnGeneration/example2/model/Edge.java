package org.jorlib.demo.frameworks.columnGeneration.example2.model;

/**
 * Undirected edge
 * 
 * @author jkinable
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
