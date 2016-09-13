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
import java.util.List;

/**
 * Event invoked when new branches are created in the Branch-and-Price tree
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class BranchEvent
    extends EventObject
{

    /** Number of branches created **/
    public final int nrBranches;
    /** Parent node **/
    public final BAPNode parentNode;
    /** List of child nodes **/
    public final List<BAPNode> childNodes;

    /**
     * Creates a new BranchEvent
     * 
     * @param source Generator of this event
     * @param nrBranches Number of branches created
     * @param parentNode Parent node
     * @param childNodes List of child nodes
     */
    public BranchEvent(Object source, int nrBranches, BAPNode parentNode, List<BAPNode> childNodes)
    {
        super(source);
        this.nrBranches = nrBranches;
        this.parentNode = parentNode;
        this.childNodes = childNodes;
    }
}
