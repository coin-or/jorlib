package org.jorlib.demo.io.tspLibReader;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import org.jorlib.io.tspLibReader.TSPLibInstance;
import org.jorlib.io.tspLibReader.TSPPanel;
import org.jorlib.io.tspLibReader.TSPLibTour;

/**
 * Simple class which reads a TSPLib instance and plots a solution graphically
 * @author David Hadka
 *
 */
public final class TSPGraphicalDemo {

	public static void main(String[] args) throws IOException {
		TSPLibInstance problem = new TSPLibInstance(new File("./data/tspLib/tsp/gr120.tsp"));
		problem.addTour(new File("./data/tspLib/tsp/gr120.opt.tour"));
		
		TSPPanel panel = new TSPPanel(problem);
		
		//Display tours
		panel.displayTour(problem.getTours().get(0), Color.RED, new BasicStroke(2.0f)); //Optimal tour
		panel.displayTour(TSPLibTour.createRandomTour(problem.getDimension()), new Color(128, 128, 128, 64)); //Random tour
		
		
		JFrame frame = new JFrame(problem.getName());
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(500, 400);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
