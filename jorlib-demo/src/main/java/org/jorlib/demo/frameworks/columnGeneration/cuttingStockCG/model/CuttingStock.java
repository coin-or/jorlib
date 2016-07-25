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
 * CuttingStock.java
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
package org.jorlib.demo.frameworks.columnGeneration.cuttingStockCG.model;

import org.jorlib.frameworks.columnGeneration.model.ModelInterface;

/**
 * Define a Cutting Stock problem
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public final class CuttingStock implements ModelInterface{

	public final int nrFinals=4; //Number of different finals
	public final int rollWidth=100; //Width of the raws
	public final int[] finals={45, 36, 31, 14}; //Size of the finals
	public final int[] demandForFinals={97, 610, 395, 211}; //Requested quantity of each final

	@Override
	public String getName() {
		return "CuttingStockExample";
	}
}
