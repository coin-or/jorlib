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
 * MasterData.java
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
package org.jorlib.frameworks.columnGeneration.master;

/**
 * This is a data object which is being managed by the Master problem. The same data object is passed
 * to the cutHandlers. Therefore, the object can be used to pass information from the master problem to
 * the classes which separate valid inequalities. 
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public class MasterData {

	//Objective value of the current master problem
	public double objectiveValue;
	//Number of times the master problem has been solved
	public int iterations=0;
	//Indicates whether the master problem has been solved to optimality
	public boolean optimal=false;
}
