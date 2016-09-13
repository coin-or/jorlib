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
package org.jorlib.io.tsplibreader.fieldTypesAndFormats;

/**
 * Enumeration of the ways node coordinates can be specified.
 * 
 * @author David Hadka
 */
public enum NodeCoordType
{

    /**
     * Nodes are specified by coordinates in 2-D.
     */
    TWOD_COORDS(2),

    /**
     * Nodes are specified by coordinates in 3-D.
     */
    THREED_COORDS(3),

    /**
     * Nodes do not have associated coordinates.
     */
    NO_COORDS(-1);

    /**
     * The length (dimension) of the coordinates.
     */
    private final int length;

    /**
     * Constructs a new node coordinate enumeration.
     * 
     * @param length the length (dimension) of the coordinates
     */
    private NodeCoordType(int length)
    {
        this.length = length;
    }

    /**
     * Returns the length (dimension) of the coordinates.
     * 
     * @return the length (dimension) of the coordinates
     */
    public int getLength()
    {
        return length;
    }

}
