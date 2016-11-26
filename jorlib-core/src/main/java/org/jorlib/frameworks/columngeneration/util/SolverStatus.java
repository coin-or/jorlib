package org.jorlib.frameworks.columngeneration.util;

/**
 * Describes the state of the solver with respect to the status of the last solve.
 *
 * @author Rowan Hoogervorst
 */
public enum SolverStatus
{
    /** The problem has been solved to optimality. */
    OPTIMAL,
   
    /** THe problem turned out to be infeasible. */
    INFEASIBLE,
    
    /** A solution is available, but it is not necessarily optimal. */
    SOLUTION_AVAILABLE,
    
    /** No solve has been made to the model so-far. */
    UNDECIDED;
    
}
