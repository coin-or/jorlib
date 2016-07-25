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
 * StartGeneratingCutsEvent.java
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
 * Event generated when the cut handler starts separating violated inequalities
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class StartGeneratingCutsEvent extends EventObject{

    /**
     * Creates a new StartGeneratingCutsEvent
     * @param source Generator of the event
     */
    public StartGeneratingCutsEvent(Object source){
        super(source);
    }
}
