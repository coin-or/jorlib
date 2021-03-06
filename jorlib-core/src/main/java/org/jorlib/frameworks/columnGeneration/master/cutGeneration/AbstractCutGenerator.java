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
 * AbstractCutGenerator.java
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

import java.util.List;

import org.jorlib.frameworks.columnGeneration.master.MasterData;
import org.jorlib.frameworks.columnGeneration.model.ModelInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is used to separate valid inequalities. It can access data from the the master problem through a
 * {@link MasterData} object. Each time inequalities have to be separated, the {@link #generateInqualities() generateInqualities} method is invoked. Given
 * the data from the master, the AbstractCutGenerator can then check whether there are any violated inequalities.
 * Typically, one wants to store any inequalities generated for a particular master problem in the corresponding MasterData object
 * such that the master problem has access to the inequalities.
 *
 * @author Joris Kinable
 * @version 13-4-2015
 *
 * @param <T> Type of data model
 * @param <W> Type of MasterData
 */
public abstract class AbstractCutGenerator<T extends ModelInterface, W extends MasterData> {

	/** Logger for this class **/
	protected final Logger logger = LoggerFactory.getLogger(AbstractCutGenerator.class);

	/** Data model **/
	protected final T dataModel;
	/** Data object coming from the master **/
	protected W masterData;
	/** Name of the generator **/
	protected final String name;

	/**
	 * Creates a new AbstractCutGenerator
	 * @param dataModel data model instance
	 * @param name name of the CutGenerator
	 **/
	public AbstractCutGenerator(T dataModel, String name){
		this.dataModel = dataModel;
		this.name=name;
	}
	
	/**
	 * Separate valid inequalities
	 * @return returns a list of violated inequality which have been found
	 */
	public abstract List<AbstractInequality> generateInqualities();
	
	/**
	 * Add an inequality of the type generated by this AbstractCutGenerator to the model. The inequality may have been generated elsewhere, e.g. by a different node,
	 * or the user may provide an initial set of inequalities.
	 * @param cut cut to be added
	 */
	public abstract void addCut(AbstractInequality cut);

	/**
	 * Returns all inequalities maintained by this generator
	 * @return list of inequalities maintained by the generator
	 */
	public abstract List<AbstractInequality> getCuts();
	
	/**
	 * Set the data object containing data from the master problem
	 * @param masterData masterData
	 */
	public void setMasterData(W masterData){
		this.masterData=masterData;
	}
	
	/**
	 * Close the generator
	 */
	public abstract void close();

	public String toString(){
		return name;
	}
}
