/* ==========================================
 * jORLib : Java Operations Research Library
 * ==========================================
 *
 * Project Info:  http://www.coin-or.org/projects/jORLib.xml
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015-2016, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 * as published by the Free Software Foundation.
 */
package org.jorlib.frameworks.columngeneration.model;

/**
 * Interface which should be implemented by any data model.
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public interface ModelInterface
{
    /**
     * Returns the name of the data model
     * 
     * @return the name of the data model
     */
    String getName();
}
