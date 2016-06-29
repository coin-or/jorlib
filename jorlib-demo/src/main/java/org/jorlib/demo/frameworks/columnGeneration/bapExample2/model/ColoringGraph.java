package org.jorlib.demo.frameworks.columnGeneration.bapExample2.model;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.VertexFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jorlib.frameworks.columnGeneration.model.ModelInterface;

import java.io.*;


/**
 * Created by jkinable on 6/27/16.
 */
public final class ColoringGraph extends SimpleGraph<Integer, DefaultEdge> implements ModelInterface, UndirectedGraph<Integer, DefaultEdge>{

    private final String instanceName;

    public ColoringGraph(String instanceLocation) throws IOException {
        super(DefaultEdge.class);

        File inputFile=new File(instanceLocation);
        this.instanceName=inputFile.getName();
        BufferedReader in = new BufferedReader(new FileReader(inputFile));
        DIMACSImporter<Integer, DefaultEdge> importer=new DIMACSImporter<>(in, 1);
        importer.generateGraph(this, new IntegerVertexFactory(), null);

    }

    public int getNrVertices(){
        return this.vertexSet().size();
    }

    @Override
    public String getName() {
        return instanceName;
    }

    private static final class IntegerVertexFactory implements VertexFactory<Integer>{
        int last = 0;

        @Override
        public Integer createVertex()
        {
            return last++;
        }
    }
}
