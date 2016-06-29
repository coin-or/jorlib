package org.jorlib.demo.frameworks.columnGeneration.bapExample2.cg;

import org.jorlib.demo.frameworks.columnGeneration.bapExample2.model.ColoringGraph;
import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;

import java.util.Map;
import java.util.Set;

/**
 * Created by jkinable on 6/27/16.
 */
public final class IndependentSet extends AbstractColumn<ColoringGraph, ChromaticNumberPricingProblem>{

    //public final Map<Integer, Set<Integer>> colorGroups;
    public final Set<Integer> vertices;

    /**
     * Constructs a new column
     *
     * @param associatedPricingProblem Pricing problem to which this column belongs
     * @param isArtificial             Is this an artificial column?
     * @param creator                  Who/What created this column?
     */
    public IndependentSet(ChromaticNumberPricingProblem associatedPricingProblem, boolean isArtificial, String creator, Set<Integer> vertices) {
        super(associatedPricingProblem, isArtificial, creator);
        this.vertices=vertices;
    }


    @Override
    public boolean equals(Object o) {
        if(this==o)
            return true;
        else if(!(o instanceof IndependentSet))
            return false;
        IndependentSet other=(IndependentSet) o;
        return this.vertices.equals(other.vertices) && this.isArtificialColumn == other.isArtificialColumn;
    }

    @Override
    public int hashCode() {
        return vertices.hashCode();
    }

    @Override
    public String toString() {
        String s="Value: "+this.value+" artificial: "+isArtificialColumn+" set: "+vertices.toString();
        return s;
    }

}
