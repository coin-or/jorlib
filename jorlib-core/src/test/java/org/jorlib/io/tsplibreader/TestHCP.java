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
 * Tests Hamiltonian cycle problem (HCP) instances.
 * 
 * @author David Hadka
 */
public final class TestHCP
{

    private static Set<String> instances;

    @BeforeClass
    public static void initializeInstances()
    {
        instances = new HashSet<>();
        instances.add("alb1000");
        instances.add("alb2000");
        instances.add("alb3000a");
        instances.add("alb3000b");
        instances.add("alb3000c");
        instances.add("alb3000d");
        instances.add("alb3000e");
        instances.add("alb4000");
        instances.add("alb5000");
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
            InputStream inputStream1 = getClass()
                .getClassLoader().getResourceAsStream("./tspLib/hcp/" + instance + ".hcp");
            if (inputStream1 == null)
                Assert.fail("Cannot find problem instance!");
            TSPLibInstance problem = new TSPLibInstance(inputStream1);
            Assert.assertEquals(DataType.HCP, problem.getDataType());

            InputStream inputStream2 = getClass()
                .getClassLoader().getResourceAsStream("./tspLib/hcp/" + instance + ".opt.tour");
            if (inputStream2 == null) { // No optimal tour file exists
                inputStream1.close();
                continue;
            }
            problem.addTour(inputStream2);
            for (TSPLibTour tour : problem.getTours()) {
                Assert.assertTrue(tour.isHamiltonianCycle(problem));
                Assert.assertTrue(tour.containsFixedEdges(problem));
            }
            inputStream1.close();
            inputStream2.close();
        }
    }

}
