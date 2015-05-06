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

import java.util.EventObject;

/**
 * Created by jkinable on 5/5/15.
 */
public class BranchEvent extends EventObject{

    public final AbstractBranchCreator branchCreator;
    public final int nrBranches;

    public BranchEvent(Object source, AbstractBranchCreator branchCreator, int nrBranches){
        super(source);
        this.branchCreator=branchCreator;
        this.nrBranches=nrBranches;
    }
}
