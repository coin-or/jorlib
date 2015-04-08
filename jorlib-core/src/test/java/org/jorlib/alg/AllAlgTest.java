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
 * AllAlgTest.java
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
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	KnapsackTest.class,
	LiftedCoverInequalitySeparatorTest.class
})

public class AllAlgTest {
}
