/* ==========================================
 * jORLib : Java Operations Research Library
 * ==========================================
 *
 * Project Info:  http://www.coin-or.org/projects/jORLib.xml
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2012-2016, by David Hadka and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 * as published by the Free Software Foundation.
 */
package org.jorlib.io.tsplibreader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.jorlib.io.tsplibreader.fieldtypesandformats.DataType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests asymmetric traveling salesman problem (ATOP) instances. This only tests if the instances
 * loaded without error, not for correctness.
 * 
 * @author David Hadka
 */
public final class TestATSP
{

    private static Set<String> instances;

    @BeforeClass
    public static void initializeInstances()
    {
        instances = new HashSet<>();
        instances.add("br17");
        instances.add("ft53");
        instances.add("ft70");
        instances.add("ftv33");
        instances.add("ftv35");
        instances.add("ftv38");
        instances.add("ftv44");
        instances.add("ftv47");
        instances.add("ftv55");
        instances.add("ftv64");
        instances.add("ftv70");
        instances.add("ftv170");
        instances.add("kro124p");
        instances.add("p43");
        instances.add("rbg323");
        instances.add("rbg358");
        instances.add("rbg403");
        instances.add("rbg443");
        instances.add("ry48p");
    }

    @AfterClass
    public static void freeInstances()
    {
        instances = null;
    }

    @Test
    public void testLoad()
        throws IOException
    {
        for (String instance : instances) {
            InputStream inputStream = getClass()
                .getClassLoader().getResourceAsStream("./tspLib/atsp/" + instance + ".atsp");
            if (inputStream == null)
                Assert.fail("Cannot find problem instance!");
            TSPLibInstance problem = new TSPLibInstance(inputStream);
            Assert.assertEquals(DataType.ATSP, problem.getDataType());
            inputStream.close();
        }
    }

}
