/* ==========================================
 * jORLib : a free Java OR library
 * ==========================================
 *
 * Project Info:  https://github.com/jkinable/jorlib
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under GPLv3
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
 * Created by jkinable on 5/5/15.
 */
public class BranchEvent extends EventObject{

    public final int nrBranches;
    public final BAPNode parentNode;
    public final List<BAPNode> childNodes;

    public BranchEvent(Object source, int nrBranches, BAPNode parentNode, List<BAPNode> childNodes){
        super(source);
        this.nrBranches=nrBranches;
        this.parentNode=parentNode;
        this.childNodes=childNodes;
    }
}
