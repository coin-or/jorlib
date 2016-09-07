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

import org.jorlib.io.tsplibreader.DistanceFunctionTest;
import org.jorlib.io.tsplibreader.EdgeTest;
import org.jorlib.io.tsplibreader.TestATSP;
import org.jorlib.io.tsplibreader.TestHCP;
import org.jorlib.io.tsplibreader.TestSOP;
import org.jorlib.io.tsplibreader.TestTSP;
import org.jorlib.io.tsplibreader.TestVRP;
import org.jorlib.io.tsplibreader.TourTest;
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
