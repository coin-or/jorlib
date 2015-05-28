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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.jorlib.io.tspLibReader.fieldTypesAndFormats.DataType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests asymmetric traveling salesman problem (ATOP) instances.  This only
 * tests if the instances loaded without error, not for correctness.
 * 
 * @author David Hadka
 */
public final class TestATSP {
	
	private static Set<String> instances;
	
	@BeforeClass
	public static void initializeInstances() {
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
	public static void freeInstances() {
		instances = null;
	}
	
	@Test
	public void testLoad() throws IOException {
		for (String instance : instances) {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("./tspLib/atsp/"+instance+".atsp");
			if(inputStream == null)
				Assert.fail("Cannot find problem instance!");
			TSPLibInstance problem = new TSPLibInstance(inputStream);
			Assert.assertEquals(DataType.ATSP, problem.getDataType());
			inputStream.close();
		}
	}

}
