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
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the distance functions against the cases provided in the official TSPLIB documentation.
 * 
 * @author David Hadka
 */
public final class DistanceFunctionTest
{

    @Test
    public void testPCB442()
        throws IOException, URISyntaxException
    {
        InputStream inputStream =
            getClass().getClassLoader().getResourceAsStream("./tspLib/tsp/pcb442.tsp");
        TSPLibInstance problem = new TSPLibInstance(inputStream);
        problem.addTour(TSPLibTour.createCanonicalTour(problem.getDimension()));
        Assert.assertEquals(221440, problem.getTours().get(0).distance(problem), 0.5);
        inputStream.close();
    }

    @Test
    public void testGR666()
        throws IOException, URISyntaxException
    {
        InputStream inputStream =
            getClass().getClassLoader().getResourceAsStream("./tspLib/tsp/gr666.tsp");
        TSPLibInstance problem = new TSPLibInstance(inputStream);
        problem.addTour(TSPLibTour.createCanonicalTour(problem.getDimension()));
        Assert.assertEquals(423710, problem.getTours().get(0).distance(problem), 0.5);
        inputStream.close();
    }

    @Test
    public void testATT532()
        throws IOException, URISyntaxException
    {
        InputStream inputStream =
            getClass().getClassLoader().getResourceAsStream("./tspLib/tsp/att532.tsp");
        TSPLibInstance problem = new TSPLibInstance(inputStream);
        problem.addTour(TSPLibTour.createCanonicalTour(problem.getDimension()));
        Assert.assertEquals(309636, problem.getTours().get(0).distance(problem), 0.5);
        inputStream.close();
    }

}
