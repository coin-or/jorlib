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
package org.jorlib.frameworks;

import org.jorlib.frameworks.columngeneration.branchandprice.bapnodecomparators.BFSBapNodeComparatorTest;
import org.jorlib.frameworks.columngeneration.branchandprice.bapnodecomparators.BestBoundBapNodeComparatorTest;
import org.jorlib.frameworks.columngeneration.branchandprice.bapnodecomparators.DFSBapNodeComparatorTest;
import org.jorlib.frameworks.columngeneration.tsp.BAPTSPTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * A TestSuite for all tests in this package.
 *
 * @author Joris Kinable
 * @since April 8, 2015
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ BAPTSPTest.class, BestBoundBapNodeComparatorTest.class,
    BFSBapNodeComparatorTest.class, DFSBapNodeComparatorTest.class })

public final class AllFrameworksTests
{
}
