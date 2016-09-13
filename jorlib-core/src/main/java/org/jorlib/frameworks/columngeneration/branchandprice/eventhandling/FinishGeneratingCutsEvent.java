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

import org.jorlib.frameworks.columngeneration.master.cutGeneration.AbstractInequality;

import java.util.EventObject;
import java.util.List;

/**
 * Event generated when the cut handler finished separating violated inequalities
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class FinishGeneratingCutsEvent
    extends EventObject
{

    public final List<AbstractInequality> separatedInequalities;

    /**
     * Creates a new FinishGeneratingCutsEvent
     * 
     * @param source Generator of the event
     * @param separatedInequalities List of newly separated inqualities
     */
    public FinishGeneratingCutsEvent(Object source, List<AbstractInequality> separatedInequalities)
    {
        super(source);
        this.separatedInequalities = separatedInequalities;
    }
}
