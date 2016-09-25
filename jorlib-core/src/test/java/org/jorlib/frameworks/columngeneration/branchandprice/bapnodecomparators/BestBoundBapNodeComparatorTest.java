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
import org.jorlib.frameworks.columngeneration.colgenmain.AbstractColumn;
import org.jorlib.frameworks.columngeneration.master.OptimizationSense;
import org.jorlib.frameworks.columngeneration.model.ModelInterface;
import org.jorlib.frameworks.columngeneration.pricing.AbstractPricingProblem;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the BestBoundBapNodeComparator
 */
public final class BestBoundBapNodeComparatorTest
{

    @Test
    public <T extends ModelInterface, U extends AbstractColumn<T,? extends AbstractPricingProblem<T, U>>> void testBestBoundMinimization()
    {
        BAPNode<T, U> bapNode0 = new BAPNode<>(0, null, null, null, 4, null); // Node with bound equal to
                                                                        // 4
        BAPNode<T, U> bapNode1 = new BAPNode<>(1, null, null, null, 2, null); // Node with bound equal to
                                                                        // 2
        BAPNode<T, U> bapNode2 = new BAPNode<>(2, null, null, null, 4, null); // Node with bound equal to
                                                                        // 4

        BestBoundBapNodeComparator<T, U> comparator =
            new BestBoundBapNodeComparator<>(OptimizationSense.MINIMIZE);

        // Compare nodes with different bound
        Assert.assertEquals(-1, comparator.compare(bapNode1, bapNode0));
        Assert.assertEquals(1, comparator.compare(bapNode0, bapNode1));

        // Compare nodes with the same bound
        Assert.assertEquals(1, comparator.compare(bapNode0, bapNode2));
        Assert.assertEquals(-1, comparator.compare(bapNode2, bapNode0));
    }

    @Test
    public <T extends ModelInterface, U extends AbstractColumn<T,? extends AbstractPricingProblem<T, U>>> void testBestBoundMaximization()
    {
        BAPNode<T, U> bapNode0 = new BAPNode<>(0, null, null, null, 4, null); // Node with bound equal to
                                                                        // 4
        BAPNode<T, U> bapNode1 = new BAPNode<>(1, null, null, null, 2, null); // Node with bound equal to
                                                                        // 2
        BAPNode<T, U> bapNode2 = new BAPNode<>(2, null, null, null, 4, null); // Node with bound equal to
                                                                        // 4

        BestBoundBapNodeComparator<T, U> comparator =
            new BestBoundBapNodeComparator<>(OptimizationSense.MAXIMIZE);

        // Compare nodes with different bound
        Assert.assertEquals(1, comparator.compare(bapNode1, bapNode0));
        Assert.assertEquals(-1, comparator.compare(bapNode0, bapNode1));

        // Compare nodes with the same bound
        Assert.assertEquals(1, comparator.compare(bapNode0, bapNode2));
        Assert.assertEquals(-1, comparator.compare(bapNode2, bapNode0));
    }
}
