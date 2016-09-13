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

/**
 * The psuedo-Euclidean distance function used by the {@code ATT} TSPLIB problem instances.
 * 
 * @author David Hadka
 */
public class PseudoEuclideanDistance
    extends DistanceFunction
{

    /**
     * Constructs a new pseudo-Euclidean distance function.
     */
    public PseudoEuclideanDistance()
    {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @throws IllegalArgumentException if the nodes are not two dimensional
     */
    @Override
    public double distance(int length, double[] position1, double[] position2)
    {
        if (length != 2) {
            throw new IllegalArgumentException("nodes must be 2D");
        }

        double xd = position1[0] - position2[0];
        double yd = position1[1] - position2[1];
        double r = Math.sqrt((Math.pow(xd, 2.0) + Math.pow(yd, 2.0)) / 10.0);
        double t = Math.round(r);

        if (t < r) {
            return t + 1.0;
        } else {
            return t;
        }
    }

}
