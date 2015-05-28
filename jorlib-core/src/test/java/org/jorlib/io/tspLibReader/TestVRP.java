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
import java.util.HashSet;
import java.util.Set;

import org.jorlib.io.tspLibReader.fieldTypesAndFormats.DataType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests vehicle routing problem (VRP) instances.  This only tests if the
 * instance loads correctly, not for correctness.
 * 
 * @author David Hadka
 */
public final class TestVRP {
	
	private static Set<String> instances;
	
	@BeforeClass
	public static void initializeInstances() {
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
	public static void freeInstances() {
		instances = null;
	}
	
	@Test
	public void testLoad() throws IOException {
		for (String instance : instances) {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("./tspLib/vrp/"+instance+".vrp");
			if(inputStream == null)
				Assert.fail("Cannot find problem instance!");
			TSPLibInstance problem = new TSPLibInstance(inputStream);
			Assert.assertEquals(DataType.CVRP, problem.getDataType());
			inputStream.close();
		}
	}

}
