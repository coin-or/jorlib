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
 * Enumeration of the formats in which edge data is specified.
 * 
 * @author David Hadka
 */
public enum EdgeDataFormat
{

    /**
     * The graph is specified by an edge list.
     */
    EDGE_LIST,

    /**
     * The graph is specified by an adjacency list.
     */
    ADJ_LIST

}
