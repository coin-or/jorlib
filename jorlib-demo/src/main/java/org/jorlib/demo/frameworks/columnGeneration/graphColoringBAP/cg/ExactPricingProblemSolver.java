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
 * ExactPricingProblemSolver.java
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
package org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.cg;

import ilog.concert.*;
import ilog.cplex.IloCplex;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.util.VertexPair;
import org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.bap.branching.branchingDecisions.DifferentColor;
import org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.bap.branching.branchingDecisions.SameColor;
import org.jorlib.demo.frameworks.columnGeneration.graphColoringBAP.model.ColoringGraph;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.pricing.AbstractPricingProblemSolver;
import org.jorlib.frameworks.columnGeneration.util.MathProgrammingUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Algorthm implementation which solves the pricing problem to optimality. This solver is based on an exact MIP implementation
 * using Cplex.
 *
 * @author Joris Kinable
 * @version 29-6-2016
 */
public final class ExactPricingProblemSolver extends AbstractPricingProblemSolver<ColoringGraph, IndependentSet, ChromaticNumberPricingProblem> {

    private IloCplex cplex; //Cplex instance.
    private IloObjective obj; //Objective function
    private IloIntVar[] vars; //Variables
    private Map<VertexPair<Integer>, IloConstraint> branchingConstraints; //Constraints added to enforce branching decisions

    /**
     * Creates a new solver instance for a particular pricing problem
     *
     * @param dataModel      data model
     * @param pricingProblem pricing problem
     */
    public ExactPricingProblemSolver(ColoringGraph dataModel, ChromaticNumberPricingProblem pricingProblem) {
        super(dataModel, pricingProblem);
        this.name="ExactMaxWeightedIndependentSetSolver";
        this.buildModel();
    }

    /**
     * Build the MIP model. Essentially this model calculates maximum weight independent sets.
     */
    private void buildModel(){
        try {
            cplex=new IloCplex();
            cplex.setParam(IloCplex.IntParam.AdvInd, 0);
            cplex.setParam(IloCplex.IntParam.Threads, 1);
            cplex.setOut(null);

            //Create the variables (a single variable per edge)
            vars=cplex.boolVarArray(dataModel.getNrVertices());

            //Create the objective function
            obj=cplex.addMaximize();

            //Create the constraints z_i+z_j <= 1 for all (i,j)\in E:
            for(DefaultEdge edge : dataModel.edgeSet())
                cplex.addLe(cplex.sum(vars[dataModel.getEdgeSource(edge)], vars[dataModel.getEdgeTarget(edge)]), 1);

            branchingConstraints=new HashMap<>();
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method which solves the pricing problem.
     * @return List of columns (independent sets) with negative reduced cost.
     * @throws TimeLimitExceededException TimeLimitExceededException
     */
    @Override
    protected List<IndependentSet> generateNewColumns() throws TimeLimitExceededException {
        List<IndependentSet> newPatterns=new ArrayList<>();
        try {
            //Compute how much time we may take to solve the pricing problem
            double timeRemaining=Math.max(1,(timeLimit-System.currentTimeMillis())/1000.0);
            cplex.setParam(IloCplex.DoubleParam.TiLim, timeRemaining); //set time limit in seconds

            //Solve the problem and check the solution nodeStatus
            if(!cplex.solve() || cplex.getStatus()!=IloCplex.Status.Optimal){
                if(cplex.getCplexStatus()==IloCplex.CplexStatus.AbortTimeLim){ //Aborted due to time limit
                    throw new TimeLimitExceededException();
                }else if(cplex.getStatus()==IloCplex.Status.Infeasible) { //Pricing problem infeasible
                    pricingProblemInfeasible=true;
                    this.objective=Double.MAX_VALUE;
                    throw new RuntimeException("Pricing problem infeasible");
                }else{
                    throw new RuntimeException("Pricing problem solve failed! Status: "+cplex.getStatus());
                }
            }else{ //Pricing problem solved to optimality.
                this.pricingProblemInfeasible=false;
                this.objective=cplex.getObjValue();

                if(objective >= 1 + config.PRECISION){ //Generate new column if it has negative reduced cost
                    double[] values=cplex.getValues(vars); //Get the variable values
                    //Create an Independent Set using all vertices with value 1 in the pricing problem
                    Set<Integer> vertices= IntStream.range(0, dataModel.getNrVertices()).filter(i->MathProgrammingUtil.doubleToBoolean(values[i])).boxed().collect(Collectors.toSet());
                    IndependentSet column=new IndependentSet(pricingProblem, false, this.getName(), vertices, 1);
                    newPatterns.add(column);
                }
            }

        }catch (IloException e) {
            e.printStackTrace();
        }
        return newPatterns;
    }

    /**
     * Update the objective function of the pricing problem with the new dual information.
     * The dual values are stored in the pricing problem.
     */
    @Override
    protected void setObjective() {
        try {
            obj.setExpr(cplex.scalProd(pricingProblem.dualCosts, vars));
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the pricing problem
     */
    @Override
    public void close() {
        cplex.end();
    }

    /**
     * Listen to branching decisions. The pricing problem is changed by the branching decisions.
     * @param bd BranchingDecision
     */
    @Override
    public void branchingDecisionPerformed(BranchingDecision bd) {
        try {
            if(bd instanceof SameColor){ //Ensure that two vertices appear together in an independent set
                SameColor sameColorDecision = (SameColor) bd;
                IloConstraint branchingConstraint=cplex.addEq(vars[sameColorDecision.vertexPair.getFirst()], vars[sameColorDecision.vertexPair.getSecond()]);
                branchingConstraints.put(sameColorDecision.vertexPair, branchingConstraint);
            }else if(bd instanceof DifferentColor){ //Ensure that two vertices do NOT appear together in an independent set.
                DifferentColor differentColorDecision= (DifferentColor) bd;
                IloConstraint branchingConstraint=cplex.addLe(cplex.sum(vars[differentColorDecision.vertexPair.getFirst()], vars[differentColorDecision.vertexPair.getSecond()]), 1);
                branchingConstraints.put(differentColorDecision.vertexPair, branchingConstraint);
            }
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    /**
     * When the Branch-and-Price algorithm backtracks, branching decisions are reversed.
     * @param bd BranchingDecision
     */
    @Override
    public void branchingDecisionReversed(BranchingDecision bd) {
        try {
            if(bd instanceof SameColor){
                SameColor sameColorDecision = (SameColor) bd;
                cplex.remove(branchingConstraints.get(sameColorDecision.vertexPair));
            }else if(bd instanceof DifferentColor){
                DifferentColor differentColorDecision= (DifferentColor) bd;
                cplex.remove(branchingConstraints.get(differentColorDecision.vertexPair));
            }
        } catch (IloException e) {
            e.printStackTrace();
        }
    }
}
