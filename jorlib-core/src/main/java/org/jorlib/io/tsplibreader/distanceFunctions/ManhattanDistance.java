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
 * The Manhattan distance function.
 * 
 * @author David Hadka
 */
public class ManhattanDistance
    extends DistanceFunction
{

    /**
     * Constructs a new Manhattan distance function.
     */
    public ManhattanDistance()
    {
        super();
    }

    @Override
    public double distance(int length, double[] position1, double[] position2)
    {
        double result = 0.0;

        for (int i = 0; i < length; i++) {
            result += Math.abs(position1[i] - position2[i]);
        }

        return Math.round(result);
    }

}
