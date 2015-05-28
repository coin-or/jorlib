package org.jorlib.demo.io.tspLibReader;

import java.io.File;
import java.io.IOException;

import org.jorlib.io.tspLibReader.TSPLibInstance;

/**
 * Simple class which reads a TSPLib instance
 * @author Joris Kinable
 * @since April 24, 2015
 *
 */
public final class TSPDemo {

	public TSPDemo() throws IOException{
		//Read a TSP instance from the TSPLib, as well as a TSP tour 
		File directory = new File("./data/tspLib/tsp/");
		File instanceData = new File(directory, "ulysses16.tsp");
		File optimalTour = new File(directory, "ulysses16.opt.tour");
		
		//Create a TSP instance, thereby parsing the TSPLib file
		TSPLibInstance problem = new TSPLibInstance(instanceData);
		//Add the tour
		problem.addTour(optimalTour);
		
		//Print some information about the problem
		System.out.println("Name of TSP problem: "+problem.getName());
		System.out.println("Number of vertices: "+problem.getDimension());
		System.out.println("Number of registered tours: "+problem.getTours().size());
		System.out.println("Length of registered tour: "+problem.getTours().get(0).distance(problem));
	}
	
	public static void main(String[] args){
		try {
			new TSPDemo();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
