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
 * BranchEvent.java
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

import org.jorlib.frameworks.columnGeneration.branchAndPrice.AbstractBranchCreator;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.BAPNode;

import java.util.EventObject;
import java.util.List;

/**
 * Event invoked when new branches are created in the Branch-and-Price tree
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class BranchEvent extends EventObject{

    /** Number of branches created **/
    public final int nrBranches;
    /** Parent node **/
    public final BAPNode parentNode;
    /** List of child nodes **/
    public final List<BAPNode> childNodes;

    /**
     * Creates a new BranchEvent
     * @param source Generator of this event
     * @param nrBranches Number of branches created
     * @param parentNode Parent node
     * @param childNodes List of child nodes
     */
    public BranchEvent(Object source, int nrBranches, BAPNode parentNode, List<BAPNode> childNodes){
        super(source);
        this.nrBranches=nrBranches;
        this.parentNode=parentNode;
        this.childNodes=childNodes;
    }
}
