/* ==========================================
 * jORLib : Java Operations Research Library
 * ==========================================
 *
 * Project Info:  http://www.coin-or.org/projects/jORLib.xml
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2017-2017, by Rowan Hoogervorst and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 * as published by the Free Software Foundation.
 */
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
