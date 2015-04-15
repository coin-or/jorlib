/* ==========================================
 * jORLib : a free Java OR library
 * ==========================================
 *
 * Project Info:  https://github.com/jkinable/jorlib
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under GPLv3
 *
 */
/* -----------------
 * Inequality.java
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
package org.jorlib.frameworks.columnGeneration.master.cuts;

import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutGenerator;

/**
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public abstract class Inequality {
	
	public final CutGenerator maintainingGenerator;
	
	public Inequality(CutGenerator maintainingGenerator){
		this.maintainingGenerator=maintainingGenerator;
	}
	
	@Override
	public abstract boolean equals(Object o);
	
	@Override
	public abstract int hashCode();
	
}
