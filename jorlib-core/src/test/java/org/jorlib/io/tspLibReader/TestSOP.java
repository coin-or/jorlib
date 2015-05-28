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
 * Tests sequential ordering problem (SOP) instances.  This only tests if the
 * instances load without error, not for correctness.
 * 
 * @author David Hadka
 */
public final class TestSOP {
	
	private static Set<String> instances;
	
	@BeforeClass
	public static void initializeInstances() {
		instances = new HashSet<String>();
		instances.add("br17.10");
		instances.add("br17.12");
		instances.add("ESC07");
		instances.add("ESC11");
		instances.add("ESC12");
		instances.add("ESC25");
		instances.add("ESC47");
		instances.add("ESC63");
		instances.add("ESC78");
		instances.add("ft53.1");
		instances.add("ft53.2");
		instances.add("ft53.3");
		instances.add("ft53.4");
		instances.add("ft70.1");
		instances.add("ft70.2");
		instances.add("ft70.3");
		instances.add("ft70.4");
		instances.add("kro124p.1");
		instances.add("kro124p.2");
		instances.add("kro124p.3");
		instances.add("kro124p.4");
		instances.add("p43.1");
		instances.add("p43.2");
		instances.add("p43.3");
		instances.add("p43.4");
		instances.add("prob.42");
		instances.add("prob.100");
		instances.add("rbg048a");
		instances.add("rbg050c");
		instances.add("rbg109a");
		instances.add("rbg150a");
		instances.add("rbg174a");
		instances.add("rbg253a");
		instances.add("rbg323a");
		instances.add("rbg341a");
		instances.add("rbg358a");
		instances.add("rbg378a");
		instances.add("ry48p.1");
		instances.add("ry48p.2");
		instances.add("ry48p.3");
		instances.add("ry48p.4");
		
	}
	
	@AfterClass
	public static void freeInstances() {
		instances = null;
	}
	
	@Test
	public void testLoad() throws IOException {
		for (String instance : instances) {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("./tspLib/sop/"+instance+".sop");
			if(inputStream == null)
				Assert.fail("Cannot find problem instance!");
			TSPLibInstance problem = new TSPLibInstance(inputStream);
			Assert.assertEquals(DataType.SOP, problem.getDataType());
			inputStream.close();
		}
	}

}
