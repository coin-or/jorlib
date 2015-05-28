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
 * Tests Hamiltonian cycle problem (HCP) instances.  
 * 
 * @author David Hadka
 */
public final class TestHCP {
	
	private static Set<String> instances;
	
	@BeforeClass
	public static void initializeInstances() {
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
	public static void freeInstances() {
		instances = null;
	}
	
	@Test
	public void testLoad() throws IOException {
		for(String instance : instances){
			InputStream inputStream1 = getClass().getClassLoader().getResourceAsStream("./tspLib/hcp/"+instance+".hcp");
			if(inputStream1 == null)
				Assert.fail("Cannot find problem instance!");
			TSPLibInstance problem = new TSPLibInstance(inputStream1);
			Assert.assertEquals(DataType.HCP, problem.getDataType());

			InputStream inputStream2 = getClass().getClassLoader().getResourceAsStream("./tspLib/hcp/"+instance+".opt.tour");
			if(inputStream2 == null){ //No optimal tour file exists
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
