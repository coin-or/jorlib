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
import org.jgrapht.VertexFactory;
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
     * @throws IOException Throws IO exception when the instance cannot be found.
     */
    public ColoringGraph(String instanceLocation)
        throws IOException
    {
        super(DefaultEdge.class);

        File inputFile = new File(instanceLocation);
        this.instanceName = inputFile.getName();
        BufferedReader in = new BufferedReader(new FileReader(inputFile));
        DIMACSImporter<Integer, DefaultEdge> importer = new DIMACSImporter<>(in, 1);
        importer.generateGraph(this, new IntegerVertexFactory(), null);

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

    /**
     * Vertex factory used by the parser which processes the DIMACS input file
     */
    private static final class IntegerVertexFactory
        implements VertexFactory<Integer>
    {
        int last = 0;

        @Override
        public Integer createVertex()
        {
            return last++;
        }
    }
}
