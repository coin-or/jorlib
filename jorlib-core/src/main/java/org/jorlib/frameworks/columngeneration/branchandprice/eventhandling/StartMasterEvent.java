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
package org.jorlib.frameworks.columngeneration.branchandprice.eventhandling;

import java.util.EventObject;

/**
 * Event generated when master problem in column generation procedure is being solved
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class StartMasterEvent
    extends EventObject
{

    private static final long serialVersionUID = -6817299978004749122L;

    /** Indicates which iteration we are at in the column generation procedure **/
    public final int columnGenerationIteration;

    /**
     * Creates a new StartMasterEvent
     * 
     * @param source Generator of the event
     * @param columnGenerationIteration column generation iteration during which this event was
     *        fired
     */
    public StartMasterEvent(Object source, int columnGenerationIteration)
    {
        super(source);
        this.columnGenerationIteration = columnGenerationIteration;
    }
}
