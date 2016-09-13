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
 * Enumeration of ways a graphical display can be generated from the data.
 * 
 * @author David Hadka
 */
public enum DisplayDataType
{

    /**
     * The display is generated from the node coordinates.
     */
    COORD_DISPLAY,

    /**
     * Explicit coordinates in 2-D are given.
     */
    TWOD_DISPLAY,

    /**
     * No graphical display is available.
     */
    NO_DISPLAY

}
