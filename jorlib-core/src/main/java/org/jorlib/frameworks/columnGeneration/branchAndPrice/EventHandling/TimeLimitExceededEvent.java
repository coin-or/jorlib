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
 * TimeLimitExceededEvent.java
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

import org.jorlib.frameworks.columnGeneration.branchAndPrice.BAPNode;

import java.util.EventObject;

/**
 * Event generated when Branch-and-Price or column generation is terminated due to a time out
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class TimeLimitExceededEvent extends EventObject{

    /** Branch-and-Price Node which was being processed when TimeLimitExceededEvent occurred **/
    public final BAPNode node;

    /**
     * Event fired when a time out occurs due to a time limit
     * @param source Generator of the event
     */
    public TimeLimitExceededEvent(Object source){
        super(source);
        this.node=null;
    }

    /**
     * Event fired when a time out occurs due to a time limit
     * @param source Generator of the event
     * @param node node which was being processed when the time out occurred.
     */
    public TimeLimitExceededEvent(Object source, BAPNode node){
        super(source);
        this.node=node;
    }
}
