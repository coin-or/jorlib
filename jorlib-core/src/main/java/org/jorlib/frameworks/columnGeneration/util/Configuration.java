package org.jorlib.frameworks.columnGeneration.util;

import java.util.Properties;

/**
 * Singleton class providing configuration data
 * @author Joris Kinable
 * @version 13-4-2015
 *
 */
public class Configuration {
	
	//Handle to the Configuration
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
		WRITE_STATS = false;
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
		WRITE_STATS=Boolean.valueOf(properties.getProperty("WRITE_STATS"));
	}
	
	/**
	 * @return Returns configuration
	 */
	public static Configuration getConfiguration(){
		if(instance == null)
			instance=new Configuration();
		return instance;
	}
	
	/**
	 * Read properties from a file
	 * @param Properties file containing the configuration.
	 */
	public static void readFromFile(Properties properties){
		if(instance != null)
			throw new RuntimeException("You can only provide a configuration once");
		instance=new Configuration(properties);
	}
	
	
	
	//---------------- CONFIGURATION --------------------------
	
	/**
	 * Global parameters
	 */
	public final int MAXTHREADS; //Number of threads used by the column generationo procedure
	public final double PRECISION; //Precision parameter. DONT CHANGE THIS IF YOU ARE USING CPLEX!
	
	/**
	 * Column generation & Branch-and-price configuration
	 */
	//Enable/Disable generation of cuts in master problem.
	public final boolean CUTSENABLED; 
	//Define whether master problem should be written to .lp file
	public final  boolean EXPORT_MODEL; 
	//Define export directory for master models
	public final String EXPORT_MASTER_DIR;
	//Define whether statistics should be written to output file
	public final boolean WRITE_STATS; 
	
}