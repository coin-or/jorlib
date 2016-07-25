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
package org.jorlib.frameworks;

import org.jorlib.frameworks.columnGeneration.tsp.BAPTSPTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * A TestSuite for all tests in this package.
 *
 * @author Joris Kinable
 * @since April 8, 2015
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	BAPTSPTest.class
})

public final class AllFrameworksTests {
}
