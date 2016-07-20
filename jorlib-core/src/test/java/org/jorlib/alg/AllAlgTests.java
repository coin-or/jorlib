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
 * AllAlgTests.java
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
package org.jorlib.alg;

import org.jorlib.alg.knapsack.*;
import org.jorlib.alg.knapsack.separation.*;
import org.jorlib.alg.packing.circlePacking.SmallestEnclosingCircleCalculatorTest;
import org.jorlib.alg.tsp.separation.SubtourSeparatorTest;
import org.junit.runner.*;
import org.junit.runners.*;

/**
 * A TestSuite for all tests in this package.
 *
 * @author Joris Kinable
 * @since April 8, 2015
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	KnapsackTest.class,
	LiftedCoverInequalitySeparatorTest.class,
	SmallestEnclosingCircleCalculatorTest.class,
	SubtourSeparatorTest.class
})

public final class AllAlgTests {
}
