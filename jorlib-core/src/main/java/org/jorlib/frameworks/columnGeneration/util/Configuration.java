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
		MAXTHREADS = 3;
		PRECISION=0.000001;
		
		//CG & Branch-and-price params
		CUTSENABLED = true;
		EXPORT_MODEL=false;
		EXPORT_MASTER_DIR="./output/masterLP/";
	}

	/**
	 * Use configuration from file
	 * @param properties properties in file
	 */
	protected Configuration(Properties properties){
		//Global params
		MAXTHREADS=Integer.valueOf(properties.getProperty("MAXTHREADS"));
		PRECISION=Double.valueOf(properties.getProperty("PRECISION"));
		
		//CG & Branch-and-price params
		CUTSENABLED=Boolean.valueOf(properties.getProperty("CUTSENABLED"));
		EXPORT_MODEL=Boolean.valueOf(properties.getProperty("EXPORT_MODEL"));
		EXPORT_MASTER_DIR=properties.getProperty("EXPORT_MODEL_DIR");
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

	/** Number of threads used by the column generationo procedure. Default: 3**/
	public final int MAXTHREADS;

	/** Precision parameter. DONT CHANGE THIS IF YOU ARE USING CPLEX! Default: 0.000001**/
	public final double PRECISION;


	/*
	 * Column generation & Branch-and-price configuration
	 */

	/** Enable/Disable generation of cuts in master problem. Default: true **/
	public final boolean CUTSENABLED; 
	/** Define whether master problem should be written to .lp file. Default: false **/
	public final  boolean EXPORT_MODEL; 
	/** Define export directory for master models. Default: ./output/masterLP/ **/
	public final String EXPORT_MASTER_DIR;
	
}