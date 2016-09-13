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

import org.jorlib.io.tsplibreader.fieldTypesAndFormats.DataType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests sequential ordering problem (SOP) instances. This only tests if the instances load without
 * error, not for correctness.
 * 
 * @author David Hadka
 */
public final class TestSOP
{

    private static Set<String> instances;

    @BeforeClass
    public static void initializeInstances()
    {
        instances = new HashSet<String>();
        instances.add("br17.10");
        instances.add("br17.12");
        instances.add("ESC07");
        instances.add("ESC11");
        instances.add("ESC12");
        instances.add("ESC25");
        instances.add("ESC47");
        instances.add("ESC63");
        instances.add("ESC78");
        instances.add("ft53.1");
        instances.add("ft53.2");
        instances.add("ft53.3");
        instances.add("ft53.4");
        instances.add("ft70.1");
        instances.add("ft70.2");
        instances.add("ft70.3");
        instances.add("ft70.4");
        instances.add("kro124p.1");
        instances.add("kro124p.2");
        instances.add("kro124p.3");
        instances.add("kro124p.4");
        instances.add("p43.1");
        instances.add("p43.2");
        instances.add("p43.3");
        instances.add("p43.4");
        instances.add("prob.42");
        instances.add("prob.100");
        instances.add("rbg048a");
        instances.add("rbg050c");
        instances.add("rbg109a");
        instances.add("rbg150a");
        instances.add("rbg174a");
        instances.add("rbg253a");
        instances.add("rbg323a");
        instances.add("rbg341a");
        instances.add("rbg358a");
        instances.add("rbg378a");
        instances.add("ry48p.1");
        instances.add("ry48p.2");
        instances.add("ry48p.3");
        instances.add("ry48p.4");

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
                .getClassLoader().getResourceAsStream("./tspLib/sop/" + instance + ".sop");
            if (inputStream == null)
                Assert.fail("Cannot find problem instance!");
            TSPLibInstance problem = new TSPLibInstance(inputStream);
            Assert.assertEquals(DataType.SOP, problem.getDataType());
            inputStream.close();
        }
    }

}
