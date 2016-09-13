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
 * Tests vehicle routing problem (VRP) instances. This only tests if the instance loads correctly,
 * not for correctness.
 * 
 * @author David Hadka
 */
public final class TestVRP
{

    private static Set<String> instances;

    @BeforeClass
    public static void initializeInstances()
    {
        instances = new HashSet<String>();
        instances.add("att48");
        instances.add("eil7");
        instances.add("eil13");
        instances.add("eil22");
        instances.add("eil23");
        instances.add("eil30");
        instances.add("eil31");
        instances.add("eil33");
        instances.add("eil51");
        instances.add("eilA76");
        instances.add("eilA101");
        instances.add("eilB76");
        instances.add("eilB101");
        instances.add("eilC76");
        instances.add("eilD76");
        instances.add("gil262");
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
                .getClassLoader().getResourceAsStream("./tspLib/vrp/" + instance + ".vrp");
            if (inputStream == null)
                Assert.fail("Cannot find problem instance!");
            TSPLibInstance problem = new TSPLibInstance(inputStream);
            Assert.assertEquals(DataType.CVRP, problem.getDataType());
            inputStream.close();
        }
    }

}
