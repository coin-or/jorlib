/* ==========================================
 * jORLib : Java Operations Research Library
 * ==========================================
 *
 * Project Info:  http://www.coin-or.org/projects/jORLib.xml
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015-2016, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 * as published by the Free Software Foundation.
 */
package org.jorlib.frameworks.columngeneration.colgenmain;

import org.jorlib.frameworks.columngeneration.model.ModelInterface;
import org.jorlib.frameworks.columngeneration.pricing.AbstractPricingProblem;

/**
 * Abstract Class modeling a column in the column generation procedure. Note that the fields in a
 * column (except the value assigned to it by the master problem) are all final: a column should NOT
 * be tempered with.
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public abstract class AbstractColumn<T extends ModelInterface, V extends AbstractPricingProblem<T, ? extends AbstractColumn<T,V>>>
{
    /**
     * The value of the column assigned to it by the last master problem solved which contained this
     * node
     **/
    public double value;
    /**
     * Indicates whether this column is volatile. Volatile columns are passed from one Branch-and-Price node to
     * another; instead, volatile columns are deleted once the node has been developed. Furthermore, an optimal
     * solution may NOT contain volatile columns. If, for some reason the optimal solution to a node does contain
     * volatile columns, it is assumed that the node is infeasible (the node will be pruned). Volatile columns
     * can be used to provide an initial feasible solution to the node; a volatile column may violate the definition
     * of a proper column, as defined by the Pricing Problem.
     *
     **/
    public final boolean isVolatile;
    /**
     * Textual description of the method which created this column, e.g a pricing problem, initial
     * solution, artificial, etc. Used for debugging purposes only to determine where a column is
     * coming from
     **/
    public final String creator;

    /** The pricing problem to which this column belongs **/
    public final V associatedPricingProblem;

    /**
     * Constructs a new column
     * 
     * @param associatedPricingProblem Pricing problem to which this column belongs
     * @param isVolatile Is this a volatile column?
     * @param creator Who/What created this column?
     */
    public AbstractColumn(V associatedPricingProblem, boolean isVolatile, String creator)
    {
        this.creator = creator;
        this.isVolatile = isVolatile;
        this.associatedPricingProblem = associatedPricingProblem;
    }

    /**
     * Compares two columns mutually.
     */
    public abstract boolean equals(Object o);

    /**
     * Creates a hashCode for the given column
     */
    public abstract int hashCode();

    /**
     * Gives a textual representation of a column
     */
    public abstract String toString();

}
