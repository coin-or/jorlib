/* ==========================================
 * jORLib : Java Operations Research Library
 * ==========================================
 *
 * Project Info:  http://www.coin-or.org/projects/jORLib.xml
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2016-2016, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 * as published by the Free Software Foundation.
 */
package org.jorlib.frameworks.columngeneration.branchandprice.bapnodecomparators;

import org.jorlib.frameworks.columngeneration.branchandprice.BAPNode;
import org.jorlib.frameworks.columngeneration.master.OptimizationSense;

import java.util.Comparator;

/**
 * Simple comparator which processes the BAP tree using the Best Bound strategy. In the Best Bound
 * strategy, the node with the lowest lower bound is selected first (in case of a minimization
 * problem). Alternatively, in case of the maximization problem the node with the largest upper
 * bound is selected first. In case of a draw, i.e., two nodes have the same bound, this class
 * behaves the same as the {@link DFSBapNodeComparator}. This strategy is mainly used to strengthen
 * the lower bound (minimization problem) resp., upper bound (maximization problem) as fast as
 * possible.
 *
 * @author Joris Kinable
 * @version 7-9-2016
 */
public class BestBoundBapNodeComparator
    implements Comparator<BAPNode>
{

    private final OptimizationSense optimizationSense;

    /**
     * Construct a new BestBoundBapNodeComparator. The behavior of this comparator depends on the
     * Optimization Sense of the optimization problem
     * 
     * @param optimizationSense Indicates whether the problem is a minimization or maximization
     *        problem
     */
    public BestBoundBapNodeComparator(OptimizationSense optimizationSense)
    {
        this.optimizationSense = optimizationSense;
    }

    @Override
    public int compare(BAPNode o1, BAPNode o2)
    {
        int boundComparison = Double.compare(o1.getBound(), o2.getBound());
        if (boundComparison == 0) // Bounds are equal
            return -1 * Integer.compare(o1.nodeID, o2.nodeID);
        else if (optimizationSense == OptimizationSense.MINIMIZE)
            return boundComparison;
        else // Maximization problem
            return -1 * boundComparison;

    }
}
