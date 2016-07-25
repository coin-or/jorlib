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
 * Configuration.java
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
package org.jorlib.frameworks.columnGeneration.util;

import org.jorlib.frameworks.columnGeneration.master.cutGeneration.AbstractCutGenerator;
import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutHandler;

import java.util.Properties;

/**
 * Singleton class providing configuration settings
 * 
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public class Configuration {
	
	/** Handle to the Configuration **/
	private static Configuration instance = null;
	
	/**
	 * Use default configuration
	 */
	protected Configuration(){
		//Global params
		MAXTHREADS = 4;
		PRECISION=0.000001;
		
		//CG & Branch-and-price params
		CUTSENABLED = true;
		EXPORT_MODEL=false;
		EXPORT_MASTER_DIR="./output/masterLP/";

		//Cut handling
		QUICK_RETURN_AFTER_CUTS_FOUND=true;
	}

	/**
	 * Use configuration from file. The properties file can contain one or more of the fields mentioned. Fields not included
	 * in the properties file will default to their default values.
	 * @param properties properties in file
	 */
	protected Configuration(Properties properties){

		//Global params
		MAXTHREADS=(properties.containsKey("MAXTHREADS") ? Integer.valueOf(properties.getProperty("MAXTHREADS")) : 4);
		PRECISION=(properties.containsKey("PRECISION") ? Double.valueOf(properties.getProperty("PRECISION")) : 0.000001);
		
		//CG & Branch-and-price params
		CUTSENABLED=(properties.containsKey("CUTSENABLED") ? Boolean.valueOf(properties.getProperty("CUTSENABLED")) : true );
		EXPORT_MODEL=(properties.containsKey("EXPORT_MODEL") ? Boolean.valueOf(properties.getProperty("EXPORT_MODEL")) : false);
		EXPORT_MASTER_DIR=(properties.containsKey("EXPORT_MODEL_DIR") ? properties.getProperty("EXPORT_MODEL_DIR") : "./output/masterLP/");

		//Cut handling
		QUICK_RETURN_AFTER_CUTS_FOUND=(properties.containsKey("QUICK_RETURN_AFTER_CUTS_FOUND") ? Boolean.valueOf(properties.getProperty("QUICK_RETURN_AFTER_CUTS_FOUND")) : true);
	}
	
	/**
	 * Returns the configuration
	 * @return Returns configuration
	 */
	public static Configuration getConfiguration(){
		if(instance == null)
			instance=new Configuration();
		return instance;
	}
	
	/**
	 * Read properties from a file
	 * @param properties file containing the configuration.
	 */
	public static void readFromFile(Properties properties){
		if(instance != null)
			throw new RuntimeException("You can only provide a configuration once");
		instance=new Configuration(properties);
	}


	//---------------- CONFIGURATION --------------------------
	
	/*
	 * Global parameters
	 */

	/** Number of threads used by the column generation procedure. Default: 4**/
	public final int MAXTHREADS;

	/** Precision parameter. DONT CHANGE THIS IF YOU ARE USING CPLEX! Default: 0.000001**/
	public final double PRECISION;


	/*
	 * Column generation & Branch-and-price configuration
	 */

	/** Enable/Disable generation of inequalities in master problem. Default: true **/
	public final boolean CUTSENABLED; 
	/** Define whether master problem should be written to .lp file. Default: false **/
	public final  boolean EXPORT_MODEL; 
	/** Define export directory for master models. Default: ./output/masterLP/ **/
	public final String EXPORT_MASTER_DIR;


	/**
	 * Cut handling
	 */

	/**
	 * The {@link CutHandler} invokes the {@link AbstractCutGenerator}(s) one by one to generate inequalities. When a particular cutGenerator does not yield any inequalities,
	 * the cutHandler will move on to the next registered cutGenerator. When quickReturnAfterCutsFound is set to true, the cutHandler
	 * will return as soon as any inequalities have been found. When set to false, all cutGenerators will be invoked. Default: true
	 */
	public final boolean QUICK_RETURN_AFTER_CUTS_FOUND;
}