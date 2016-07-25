/* ==========================================
 * jORLib : a free Java OR library
 * ==========================================
 *
 * Project Info:  https://github.com/jkinable/jorlib
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 *
 */
/* -----------------
 * StartPricingEvent.java
 * -----------------
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * Original Author:  Joris Kinable
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 *
 */
package org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling;

import java.util.EventObject;

/**
 * Event generated when column generation starts solving the pricing problems
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class StartPricingEvent extends EventObject{

    /** Indicates which iteration we are at in the column generation procedure **/
    public final int columnGenerationIteration;

    /**
     * Creates a new StartPricingEvent
     * @param source Generator of the event
     * @param columnGenerationIteration column generation iteration during which this event was fired
     */
    public StartPricingEvent(Object source, int columnGenerationIteration){
        super(source);
        this.columnGenerationIteration=columnGenerationIteration;
    }
}
