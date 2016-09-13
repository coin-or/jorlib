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
package org.jorlib.demo.io.tsplibreader;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import org.jorlib.io.tsplibreader.TSPLibInstance;
import org.jorlib.io.tsplibreader.TSPPanel;
import org.jorlib.io.tsplibreader.TSPLibTour;

/**
 * Simple class which reads a TSPLib instance and plots a solution graphically
 * 
 * @author David Hadka
 *
 */
public final class TSPGraphicalDemo
{

    public static void main(String[] args)
        throws IOException
    {
        TSPLibInstance problem = new TSPLibInstance(new File("./data/tspLib/tsp/gr120.tsp"));
        problem.addTour(new File("./data/tspLib/tsp/gr120.opt.tour"));

        TSPPanel panel = new TSPPanel(problem);

        // Display tours
        panel.displayTour(problem.getTours().get(0), Color.RED, new BasicStroke(2.0f)); // Optimal
                                                                                        // tour
        panel.displayTour(
            TSPLibTour.createRandomTour(problem.getDimension()), new Color(128, 128, 128, 64)); // Random
                                                                                                // tour

        JFrame frame = new JFrame(problem.getName());
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
