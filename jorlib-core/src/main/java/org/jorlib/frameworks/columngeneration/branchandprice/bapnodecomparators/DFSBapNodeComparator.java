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
package org.jorlib.frameworks.columngeneration.branchandprice.bapnodecomparators;

import org.jorlib.frameworks.columngeneration.branchandprice.BAPNode;
import org.jorlib.frameworks.columngeneration.colgenmain.AbstractColumn;
import org.jorlib.frameworks.columngeneration.model.ModelInterface;
import org.jorlib.frameworks.columngeneration.pricing.AbstractPricingProblem;

import java.util.Comparator;

/**
 * Simple comparator which processes the BAP tree in a DFS manner. The nodes are sorted based on
 * their nodeID. NOTE: this comparator is used by default. Alternative comparators have to be
 * specified explicitly.
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class DFSBapNodeComparator<T extends ModelInterface,U extends AbstractColumn<T, ? extends AbstractPricingProblem<T,U>>>
    implements Comparator<BAPNode<T,U>>
{
    @Override
    public int compare(BAPNode<T,U> o1, BAPNode<T,U> o2)
    {
        return -Integer.compare(o1.nodeID, o2.nodeID);
    }
}
