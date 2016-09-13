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
 * The geographical distance function. Node coordinates must be specified in latitude and longitude
 * positions of the form {@code DDD.MM} where {@code DDD} are the degrees and {@code MM} are the
 * minutes. Positive values indicate north/east and negative values indicate south/west. The
 * resulting distances are in kilometers.
 * 
 * @author David Hadka
 */
public class GeographicalDistance
    extends DistanceFunction
{

    /**
     * The constant PI used in the geographical distance calculations. This is used instead of
     * {@code Math.PI} to remain consistent with TSPLIB.
     */
    private static final double PI = 3.141592;

    /**
     * Constructs a new geographical distance function.
     */
    public GeographicalDistance()
    {
        super();
    }

    /**
     * Converts a latitude or longitude value in the form {@code DDD.MM} to its geographical angle
     * in radians.
     * 
     * @param x the latitude or longitude value in the form {@code DDD.MM}
     * @return the geographical angle in radians
     */
    public static double toGeographical(double x)
    {
        int deg = (int) (x);
        double min = x - deg;
        return PI * (deg + 5.0 * min / 3.0) / 180.0;
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

        double latitude1 = toGeographical(position1[0]);
        double latitude2 = toGeographical(position2[0]);
        double longitude1 = toGeographical(position1[1]);
        double longitude2 = toGeographical(position2[1]);
        double radius = 6378.388;
        double q1 = Math.cos(longitude1 - longitude2);
        double q2 = Math.cos(latitude1 - latitude2);
        double q3 = Math.cos(latitude1 + latitude2);

        return Math.floor(radius * Math.acos(0.5 * ((1.0 + q1) * q2 - (1.0 - q1) * q3)) + 1.0);
    }

}
