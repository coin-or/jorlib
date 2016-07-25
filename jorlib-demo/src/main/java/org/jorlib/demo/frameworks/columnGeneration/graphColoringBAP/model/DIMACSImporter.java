package org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.model;

import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;
import org.jgrapht.WeightedGraph;
import org.jgrapht.generate.GraphGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * NOTE: This class will be present in the 1.0 release of jGrapht and as such will be not be part of jORLib.
 * This class is temporarily here until the the 1.0 release of jGrapht is available.
 *
 * Imports a graph specified in DIMACS format (http://mat.gsia.cmu.edu/COLOR/general/ccformat.ps).
 * In summary, graphs specified in DIMACS format adhere to the following structure:
 * <pre><code>
 *
 * DIMACS G {
 *    c <comments; ignored during parsing of the graph
 *    p edge <number of nodes> <number of edges>
 *    e <edge source 1> <edge target 1>
 *    e <edge source 2> <edge target 2>
 *    e <edge source 3> <edge target 3>
 *    e <edge source 4> <edge target 4>
 *    ...
 * }
 *
 * </code></pre>
 *
 * Although not specified directly in the DIMACS format documentation, this implementation also allows for the a weighted variant:
 * <pre><code>e <edge source 1> <edge target 1> <edge_weight> </code></pre>
 *
 * Note: the current implementation does not fully implement the DIMACS specifications! Special (rarely used) fields
 * specified as 'Optional Descriptors' are currently not supported.
 *
 * @author Michael Behrisch (adaptation of GraphReader class)
 * @author Joris Kinable
 *
 * @param <V>
 * @param <E>
 */
import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;
import org.jgrapht.WeightedGraph;
import org.jgrapht.generate.GraphGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Imports a graph specified in DIMACS format
 * (http://mat.gsia.cmu.edu/COLOR/general/ccformat.ps). In summary, graphs
 * specified in DIMACS format adhere to the following structure:
 *
 * <pre>
 * {@code
 * DIMACS G {
 *    c <comments; ignored during parsing of the graph
 *    p edge <number of nodes> <number of edges>
 *    e <edge source 1> <edge target 1>
 *    e <edge source 2> <edge target 2>
 *    e <edge source 3> <edge target 3>
 *    e <edge source 4> <edge target 4>
 *    ...
 * }
 * }
 * </pre>
 *
 * Although not specified directly in the DIMACS format documentation, this
 * implementation also allows for the a weighted variant:
 *
 * <pre>
 * {@code
 * e <edge source 1> <edge target 1> <edge_weight>
 * }
 * </pre>
 *
 * Note: the current implementation does not fully implement the DIMACS
 * specifications! Special (rarely used) fields specified as 'Optional
 * Descriptors' are currently not supported.
 *
 * @author Michael Behrisch (adaptation of GraphReader class)
 * @author Joris Kinable
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
public class DIMACSImporter<V, E>
        implements GraphGenerator<V, E, V>
{
    private final BufferedReader input;
    private final double defaultWeight;

    // ~ Constructors ----------------------------------------------------------

    /**
     * Construct a new DIMACSImporter
     *
     * @param input the input reader
     * @param defaultWeight default edge weight
     * @throws IOException in case an I/O error occurs
     */
    public DIMACSImporter(Reader input, double defaultWeight)
            throws IOException
    {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader) input;
        } else {
            this.input = new BufferedReader(input);
        }
        this.defaultWeight = defaultWeight;
    }

    /**
     * Construct a new DIMACSImporter
     *
     * @param input the input reader
     * @throws IOException in case an I/O error occurs
     */
    public DIMACSImporter(Reader input)
            throws IOException
    {
        this(input, 1);
    }

    // ~ Methods ---------------------------------------------------------------

    private String[] split(final String src)
    {
        if (src == null) {
            return null;
        }
        return src.split("\\s+");
    }

    private String[] skipComments()
    {
        String[] cols = null;
        try {
            cols = split(input.readLine());
            while ((cols != null) && ((cols.length == 0) || cols[0].equals("c")
                    || cols[0].startsWith("%")))
            {
                cols = split(input.readLine());
            }
        } catch (IOException e) {
        }
        return cols;
    }

    private int readNodeCount()
    {
        final String[] cols = skipComments();
        if (cols[0].equals("p")) {
            return Integer.parseInt(cols[2]);
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateGraph(
            Graph<V, E> target,
            VertexFactory<V> vertexFactory,
            Map<String, V> resultMap)
    {
        final int size = readNodeCount();
        if (resultMap == null) {
            resultMap = new HashMap<>();
        }

        for (int i = 0; i < size; i++) {
            V newVertex = vertexFactory.createVertex();
            target.addVertex(newVertex);
            resultMap.put(Integer.toString(i + 1), newVertex);
        }
        String[] cols = skipComments();
        while (cols != null) {
            if (cols[0].equals("e")) {
                E edge = target
                        .addEdge(resultMap.get(cols[1]), resultMap.get(cols[2]));
                if (target instanceof WeightedGraph && (edge != null)) {
                    double weight = defaultWeight;
                    if (cols.length > 3) {
                        weight = Double.parseDouble(cols[3]);
                    }
                    ((WeightedGraph<V, E>) target).setEdgeWeight(edge, weight);
                }
            }
            cols = skipComments();
        }
    }
}