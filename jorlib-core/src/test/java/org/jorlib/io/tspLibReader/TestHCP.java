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
public class TestHCP {
	
	private static Set<String> instances;
	
	@BeforeClass
	public static void initializeInstances() {
		instances = new HashSet<String>();
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
		File directory = new File("./data/tspLib/hcp/");
		if(!directory.exists()){
			System.out.println("Skipping TestHCP unit tests: HCP data not available");
			return;
		}
		for (String instance : instances) {
			File instanceData = new File(directory, instance + ".hcp");
			File optimalTour = new File(directory, instance + ".opt.tour");
			
			if (instanceData.exists() && optimalTour.exists()) {
				TSPInstance problem = new TSPInstance(instanceData);
				Assert.assertEquals(DataType.HCP, problem.getDataType());
				problem.addTour(optimalTour);
				
				for (Tour tour : problem.getTours()) {
					Assert.assertTrue(tour.isHamiltonianCycle(problem));
					Assert.assertTrue(tour.containsFixedEdges(problem));
				}
			}else
				System.out.println("Skipping TestHCP unit test for instance: "+instanceData.getName()+" - Instance file or tour file does not exist");
		}
	}

}
