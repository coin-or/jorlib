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

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jorlib.io.tspLibReader.fieldTypesAndFormats.DataType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests traveling salesman problem (TSP) instances.
 * 
 * @author David Hadka
 */
public final class TestTSP {
	
	private static Map<String, Integer> instances;
	
	@BeforeClass
	public static void initializeInstances() {
		instances = new LinkedHashMap<>();
		instances.put("a280", 2579);
		instances.put("ali535", 202339);
		instances.put("att48", 10628);
		instances.put("att532", 27686);
		instances.put("bayg29", 1610);
		instances.put("bays29", 2020);
		instances.put("berlin52", 7542);
		instances.put("bier127", 118282);
		instances.put("brazil58", 25395);
		instances.put("brd14051", 469385);
		instances.put("brg180", 1950);
		instances.put("burma14", 3323);
		instances.put("ch130", 6110);
		instances.put("ch150", 6528);
		instances.put("d198", 15780);
		instances.put("d493", 35002);
		instances.put("d657", 48912);
		instances.put("d1291", 50801);
		instances.put("d1655", 62128);
		instances.put("d2103", 80450);
		instances.put("d15112", 1573084);
		instances.put("d18512", 645238);
		instances.put("dantzig42", 699);
		instances.put("dsj1000", 18660188);
		instances.put("eil51", 426);
		instances.put("eil76", 538);
		instances.put("eil101", 629);
		instances.put("fl417", 11861);
		instances.put("fl1400", 20127);
		instances.put("fl1577", 22249);
		instances.put("fl3795", 28772);
		instances.put("fnl4461", 182566);
		instances.put("fri26", 937);
		instances.put("gil262", 2378);
		instances.put("gr17", 2085);
		instances.put("gr21", 2707);
		instances.put("gr24", 1272);
		instances.put("gr48", 5046);
		instances.put("gr96", 55209);
		instances.put("gr120", 6942);
		instances.put("gr137", 69853);
		instances.put("gr202", 40160);
		instances.put("gr229", 134602);
		instances.put("gr431", 171414);
		instances.put("gr666", 294358);
		instances.put("hk48", 11461);
		instances.put("kroA100", 21282);
		instances.put("kroB100", 22141);
		instances.put("kroC100", 20749);
		instances.put("kroD100", 21294);
		instances.put("kroE100", 22068);
		instances.put("kroA150", 26524);
		instances.put("kroB150", 26130);
		instances.put("kroA200", 29368);
		instances.put("kroB200", 29437);
		instances.put("lin105", 14379);
		instances.put("lin318", 42029);
		instances.put("linhp318", 41345);
		instances.put("nrw1379", 56638);
		instances.put("p654", 34643);
		instances.put("pa561", 2763);
		instances.put("pcb442", 50778);
		instances.put("pcb1173", 56892);
		instances.put("pcb3038", 137694);
		instances.put("pla7397", 23260728);
		instances.put("pla33810", 66048945);
		instances.put("pla85900", 142382641);
		instances.put("pr76", 108159);
		instances.put("pr107", 44303);
		instances.put("pr124", 59030);
		instances.put("pr136", 96772);
		instances.put("pr144", 58537);
		instances.put("pr152", 73682);
		instances.put("pr226", 80369);
		instances.put("pr264", 49135);
		instances.put("pr299", 48191);
		instances.put("pr439", 107217);
		instances.put("pr1002", 259045);
		instances.put("pr2392", 378032);
		instances.put("rat99", 1211);
		instances.put("rat195", 2323);
		instances.put("rat575", 6773);
		instances.put("rat783", 8806);
		instances.put("rd100", 7910);
		instances.put("rd400", 15281);
		instances.put("rl1304", 252948);
		instances.put("rl1323", 270199);
		instances.put("rl1889", 316536);
		instances.put("rl5915", 565530);
		instances.put("rl5934", 556045);
		instances.put("rl11849", 923288);
		instances.put("st70", 675);
		instances.put("swiss42", 1273);
		instances.put("ts225", 126643);
		instances.put("tsp225", 3916);
		instances.put("u159", 42080);
		instances.put("u574", 36905);
		instances.put("u724", 41910);
		instances.put("u1060", 224094);
		instances.put("u1432", 152970);
		instances.put("u1817", 57201);
		instances.put("u2152", 64253);
		instances.put("u2319", 234256);
		instances.put("ulysses16", 6859);
		instances.put("ulysses22", 7013);
		instances.put("usa13509", 19982859);
		instances.put("vm1084", 239297);
		instances.put("vm1748", 336556);
	}
	
	@AfterClass
	public static void freeInstances() {
		instances = null;
	}
	
	@Test
	public void testLoad() throws IOException {
		for (String instance : instances.keySet()) {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("./tspLib/tsp/"+instance+".tsp");
			if(inputStream == null)
				Assert.fail("Cannot find problem instance!");
			TSPLibInstance problem = new TSPLibInstance(inputStream);
			Assert.assertEquals(DataType.TSP, problem.getDataType());
			inputStream.close();
		}
	}
	
	@Test
	public void testDistance() throws IOException {
		for (String instance : instances.keySet()) {
			InputStream inputStream1 = getClass().getClassLoader().getResourceAsStream("./tspLib/tsp/"+instance+".tsp");
			InputStream inputStream2 = getClass().getClassLoader().getResourceAsStream("./tspLib/tsp/"+instance+".opt.tour");
			if(inputStream1 == null)
				Assert.fail("Cannot find problem instance!");
			else if(inputStream2 == null){ //No optimal tour file exists
				inputStream1.close();
				continue;
			}
			TSPLibInstance problem = new TSPLibInstance(inputStream1);
			Assert.assertEquals(DataType.TSP, problem.getDataType());
			problem.addTour(inputStream2);
			for (TSPLibTour tour : problem.getTours()) {
				double tourLength = tour.distance(problem);
				double optimalLength = instances.get(instance);
				Assert.assertEquals(optimalLength, tourLength, 0.5);
			}
			inputStream1.close();
			inputStream2.close();
		}
	}

}
