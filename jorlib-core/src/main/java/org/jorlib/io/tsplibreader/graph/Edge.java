/* ==========================================
 * jORLib : Java Operations Research Library
 * ==========================================
 *
 * Project Info:  http://www.coin-or.org/projects/jORLib.xml
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2012-2016, by David Hadka and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 * as published by the Free Software Foundation.
 */
package org.jorlib.io.tsplibreader.graph;

/**
 * An edge between two nodes.
 * 
 * @author David Hadka
 */
public class Edge
{

    /**
     * The identifier of the first node.
     */
    private final int id1;

    /**
     * The identifier of the second node.
     */
    private final int id2;

    /**
     * Constructs an edge between two nodes.
     * 
     * @param id1 the identifier of the first node
     * @param id2 the identifier of the second node
     */
    public Edge(int id1, int id2)
    {
        super();
        this.id1 = id1;
        this.id2 = id2;
    }

    /**
     * Returns the identifier of the first node.
     * 
     * @return the identifier of the first node
     */
    public int getId1()
    {
        return id1;
    }

    /**
     * Returns the identifier of the second node.
     * 
     * @return the identifier of the second node
     */
    public int getId2()
    {
        return id2;
    }

    /**
     * Returns {@code true} if either endpoint of this edge is the specified node; {@code false}
     * otherwise.
     * 
     * @param id the identifier of the node
     * @return {@code true} if either endpoint of this edge is the specified node; {@code false}
     *         otherwis
     */
    public boolean hasEndpoint(int id)
    {
        return (id == id1) || (id == id2);
    }

    /**
     * Returns the opposite endpoint of this edge.
     * 
     * @param id the identifier of the one endpoint
     * @return the opposite endpoint of this edge
     * @throws IllegalArgumentException if this edge does not have the any endpoint with the
     *         specified identifier
     */
    public int getOppositeEndpoint(int id)
    {
        if (id == id1) {
            return id2;
        } else if (id == id2) {
            return id1;
        } else {
            throw new IllegalArgumentException("edge does not have endpoint " + id);
        }
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        // since the edge is undirected, hashCode must not depend on the order
        // of the identifier; this if condition ensures that hashCode is
        // consistent with the equals method
        if (id1 < id2) {
            result = prime * result + id1;
            result = prime * result + id2;
        } else {
            result = prime * result + id2;
            result = prime * result + id1;
        }

        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        Edge other = (Edge) obj;

        if ((id1 == other.id1) && (id2 == other.id2)) {
            return true;
        } else return (id1 == other.id2) && (id2 == other.id1);
    }

    @Override
    public String toString()
    {
        String sb = "(" +
                id1 +
                "," +
                id2 +
                ")";

        return sb;
    }
}
