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

import org.jorlib.frameworks.columngeneration.branchandprice.BAPNode;

import java.util.EventObject;

/**
 * Event generated when Branch-and-Price or column generation is terminated due to a time out
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class TimeLimitExceededEvent
    extends EventObject
{

    /** Branch-and-Price Node which was being processed when TimeLimitExceededEvent occurred **/
    public final BAPNode node;

    /**
     * Event fired when a time out occurs due to a time limit
     * 
     * @param source Generator of the event
     */
    public TimeLimitExceededEvent(Object source)
    {
        super(source);
        this.node = null;
    }

    /**
     * Event fired when a time out occurs due to a time limit
     * 
     * @param source Generator of the event
     * @param node node which was being processed when the time out occurred.
     */
    public TimeLimitExceededEvent(Object source, BAPNode node)
    {
        super(source);
        this.node = node;
    }
}
