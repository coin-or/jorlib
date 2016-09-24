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
package org.jorlib.frameworks.columngeneration.master.cutGeneration;

import org.jorlib.frameworks.columngeneration.colgenmain.AbstractColumn;
import org.jorlib.frameworks.columngeneration.master.MasterData;
import org.jorlib.frameworks.columngeneration.model.ModelInterface;
import org.jorlib.frameworks.columngeneration.pricing.AbstractPricingProblem;

/**
 * Class representing a valid inequality.
 *
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public abstract class AbstractInequality<T extends ModelInterface, W extends MasterData<T, ? extends AbstractColumn<T, ?>, ? extends AbstractPricingProblem<T, ?>, ? >>
{

    /**
     * Reference to the AbstractCutGenerator which generates inequalities of the type that extends
     * this class
     **/
    public final AbstractCutGenerator<T, W> maintainingGenerator;

    /**
     * Creates a new inequality
     * 
     * @param maintainingGenerator Reference to the AbstractCutGenerator which generates
     *        inequalities of the type that extends this class
     */
    public AbstractInequality(AbstractCutGenerator<T, W> maintainingGenerator)
    {
        this.maintainingGenerator = maintainingGenerator;
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

}
