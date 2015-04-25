package org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.bap;

import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.bap.branchingDecisions.FixEdge;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.bap.branchingDecisions.RemoveEdge;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.Matching;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.cg.PricingProblemByColor;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.model.Edge;
import org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample.model.TSP;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.AbstractBranchCreator;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.BAPNode;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.Inequality;
import org.jorlib.frameworks.columnGeneration.util.CplexUtil;

import java.util.*;

/**
 * Created by jkinable on 4/22/15.
 */
public class BranchCreator extends AbstractBranchCreator<TSP, Matching, PricingProblemByColor>{

    private PricingProblemByColor pricingProblemForMatching;
    private Edge edgeForBranching;
    private double bestEdgeValue=0;

    public BranchCreator(TSP modelData, List<PricingProblemByColor> pricingProblems){
        super(modelData, pricingProblems);
    }

    @Override
    protected boolean canPerformBranching(List<Matching> solution) {
        //For each color, determine whether there's a fractional edge for branching
        Map<PricingProblemByColor,Map<Edge, Double>> edgeValueMap=new HashMap<>();
        for(PricingProblemByColor pricingProblem : pricingProblems)
            edgeValueMap.put(pricingProblem, new LinkedHashMap<>());

        //Aggregate edge values
        for(Matching matching : solution){
            for(Edge edge : matching.edges){
                Double edgeValue=edgeValueMap.get(matching.associatedPricingProblem).get(edge);
                if(edgeValue == null)
                    edgeValueMap.get(matching.associatedPricingProblem).put(edge,matching.value);
                else
                    edgeValueMap.get(matching.associatedPricingProblem).put(edge,matching.value+edgeValue);
            }
        }

        for(PricingProblemByColor pricingProblem : pricingProblems){
            Map<Edge, Double> edgeValues=edgeValueMap.get(pricingProblem);
            for(Edge edge : edgeValues.keySet()){
                double value=edgeValues.get(edge);
                if(Math.abs(0.5-value) < Math.abs(0.5-bestEdgeValue)){
                    pricingProblemForMatching=pricingProblem;
                    edgeForBranching=edge;
                    bestEdgeValue=value;
                }
            }
        }
        return CplexUtil.isFractional(bestEdgeValue);
    }

    @Override
    protected List<BAPNode<TSP,Matching>> getBranches(BAPNode<TSP,Matching> parentNode, List<Matching> solution, List<Inequality> cuts) {
        //Left branch: fix the edge:
        FixEdge branchingDecision1=new FixEdge(pricingProblemForMatching, edgeForBranching);
        BAPNode node1=this.createBranch(parentNode, branchingDecision1, solution, cuts);

        //Right branch: remove the edge:
        RemoveEdge branchingDecision2=new RemoveEdge(pricingProblemForMatching, edgeForBranching);
        BAPNode node2=this.createBranch(parentNode, branchingDecision2, solution, cuts);

        return Arrays.asList(node1,node2);
    }
}
