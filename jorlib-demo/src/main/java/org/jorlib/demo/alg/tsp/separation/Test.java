package org.jorlib.demo.alg.tsp.separation;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.VertexFactory;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jorlib.alg.tsp.separation.SubtourSeparator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jkinable on 5/1/15.
 */
public class Test {



    /**
     * Example on undirected graph
     */
    public void example1(){
        Graph<Integer, DefaultEdge> completeGraph=new SimpleGraph<>(DefaultEdge.class);
        CompleteGraphGenerator<Integer, DefaultEdge> completeGenerator =new CompleteGraphGenerator<>(22);
        completeGenerator.generateGraph(completeGraph, new IntegerVertexFactory(), null);

        Map<DefaultEdge, Double> edgeValueMap=new HashMap<DefaultEdge, Double>();
        edgeValueMap.put(completeGraph.getEdge(0,9), 0.0);
        edgeValueMap.put(completeGraph.getEdge(6,12), 0.0);
        edgeValueMap.put(completeGraph.getEdge(5,18), 0.0);
        edgeValueMap.put(completeGraph.getEdge(7,13), 0.0);
        edgeValueMap.put(completeGraph.getEdge(8,9), 1.0);
        edgeValueMap.put(completeGraph.getEdge(7,10), 0.0);
        edgeValueMap.put(completeGraph.getEdge(7,17), 1.0);
        edgeValueMap.put(completeGraph.getEdge(9,13), 0.0);
        edgeValueMap.put(completeGraph.getEdge(9,19), 0.0);
        edgeValueMap.put(completeGraph.getEdge(11,13), 1.0);
        edgeValueMap.put(completeGraph.getEdge(6,7), 0.0);
        edgeValueMap.put(completeGraph.getEdge(7,20), 0.0);
        edgeValueMap.put(completeGraph.getEdge(0,5), 0.0);
        edgeValueMap.put(completeGraph.getEdge(3,19), 0.0);
        edgeValueMap.put(completeGraph.getEdge(6,16), 0.0);
        edgeValueMap.put(completeGraph.getEdge(5,10), 0.0);
        edgeValueMap.put(completeGraph.getEdge(7,8), 0.0);
        edgeValueMap.put(completeGraph.getEdge(13,21), 0.0);
        edgeValueMap.put(completeGraph.getEdge(16,21), 1.0);
        edgeValueMap.put(completeGraph.getEdge(1,2), 1.0);
        edgeValueMap.put(completeGraph.getEdge(0,8), 0.0);
        edgeValueMap.put(completeGraph.getEdge(0,17), 0.0);
        edgeValueMap.put(completeGraph.getEdge(18,21), 0.0);
        edgeValueMap.put(completeGraph.getEdge(6,11), 1.0);
        edgeValueMap.put(completeGraph.getEdge(3,8), 0.0);
        edgeValueMap.put(completeGraph.getEdge(0,6), 0.0);
        edgeValueMap.put(completeGraph.getEdge(4,5), 0.0);
        edgeValueMap.put(completeGraph.getEdge(5,19), 0.0);
        edgeValueMap.put(completeGraph.getEdge(1,18), 0.0);
        edgeValueMap.put(completeGraph.getEdge(7,9), 0.0);
        edgeValueMap.put(completeGraph.getEdge(2,6), 0.0);
        edgeValueMap.put(completeGraph.getEdge(2,7), 0.0);
        edgeValueMap.put(completeGraph.getEdge(15,17), 0.0);
        edgeValueMap.put(completeGraph.getEdge(5,17), 0.0);
        edgeValueMap.put(completeGraph.getEdge(11,21), 0.0);
        edgeValueMap.put(completeGraph.getEdge(11,18), 0.0);
        edgeValueMap.put(completeGraph.getEdge(4,18), 0.0);
        edgeValueMap.put(completeGraph.getEdge(3,4), 0.0);
        edgeValueMap.put(completeGraph.getEdge(9,21), 0.0);
        edgeValueMap.put(completeGraph.getEdge(1,16), 1.0);
        edgeValueMap.put(completeGraph.getEdge(3,13), 0.0);
        edgeValueMap.put(completeGraph.getEdge(9,14), 0.0);
        edgeValueMap.put(completeGraph.getEdge(4,6), 0.0);
        edgeValueMap.put(completeGraph.getEdge(5,16), 0.0);
        edgeValueMap.put(completeGraph.getEdge(9,16), 0.0);
        edgeValueMap.put(completeGraph.getEdge(6,18), 0.0);
        edgeValueMap.put(completeGraph.getEdge(1,13), 0.0);
        edgeValueMap.put(completeGraph.getEdge(3,7), 0.0);
        edgeValueMap.put(completeGraph.getEdge(10,16), 0.0);
        edgeValueMap.put(completeGraph.getEdge(10,14), 0.0);
        edgeValueMap.put(completeGraph.getEdge(9,18), 1.0);
        edgeValueMap.put(completeGraph.getEdge(0,14), 0.0);
        edgeValueMap.put(completeGraph.getEdge(2,15), 0.0);
        edgeValueMap.put(completeGraph.getEdge(0,4), 0.0);
        edgeValueMap.put(completeGraph.getEdge(4,7), 0.0);
        edgeValueMap.put(completeGraph.getEdge(0,7), 1.0);
        edgeValueMap.put(completeGraph.getEdge(0,20), 0.0);
        edgeValueMap.put(completeGraph.getEdge(14,15), 0.0);
        edgeValueMap.put(completeGraph.getEdge(0,16), 0.0);
        edgeValueMap.put(completeGraph.getEdge(6,15), 0.0);
        edgeValueMap.put(completeGraph.getEdge(4,14), 1.0);
        edgeValueMap.put(completeGraph.getEdge(0,19), 0.0);
        edgeValueMap.put(completeGraph.getEdge(10,13), 0.0);
        edgeValueMap.put(completeGraph.getEdge(4,21), 0.0);
        edgeValueMap.put(completeGraph.getEdge(5,12), 0.0);
        edgeValueMap.put(completeGraph.getEdge(1,11), 0.0);
        edgeValueMap.put(completeGraph.getEdge(1,19), 0.0);
        edgeValueMap.put(completeGraph.getEdge(5,11), 0.0);
        edgeValueMap.put(completeGraph.getEdge(1,12), 0.0);
        edgeValueMap.put(completeGraph.getEdge(4,8), 0.0);
        edgeValueMap.put(completeGraph.getEdge(0,15), 0.0);
        edgeValueMap.put(completeGraph.getEdge(17,21), 0.0);
        edgeValueMap.put(completeGraph.getEdge(7,14), 0.0);
        edgeValueMap.put(completeGraph.getEdge(8,14), 0.0);
        edgeValueMap.put(completeGraph.getEdge(4,20), 0.0);
        edgeValueMap.put(completeGraph.getEdge(1,4), 0.0);
        edgeValueMap.put(completeGraph.getEdge(4,15), 0.0);
        edgeValueMap.put(completeGraph.getEdge(13,14), 0.0);
        edgeValueMap.put(completeGraph.getEdge(16,19), 0.0);
        edgeValueMap.put(completeGraph.getEdge(2,19), 0.0);
        edgeValueMap.put(completeGraph.getEdge(13,15), 0.0);
        edgeValueMap.put(completeGraph.getEdge(9,10), 0.0);
        edgeValueMap.put(completeGraph.getEdge(8,17), 0.0);
        edgeValueMap.put(completeGraph.getEdge(3,11), 0.0);
        edgeValueMap.put(completeGraph.getEdge(2,21), 0.0);
        edgeValueMap.put(completeGraph.getEdge(11,20), 0.0);
        edgeValueMap.put(completeGraph.getEdge(0,18), 0.0);
        edgeValueMap.put(completeGraph.getEdge(12,17), 0.0);
        edgeValueMap.put(completeGraph.getEdge(1,3), 0.0);
        edgeValueMap.put(completeGraph.getEdge(11,17), 0.0);
        edgeValueMap.put(completeGraph.getEdge(3,21), 1.0);
        edgeValueMap.put(completeGraph.getEdge(14,17), 0.0);
        edgeValueMap.put(completeGraph.getEdge(1,8), 0.0);
        edgeValueMap.put(completeGraph.getEdge(3,18), 0.0);
        edgeValueMap.put(completeGraph.getEdge(18,19), 1.0);
        edgeValueMap.put(completeGraph.getEdge(1,20), 0.0);
        edgeValueMap.put(completeGraph.getEdge(9,20), 0.0);
        edgeValueMap.put(completeGraph.getEdge(13,19), 0.0);
        edgeValueMap.put(completeGraph.getEdge(6,21), 0.0);
        edgeValueMap.put(completeGraph.getEdge(6,9), 0.0);
        edgeValueMap.put(completeGraph.getEdge(11,12), 0.0);
        edgeValueMap.put(completeGraph.getEdge(17,20), 0.0);
        edgeValueMap.put(completeGraph.getEdge(3,16), 0.0);
        edgeValueMap.put(completeGraph.getEdge(2,18), 0.0);
        edgeValueMap.put(completeGraph.getEdge(4,12), 0.0);
        edgeValueMap.put(completeGraph.getEdge(2,14), 0.0);
        edgeValueMap.put(completeGraph.getEdge(8,12), 0.0);
        edgeValueMap.put(completeGraph.getEdge(4,17), 0.0);
        edgeValueMap.put(completeGraph.getEdge(6,17), 0.0);
        edgeValueMap.put(completeGraph.getEdge(3,20), 0.0);
        edgeValueMap.put(completeGraph.getEdge(7,16), 0.0);
        edgeValueMap.put(completeGraph.getEdge(13,16), 0.0);
        edgeValueMap.put(completeGraph.getEdge(7,21), 0.0);
        edgeValueMap.put(completeGraph.getEdge(13,20), 0.0);
        edgeValueMap.put(completeGraph.getEdge(1,15), 0.0);
        edgeValueMap.put(completeGraph.getEdge(2,20), 0.0);
        edgeValueMap.put(completeGraph.getEdge(5,15), 0.0);
        edgeValueMap.put(completeGraph.getEdge(4,13), 0.0);
        edgeValueMap.put(completeGraph.getEdge(2,17), 0.0);
        edgeValueMap.put(completeGraph.getEdge(3,17), 1.0);
        edgeValueMap.put(completeGraph.getEdge(4,9), 0.0);
        edgeValueMap.put(completeGraph.getEdge(8,20), 0.0);
        edgeValueMap.put(completeGraph.getEdge(12,16), 0.0);
        edgeValueMap.put(completeGraph.getEdge(5,8), 0.0);
        edgeValueMap.put(completeGraph.getEdge(12,13), 1.0);
        edgeValueMap.put(completeGraph.getEdge(10,11), 0.0);
        edgeValueMap.put(completeGraph.getEdge(5,9), 0.0);
        edgeValueMap.put(completeGraph.getEdge(7,18), 0.0);
        edgeValueMap.put(completeGraph.getEdge(11,14), 0.0);
        edgeValueMap.put(completeGraph.getEdge(2,12), 0.0);
        edgeValueMap.put(completeGraph.getEdge(0,11), 0.0);
        edgeValueMap.put(completeGraph.getEdge(2,9), 0.0);
        edgeValueMap.put(completeGraph.getEdge(15,19), 0.0);
        edgeValueMap.put(completeGraph.getEdge(5,21), 0.0);
        edgeValueMap.put(completeGraph.getEdge(16,20), 0.0);
        edgeValueMap.put(completeGraph.getEdge(2,4), 0.0);
        edgeValueMap.put(completeGraph.getEdge(12,14), 0.0);
        edgeValueMap.put(completeGraph.getEdge(6,8), 0.0);
        edgeValueMap.put(completeGraph.getEdge(1,6), 0.0);
        edgeValueMap.put(completeGraph.getEdge(14,21), 0.0);
        edgeValueMap.put(completeGraph.getEdge(13,17), 0.0);
        edgeValueMap.put(completeGraph.getEdge(2,8), 0.0);
        edgeValueMap.put(completeGraph.getEdge(3,5), 0.0);
        edgeValueMap.put(completeGraph.getEdge(15,18), 0.0);
        edgeValueMap.put(completeGraph.getEdge(8,11), 0.0);
        edgeValueMap.put(completeGraph.getEdge(8,15), 0.0);
        edgeValueMap.put(completeGraph.getEdge(4,16), 0.0);
        edgeValueMap.put(completeGraph.getEdge(17,18), 0.0);
        edgeValueMap.put(completeGraph.getEdge(19,20), 1.0);
        edgeValueMap.put(completeGraph.getEdge(1,14), 0.0);
        edgeValueMap.put(completeGraph.getEdge(4,19), 0.0);
        edgeValueMap.put(completeGraph.getEdge(8,13), 0.0);
        edgeValueMap.put(completeGraph.getEdge(1,21), 0.0);
        edgeValueMap.put(completeGraph.getEdge(0,3), 0.0);
        edgeValueMap.put(completeGraph.getEdge(5,6), 1.0);
        edgeValueMap.put(completeGraph.getEdge(7,12), 0.0);
        edgeValueMap.put(completeGraph.getEdge(6,20), 0.0);
        edgeValueMap.put(completeGraph.getEdge(15,20), 1.0);
        edgeValueMap.put(completeGraph.getEdge(2,3), 0.0);
        edgeValueMap.put(completeGraph.getEdge(7,15), 0.0);
        edgeValueMap.put(completeGraph.getEdge(9,11), 0.0);
        edgeValueMap.put(completeGraph.getEdge(0,1), 0.0);
        edgeValueMap.put(completeGraph.getEdge(6,14), 0.0);
        edgeValueMap.put(completeGraph.getEdge(17,19), 0.0);
        edgeValueMap.put(completeGraph.getEdge(10,18), 0.0);
        edgeValueMap.put(completeGraph.getEdge(0,21), 0.0);
        edgeValueMap.put(completeGraph.getEdge(7,11), 0.0);
        edgeValueMap.put(completeGraph.getEdge(2,13), 0.0);
        edgeValueMap.put(completeGraph.getEdge(10,21), 0.0);
        edgeValueMap.put(completeGraph.getEdge(7,19), 0.0);
        edgeValueMap.put(completeGraph.getEdge(0,13), 0.0);
        edgeValueMap.put(completeGraph.getEdge(4,11), 0.0);
        edgeValueMap.put(completeGraph.getEdge(9,17), 0.0);
        edgeValueMap.put(completeGraph.getEdge(11,16), 0.0);
        edgeValueMap.put(completeGraph.getEdge(19,21), 0.0);
        edgeValueMap.put(completeGraph.getEdge(16,17), 0.0);
        edgeValueMap.put(completeGraph.getEdge(3,15), 0.0);
        edgeValueMap.put(completeGraph.getEdge(0,12), 0.0);
        edgeValueMap.put(completeGraph.getEdge(14,19), 0.0);
        edgeValueMap.put(completeGraph.getEdge(15,16), 0.0);
        edgeValueMap.put(completeGraph.getEdge(1,7), 0.0);
        edgeValueMap.put(completeGraph.getEdge(1,10), 0.0);
        edgeValueMap.put(completeGraph.getEdge(9,12), 0.0);
        edgeValueMap.put(completeGraph.getEdge(1,9), 0.0);
        edgeValueMap.put(completeGraph.getEdge(3,10), 0.0);
        edgeValueMap.put(completeGraph.getEdge(6,13), 0.0);
        edgeValueMap.put(completeGraph.getEdge(12,20), 0.0);
        edgeValueMap.put(completeGraph.getEdge(10,12), 0.0);
        edgeValueMap.put(completeGraph.getEdge(10,20), 0.0);
        edgeValueMap.put(completeGraph.getEdge(8,19), 0.0);
        edgeValueMap.put(completeGraph.getEdge(2,11), 0.0);
        edgeValueMap.put(completeGraph.getEdge(15,21), 0.0);
        edgeValueMap.put(completeGraph.getEdge(0,2), 1.0);
        edgeValueMap.put(completeGraph.getEdge(12,19), 0.0);
        edgeValueMap.put(completeGraph.getEdge(3,12), 0.0);
        edgeValueMap.put(completeGraph.getEdge(5,20), 0.0);
        edgeValueMap.put(completeGraph.getEdge(3,6), 0.0);
        edgeValueMap.put(completeGraph.getEdge(3,14), 0.0);
        edgeValueMap.put(completeGraph.getEdge(11,15), 0.0);
        edgeValueMap.put(completeGraph.getEdge(0,10), 0.0);
        edgeValueMap.put(completeGraph.getEdge(1,5), 0.0);
        edgeValueMap.put(completeGraph.getEdge(1,17), 0.0);
        edgeValueMap.put(completeGraph.getEdge(6,19), 0.0);
        edgeValueMap.put(completeGraph.getEdge(10,19), 0.0);
        edgeValueMap.put(completeGraph.getEdge(12,21), 0.0);
        edgeValueMap.put(completeGraph.getEdge(8,18), 0.0);
        edgeValueMap.put(completeGraph.getEdge(2,10), 0.0);
        edgeValueMap.put(completeGraph.getEdge(3,9), 0.0);
        edgeValueMap.put(completeGraph.getEdge(12,18), 0.0);
        edgeValueMap.put(completeGraph.getEdge(12,15), 1.0);
        edgeValueMap.put(completeGraph.getEdge(8,21), 0.0);
        edgeValueMap.put(completeGraph.getEdge(14,20), 0.0);
        edgeValueMap.put(completeGraph.getEdge(14,16), 0.0);
        edgeValueMap.put(completeGraph.getEdge(10,15), 0.0);
        edgeValueMap.put(completeGraph.getEdge(20,21), 0.0);
        edgeValueMap.put(completeGraph.getEdge(8,16), 0.0);
        edgeValueMap.put(completeGraph.getEdge(8,10), 1.0);
        edgeValueMap.put(completeGraph.getEdge(11,19), 0.0);
        edgeValueMap.put(completeGraph.getEdge(16,18), 0.0);
        edgeValueMap.put(completeGraph.getEdge(5,14), 1.0);
        edgeValueMap.put(completeGraph.getEdge(14,18), 0.0);
        edgeValueMap.put(completeGraph.getEdge(13,18), 0.0);
        edgeValueMap.put(completeGraph.getEdge(2,16), 0.0);
        edgeValueMap.put(completeGraph.getEdge(6,10), 0.0);
        edgeValueMap.put(completeGraph.getEdge(4,10), 1.0);
        edgeValueMap.put(completeGraph.getEdge(18,20), 0.0);
        edgeValueMap.put(completeGraph.getEdge(2,5), 0.0);
        edgeValueMap.put(completeGraph.getEdge(5,13), 0.0);
        edgeValueMap.put(completeGraph.getEdge(9,15), 0.0);
        edgeValueMap.put(completeGraph.getEdge(5,7), 0.0);
        edgeValueMap.put(completeGraph.getEdge(10,17), 0.0);

        //Invoke the separator
        SubtourSeparator<Integer, DefaultEdge> separator=new SubtourSeparator<Integer, DefaultEdge>(completeGraph);
        separator.separateSubtour(edgeValueMap);

        System.out.println("Has found a violated subtour: "+separator.hasSubtour());
        System.out.println("Cut value: "+separator.getCutValue());
        System.out.println("Cut set: "+separator.getCutSet());
        //The returned cut set is: {2,3,6}. This leads to the cut: \sum_{e\in \delta{2,3,6}} x_e >=2
    }


    public static void main(String[] args){
        System.out.println("Example 1:");
        Test test=new Test();
        test.example1();
    }

    /**
     * Simple factory class which produces integers as vertices
     */
    private class IntegerVertexFactory implements VertexFactory<Integer> {
        private int counter=0;
        @Override
        public Integer createVertex() {
            return new Integer(counter++);
        }

    }
}
