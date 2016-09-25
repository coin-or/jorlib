/* ==========================================
 * jORLib : Java Operations Research Library
 * ==========================================
 *
 * Project Info:  http://www.coin-or.org/projects/jORLib.xml
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2016-2016, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 * as published by the Free Software Foundation.
 */
package org.jorlib.demo.frameworks.columngeneration.graphcoloringbap.model;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.ext.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jorlib.frameworks.columngeneration.model.ModelInterface;

import java.io.*;

/**
 * Class defining a graph coloring instance
 *
 * @author Joris Kinable
 * @version 29-6-2016
 */
public final class ColoringGraph
    extends SimpleGraph<Integer, DefaultEdge>
    implements ModelInterface, UndirectedGraph<Integer, DefaultEdge>
{

    /** Instance name **/
    private final String instanceName;

    /**
     * Constructs a new graph coloring instance, based on a file specified in DIMACS format
     * 
     * @param instanceLocation input graph
     * @throws ImportException Throws ImportException exception when the instance cannot be found or when there is a parse error.
     */
    public ColoringGraph(String instanceLocation)
        throws ImportException
    {
        super(DefaultEdge.class);

        File inputFile = new File(instanceLocation);
        this.instanceName = inputFile.getName();

        //Import the graph
        VertexProvider<Integer> vp = (label, attributes) -> Integer.parseInt(label)-1;
        EdgeProvider<Integer, DefaultEdge> ep = (from, to, label, attributes) -> ColoringGraph.this.getEdgeFactory().createEdge(from, to);
        GraphImporter<Integer, DefaultEdge> importer = new DIMACSImporter<>(vp, ep);
        importer.importGraph(this, inputFile);

    }

    /**
     * Returns the number of vertices in the graph
     * 
     * @return Number of vertices
     */
    public int getNrVertices()
    {
        return this.vertexSet().size();
    }

    @Override
    public String getName()
    {
        return instanceName;
    }
}
