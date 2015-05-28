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

import org.jorlib.io.tspLibReader.graph.Edge;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link Edge} class.
 * 
 * @author David Hadka
 */
public final class EdgeTest {
	
	@Test
	public void testHasEndpoint() {
		Edge edge = new Edge(3, 5);
		
		Assert.assertTrue(edge.hasEndpoint(3));
		Assert.assertTrue(edge.hasEndpoint(5));
		Assert.assertFalse(edge.hasEndpoint(4));
	}
	
	@Test
	public void testGetOppositeEndpoint() {
		Edge edge = new Edge(3, 5);
		
		Assert.assertEquals(3, edge.getOppositeEndpoint(5));
		Assert.assertEquals(5, edge.getOppositeEndpoint(3));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetOppositeEndpointError() {
		Edge edge = new Edge(3, 5);
		
		edge.getOppositeEndpoint(4);
	}
	
	@Test
	public void testHashCode1() {
		Edge edge1 = new Edge(3, 5);
		Edge edge2 = new Edge(5, 3);
		
		Assert.assertEquals(edge1.hashCode(), edge2.hashCode());
	}
	
	@Test
	public void testHashCode2() {
		Edge edge1 = new Edge(3, 5);
		Edge edge2 = new Edge(4, 3);
		
		Assert.assertNotEquals(edge1.hashCode(), edge2.hashCode());
	}
	
	@Test
	public void testEquals1() {
		Edge edge1 = new Edge(3, 5);
		Edge edge2 = new Edge(5, 3);
		
		Assert.assertTrue(edge1.equals(edge2));
		Assert.assertTrue(edge2.equals(edge1));
	}
	
	@Test
	public void testEquals2() {
		Edge edge1 = new Edge(3, 5);
		Edge edge2 = new Edge(4, 3);
		
		Assert.assertFalse(edge1.equals(edge2));
		Assert.assertFalse(edge2.equals(edge1));
	}

}
