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
 * AbstractInequality.java
 * -----------------
 * (C) Copyright 2015, by Joris Kinable and Contributors.
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
package org.jorlib.frameworks.columnGeneration.master.cutGeneration;


/**
 * Class representing a valid inequality.
 *
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public abstract class AbstractInequality {

	/** Reference to the AbstractCutGenerator which generates inequalities of the type that extends this class **/
	public final AbstractCutGenerator maintainingGenerator;

	/**
	 * Creates a new inequality
	 * @param maintainingGenerator Reference to the AbstractCutGenerator which generates inequalities of the type that extends this class
	 */
	public AbstractInequality(AbstractCutGenerator maintainingGenerator){
		this.maintainingGenerator=maintainingGenerator;
	}

	@Override
	public abstract boolean equals(Object o);
	
	@Override
	public abstract int hashCode();
	
}
