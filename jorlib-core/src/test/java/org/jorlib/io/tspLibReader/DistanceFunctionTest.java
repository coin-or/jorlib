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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the distance functions against the cases provided in the official
 * TSPLIB documentation.
 * 
 * @author David Hadka
 */
public final class DistanceFunctionTest {

	@Test
	public void testPCB442() throws IOException, URISyntaxException{
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("./tspLib/tsp/pcb442.tsp");
		TSPLibInstance problem = new TSPLibInstance(inputStream);
		problem.addTour(TSPLibTour.createCanonicalTour(problem.getDimension()));
		Assert.assertEquals(221440, problem.getTours().get(0).distance(problem), 0.5);
		inputStream.close();
	}

	@Test
	public void testGR666() throws IOException, URISyntaxException {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("./tspLib/tsp/gr666.tsp");
		TSPLibInstance problem = new TSPLibInstance(inputStream);
		problem.addTour(TSPLibTour.createCanonicalTour(problem.getDimension()));
		Assert.assertEquals(423710, problem.getTours().get(0).distance(problem), 0.5);
		inputStream.close();
	}

	@Test
	public void testATT532() throws IOException, URISyntaxException {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("./tspLib/tsp/att532.tsp");
		TSPLibInstance problem = new TSPLibInstance(inputStream);
		problem.addTour(TSPLibTour.createCanonicalTour(problem.getDimension()));
		Assert.assertEquals(309636, problem.getTours().get(0).distance(problem), 0.5);
		inputStream.close();
	}
	
}
