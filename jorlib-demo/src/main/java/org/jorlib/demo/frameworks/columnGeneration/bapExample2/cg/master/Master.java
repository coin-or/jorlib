package org.jorlib.demo.frameworks.columnGeneration.bapExample2.cg.master;

import ilog.concert.*;
import ilog.cplex.IloCplex;
import org.jorlib.demo.frameworks.columnGeneration.bapExample2.cg.ChromaticNumberPricingProblem;
import org.jorlib.demo.frameworks.columnGeneration.bapExample2.cg.IndependentSet;
import org.jorlib.demo.frameworks.columnGeneration.bapExample2.model.ColoringGraph;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;
import org.jorlib.frameworks.columnGeneration.io.TimeLimitExceededException;
import org.jorlib.frameworks.columnGeneration.master.AbstractMaster;
import org.jorlib.frameworks.columnGeneration.master.OptimizationSense;
import org.jorlib.frameworks.columnGeneration.util.OrderedBiMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jkinable on 6/27/16.
 */
public class Master extends AbstractMaster<ColoringGraph, IndependentSet, ChromaticNumberPricingProblem, ColoringMasterData> {

    private IloObjective obj; //Objective function
    private IloRange[] oneColorPerVertex; //Constraint

    public Master(ColoringGraph dataModel, ChromaticNumberPricingProblem pricingProblem) {
        super(dataModel, pricingProblem, OptimizationSense.MINIMIZE);
        System.out.println("Master constructor. Columns: "+masterData.getNrColumns());
    }

    @Override
    protected ColoringMasterData buildModel() {
        IloCplex cplex=null;

        try {
            cplex=new IloCplex(); //Create cplex instance
            cplex.setOut(null); //Disable cplex output
            cplex.setParam(IloCplex.IntParam.Threads,config.MAXTHREADS); //Set number of threads that may be used by the master

            //Define objectiveMasterProblem
            obj=cplex.addMinimize();

            //Define constraints
            oneColorPerVertex=new IloRange[dataModel.getNrVertices()];
            for(int i=0; i<dataModel.getNrVertices(); i++)
                oneColorPerVertex[i]=cplex.addRange(1, Double.MAX_VALUE, "oneColorPerVertex"); //Assign one color to every vertex

        } catch (IloException e) {
            e.printStackTrace();
        }

        Map<ChromaticNumberPricingProblem, OrderedBiMap<IndependentSet, IloNumVar>> varMap=new LinkedHashMap<>();
        ChromaticNumberPricingProblem pricingProblem=this.pricingProblems.get(0);
        varMap.put(pricingProblem, new OrderedBiMap<>());

        //Create a new data object which will store information from the master.
        return new ColoringMasterData(cplex, pricingProblem, varMap);
    }

    @Override
    protected boolean solveMasterProblem(long timeLimit) throws TimeLimitExceededException {
        try {
            //Set time limit
            double timeRemaining=Math.max(1,(timeLimit-System.currentTimeMillis())/1000.0);
            masterData.cplex.setParam(IloCplex.DoubleParam.TiLim, timeRemaining); //set time limit in seconds
            //Potentially export the model
            if(config.EXPORT_MODEL) masterData.cplex.exportModel(config.EXPORT_MASTER_DIR+"master_"+this.getIterationCount()+".lp");

            //Solve the model
            if(!masterData.cplex.solve() || masterData.cplex.getStatus()!=IloCplex.Status.Optimal){
                if(masterData.cplex.getCplexStatus()==IloCplex.CplexStatus.AbortTimeLim) //Aborted due to time limit
                    throw new TimeLimitExceededException();
                else
                    throw new RuntimeException("Master problem solve failed! Status: "+masterData.cplex.getStatus());
            }else{
                masterData.objectiveValue=masterData.cplex.getObjValue();
            }
        } catch (IloException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void initializePricingProblem(ChromaticNumberPricingProblem pricingProblem) {
        try {
            double[] dualValues=masterData.cplex.getDuals(oneColorPerVertex); //Dual value per vertex
            pricingProblem.initPricingProblem(dualValues);
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addColumn(IndependentSet column) {
        try{
            //Register column with objectiveMasterProblem
            IloColumn iloColumn=masterData.cplex.column(obj,1);
            //Register column with edgeOnlyUsedOnce constraints
            for(Integer vertex: column.vertices)
                iloColumn = iloColumn.and(masterData.cplex.column(oneColorPerVertex[vertex], 1));

            //Create the variable and store it
            IloNumVar var=masterData.cplex.numVar(iloColumn, 0, Double.MAX_VALUE, "x_"+masterData.getNrColumns());
            masterData.cplex.add(var);
            masterData.addColumn(column,var);
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<IndependentSet> getSolution() {
        List<IndependentSet> solution=new ArrayList<>();
        try {
            IndependentSet[] independentSets=masterData.getColumnsForPricingProblemAsList().toArray(new IndependentSet[masterData.getNrColumns()]);
            IloNumVar[] vars=masterData.getVarMap().getValuesAsArray(new IloNumVar[masterData.getNrColumns()]);
            double[] values=masterData.cplex.getValues(vars);
            for(int i=0; i<independentSets.length; i++){
                independentSets[i].value=values[i];
                if(values[i]>=config.PRECISION)
                    solution.add(independentSets[i]);
            }
        } catch (IloException e) {
            e.printStackTrace();
        }
        return solution;
    }

    @Override
    public void printSolution() {
        List<IndependentSet> solution=this.getSolution();
        for(IndependentSet is : solution)
            System.out.println(is);
    }

    @Override
    public void close() {
        masterData.cplex.end();
    }

    /**
     * Listen to branching decisions
     * @param bd Branching decision
     */
    @Override
    public void branchingDecisionPerformed(BranchingDecision bd) {
        //For simplicity, we simply destroy the master problem and rebuild it. Of course, something more sophisticated may be used which retains the master problem.
        this.close(); //Close the old cplex model
        masterData=this.buildModel(); //Create a new model without any initialColumns
    }

    /**
     * Undo branching decisions during backtracking in the Branch-and-Price tree
     * @param bd Branching decision
     */
    @Override
    public void branchingDecisionReversed(BranchingDecision bd) {
        //No action required
    }
}
