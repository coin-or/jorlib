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

import org.jorlib.io.tsplibreader.graph.Edge;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link Edge} class.
 * 
 * @author David Hadka
 */
public final class EdgeTest
{

    @Test
    public void testHasEndpoint()
    {
        Edge edge = new Edge(3, 5);

        Assert.assertTrue(edge.hasEndpoint(3));
        Assert.assertTrue(edge.hasEndpoint(5));
        Assert.assertFalse(edge.hasEndpoint(4));
    }

    @Test
    public void testGetOppositeEndpoint()
    {
        Edge edge = new Edge(3, 5);

        Assert.assertEquals(3, edge.getOppositeEndpoint(5));
        Assert.assertEquals(5, edge.getOppositeEndpoint(3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetOppositeEndpointError()
    {
        Edge edge = new Edge(3, 5);

        edge.getOppositeEndpoint(4);
    }

    @Test
    public void testHashCode1()
    {
        Edge edge1 = new Edge(3, 5);
        Edge edge2 = new Edge(5, 3);

        Assert.assertEquals(edge1.hashCode(), edge2.hashCode());
    }

    @Test
    public void testHashCode2()
    {
        Edge edge1 = new Edge(3, 5);
        Edge edge2 = new Edge(4, 3);

        Assert.assertNotEquals(edge1.hashCode(), edge2.hashCode());
    }

    @Test
    public void testEquals1()
    {
        Edge edge1 = new Edge(3, 5);
        Edge edge2 = new Edge(5, 3);

        Assert.assertTrue(edge1.equals(edge2));
        Assert.assertTrue(edge2.equals(edge1));
    }

    @Test
    public void testEquals2()
    {
        Edge edge1 = new Edge(3, 5);
        Edge edge2 = new Edge(4, 3);

        Assert.assertFalse(edge1.equals(edge2));
        Assert.assertFalse(edge2.equals(edge1));
    }

}
