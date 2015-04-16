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
 * SubtourInequality.java
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
package org.jorlib.demo.frameworks.columnGeneration.example2.cg.master.cuts;

import java.util.Set;

import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutGenerator;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.Inequality;

/**
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public class SubtourInequality extends Inequality{

	public final Set<Integer> cutSet;
	
	public SubtourInequality(CutGenerator maintainingGenerator, Set<Integer> cutSet) {
		super(maintainingGenerator);
		this.cutSet=cutSet;
	}

	@Override
	public boolean equals(Object o) {
		if(this==o)
			return true;
		else if(!(o instanceof SubtourInequality))
			return false;
		SubtourInequality other=(SubtourInequality)o;
		return this.cutSet.equals(other.cutSet);
	}

	@Override
	public int hashCode() {
		return cutSet.hashCode();
	}

}
