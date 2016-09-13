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
package org.jorlib.io.tsplibreader.distanceFunctions;

import org.jorlib.io.tsplibreader.graph.Node;

/**
 * Abstract superclass of all distance function implementations. This class ensures the two nodes
 * provided to the {@link #distance(Node, Node)} method are compatible.
 * 
 * @author David Hadka
 */
public abstract class DistanceFunction
{

    /**
     * Constructs a new distance function.
     */
    public DistanceFunction()
    {
        super();
    }

    /**
     * Computes and returns the distance (or edge weight) between the two specified nodes.
     * 
     * @param node1 the first node
     * @param node2 the second node
     * @return the distance between the two nodes
     * @throws IllegalArgumentException if the nodes are not the same dimension
     */
    public double distance(Node node1, Node node2)
    {
        double[] position1 = node1.getPosition();
        double[] position2 = node2.getPosition();

        if (position1.length != position2.length) {
            throw new IllegalArgumentException("nodes are not the same dimension");
        }

        return distance(position1.length, position1, position2);
    }

    /**
     * Calculates and returns the distance between the two positions. Implementations should throw
     * an {@link IllegalArgumentException} if any preconditions fail.
     * 
     * @param length the length (or dimension) of the two positions
     * @param position1 the position of the first node
     * @param position2 the position of the second node
     * @return the distance between the two positions
     */
    public abstract double distance(int length, double[] position1, double[] position2);

}
