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
package org.jorlib.io.tsplibreader.fieldtypesandformats;

/**
 * Enumeration of the supported data types.
 * 
 * @author David Hadka
 */
public enum DataType
{

    /**
     * Data for a symmetric traveling salesman problem.
     */
    TSP,

    /**
     * Data for an asymmetric traveling salesman problem.
     */
    ATSP,

    /**
     * Data for a sequential ordering problem.
     */
    SOP,

    /**
     * Hamiltonian cycle problem data.
     */
    HCP,

    /**
     * Capacitated vehicle routing problem data.
     */
    CVRP,

    /**
     * A collection of tours.
     */
    TOUR

}
