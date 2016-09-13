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
 * Enumeration of the various formats in which edge weights (distances) can be specified.
 * 
 * @author David Hadka
 */
public enum EdgeWeightFormat
{

    /**
     * Weights are given by a function.
     */
    FUNCTION,

    /**
     * Weights are given by a full matrix.
     */
    FULL_MATRIX,

    /**
     * Row-wise upper triangular matrix (excluding diagonal).
     */
    UPPER_ROW,

    /**
     * Row-wise lower triangular matrix (excluding diagonal).
     */
    LOWER_ROW,

    /**
     * Row-wise upper triangular matrix.
     */
    UPPER_DIAG_ROW,

    /**
     * Row-wise lower triangular matrix.
     */
    LOWER_DIAG_ROW,

    /**
     * Column-wise upper triangular matrix (without diagonal).
     */
    UPPER_COL,

    /**
     * Column-wise lower triangular matrix (without diagonal).
     */
    LOWER_COL,

    /**
     * Column-wise upper triangular matrix.
     */
    UPPER_DIAG_COL,

    /**
     * Column-wise lower triangular matrix.
     */
    LOWER_DIAG_COL

}
