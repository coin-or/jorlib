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
 * AllTests.java
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
package org.jorlib;

import org.jorlib.alg.AllAlgTests;
import org.jorlib.frameworks.AllFrameworksTests;
import org.jorlib.io.AllIOTests;
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
	AllAlgTests.class,
	AllIOTests.class,
	AllFrameworksTests.class
})

public final class AllTests {

}
