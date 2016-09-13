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

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link TSPLibTour} class.
 * 
 * @author David Hadka
 */
public final class TourTest
{

    @Test
    public void testIsEquivalent1()
    {
        TSPLibTour tour1 = TSPLibTour.createTour(1, 5, 2, 3, 4);
        TSPLibTour tour2 = TSPLibTour.createTour(2, 3, 4, 1, 5);

        Assert.assertTrue(tour1.isEquivalent(tour2));
        Assert.assertTrue(tour2.isEquivalent(tour1));
    }

    @Test
    public void testIsEquivalent2()
    {
        TSPLibTour tour1 = TSPLibTour.createTour(1, 5, 2, 3, 4);
        TSPLibTour tour2 = TSPLibTour.createTour(2, 5, 1, 4, 3);

        Assert.assertTrue(tour1.isEquivalent(tour2));
        Assert.assertTrue(tour2.isEquivalent(tour1));
    }

    @Test
    public void testIsEquivalent3()
    {
        TSPLibTour tour1 = TSPLibTour.createTour(1, 5, 2, 3, 4);
        TSPLibTour tour2 = TSPLibTour.createTour(2, 3, 1, 4, 5);

        Assert.assertFalse(tour1.isEquivalent(tour2));
        Assert.assertFalse(tour2.isEquivalent(tour1));
    }

    @Test
    public void testFromToArray()
    {
        int[] expected = new int[] { 1, 5, 2, 3, 4 };
        TSPLibTour tour = new TSPLibTour();
        tour.fromArray(expected);

        Assert.assertArrayEquals(expected, tour.toArray());
    }

    @Test
    public void testReverse1()
    {
        TSPLibTour tour = TSPLibTour.createCanonicalTour(5);
        tour.reverse(1, 3);

        Assert.assertArrayEquals(new int[] { 0, 3, 2, 1, 4 }, tour.toArray());
    }

    @Test
    public void testReverse2()
    {
        TSPLibTour tour = TSPLibTour.createCanonicalTour(5);
        tour.reverse(0, 4);

        Assert.assertArrayEquals(new int[] { 4, 3, 2, 1, 0 }, tour.toArray());
    }

    @Test
    public void testReverse3()
    {
        TSPLibTour tour = TSPLibTour.createCanonicalTour(5);
        tour.reverse(4, 0);

        Assert.assertArrayEquals(new int[] { 4, 1, 2, 3, 0 }, tour.toArray());
    }

    @Test
    public void testReverse4()
    {
        TSPLibTour tour = TSPLibTour.createCanonicalTour(5);
        tour.reverse(2, 2);

        Assert.assertArrayEquals(new int[] { 0, 1, 2, 3, 4 }, tour.toArray());
    }

}
