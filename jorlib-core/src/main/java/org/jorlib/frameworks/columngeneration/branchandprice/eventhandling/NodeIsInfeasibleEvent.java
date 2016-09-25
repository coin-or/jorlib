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
package org.jorlib.frameworks.columngeneration.branchandprice.eventhandling;

import org.jorlib.frameworks.columngeneration.branchandprice.BAPNode;
import org.jorlib.frameworks.columngeneration.colgenmain.AbstractColumn;
import org.jorlib.frameworks.columngeneration.model.ModelInterface;
import org.jorlib.frameworks.columngeneration.pricing.AbstractPricingProblem;

import java.util.EventObject;

/**
 * Event generated when Branch-and-Price node is infeasible
 *
 * @author Joris Kinable
 * @version 5-5-2015
 */
public class NodeIsInfeasibleEvent<T extends ModelInterface, U extends AbstractColumn<T, ? extends AbstractPricingProblem<T,U>>>
    extends EventObject
{

    private static final long serialVersionUID = -8253342574155352760L;

    /** Node which is infeasible **/
    public final BAPNode<T,U> node;

    /**
     * Creates a new NodeIsInfeasibleEvent
     * 
     * @param source Generator of the event
     * @param node Node which is infeasible
     */
    public NodeIsInfeasibleEvent(Object source, BAPNode<T,U> node)
    {
        super(source);
        this.node = node;
    }
}
