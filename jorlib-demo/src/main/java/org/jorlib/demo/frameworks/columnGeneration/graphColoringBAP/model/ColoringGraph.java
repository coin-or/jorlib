/* ==========================================
 * jORLib : a free Java OR library
 * ==========================================
 *
 * Project Info:  https://github.com/jkinable/jorlib
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 *
 */
/* -----------------
 * ColoringGraph.java
 * -----------------
 * (C) Copyright 2016, by Joris Kinable and Contributors.
 *
 * Original Author:  Joris Kinable
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 *
 */
package org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.model;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.VertexFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jorlib.frameworks.columnGeneration.model.ModelInterface;

import java.io.*;


/**
 * Class defining a graph coloring instance
 *
 * @author Joris Kinable
 * @version 29-6-2016
 */
public final class ColoringGraph extends SimpleGraph<Integer, DefaultEdge> implements ModelInterface, UndirectedGraph<Integer, DefaultEdge>{

    /** Instance name **/
    private final String instanceName;

    /**
     * Constructs a new graph coloring instance, based on a file specified in DIMACS format
     * @param instanceLocation input graph
     * @throws IOException Throws IO exception when the instance cannot be found.
     */
    public ColoringGraph(String instanceLocation) throws IOException {
        super(DefaultEdge.class);

        File inputFile=new File(instanceLocation);
        this.instanceName=inputFile.getName();
        BufferedReader in = new BufferedReader(new FileReader(inputFile));
        DIMACSImporter<Integer, DefaultEdge> importer=new DIMACSImporter<>(in, 1);
        importer.generateGraph(this, new IntegerVertexFactory(), null);

    }

    /**
     * Returns the number of vertices in the graph
     * @return Number of vertices
     */
    public int getNrVertices(){
        return this.vertexSet().size();
    }

    @Override
    public String getName() {
        return instanceName;
    }

    /**
     * Vertex factory used by the parser which processes the DIMACS input file
     */
    private static final class IntegerVertexFactory implements VertexFactory<Integer>{
        int last = 0;

        @Override
        public Integer createVertex()
        {
            return last++;
        }
    }
}
