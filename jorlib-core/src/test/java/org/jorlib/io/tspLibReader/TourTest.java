/* Copyright 2012 David Hadka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package org.jorlib.io.tspLibReader;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link Tour} class.
 * 
 * @author David Hadka
 */
public class TourTest {
	
	@Test
	public void testIsEquivalent1() {
		Tour tour1 = Tour.createTour(1, 5, 2, 3, 4);
		Tour tour2 = Tour.createTour(2, 3, 4, 1, 5);
		
		Assert.assertTrue(tour1.isEquivalent(tour2));
		Assert.assertTrue(tour2.isEquivalent(tour1));
	}
	
	@Test
	public void testIsEquivalent2() {
		Tour tour1 = Tour.createTour(1, 5, 2, 3, 4);
		Tour tour2 = Tour.createTour(2, 5, 1, 4, 3);
		
		Assert.assertTrue(tour1.isEquivalent(tour2));
		Assert.assertTrue(tour2.isEquivalent(tour1));
	}
	
	@Test
	public void testIsEquivalent3() {
		Tour tour1 = Tour.createTour(1, 5, 2, 3, 4);
		Tour tour2 = Tour.createTour(2, 3, 1, 4, 5);
		
		Assert.assertFalse(tour1.isEquivalent(tour2));
		Assert.assertFalse(tour2.isEquivalent(tour1));
	}
	
	@Test
	public void testFromToArray() {
		int[] expected = new int[] { 1, 5, 2, 3, 4 };
		Tour tour = new Tour();
		tour.fromArray(expected);
		
		Assert.assertArrayEquals(expected, tour.toArray());
	}
	
	@Test
	public void testReverse1() {
		Tour tour = Tour.createCanonicalTour(5);
		tour.reverse(1, 3);
		
		Assert.assertArrayEquals(new int[] { 1, 4, 3, 2, 5 }, tour.toArray());
	}
	
	@Test
	public void testReverse2() {
		Tour tour = Tour.createCanonicalTour(5);
		tour.reverse(0, 4);
		
		Assert.assertArrayEquals(new int[] { 5, 4, 3, 2, 1 }, tour.toArray());
	}
	
	@Test
	public void testReverse3() {
		Tour tour = Tour.createCanonicalTour(5);
		tour.reverse(4, 0);
		
		Assert.assertArrayEquals(new int[] { 5, 2, 3, 4, 1 }, tour.toArray());
	}
	
	@Test
	public void testReverse4() {
		Tour tour = Tour.createCanonicalTour(5);
		tour.reverse(2, 2);
		
		Assert.assertArrayEquals(new int[] { 1, 2, 3, 4, 5 }, tour.toArray());
	}

}
