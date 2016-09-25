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
 * Event generated when Branch-and-Price or column generation finishes
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class FinishEvent
    extends EventObject
{

    private static final long serialVersionUID = -1771357959981692682L;

    /**
     * Creates a new FinishEvent
     * 
     * @param source Generator of the event
     */
    public FinishEvent(Object source)
    {
        super(source);
    }
}
