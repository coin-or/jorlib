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
 * AllIOTest.java
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
package org.jorlib.io;

import org.jorlib.io.tspLibReader.DistanceFunctionTest;
import org.jorlib.io.tspLibReader.EdgeTest;
import org.jorlib.io.tspLibReader.TestATSP;
import org.jorlib.io.tspLibReader.TestHCP;
import org.jorlib.io.tspLibReader.TestSOP;
import org.jorlib.io.tspLibReader.TestTSP;
import org.jorlib.io.tspLibReader.TestVRP;
import org.jorlib.io.tspLibReader.TourTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * A TestSuite for all tests in this package.
 *
 * @author Joris Kinable
 * @since April 24, 2015
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	DistanceFunctionTest.class,
	EdgeTest.class,
	TestATSP.class,
	TestHCP.class,
	TestSOP.class,
	TestTSP.class,
	TestVRP.class,
	TourTest.class
})

public final class AllIOTests {
}
