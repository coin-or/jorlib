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
package org.jorlib.frameworks.columngeneration.branchandprice.eventhandling;

import org.jorlib.frameworks.columngeneration.master.cutGeneration.CutHandler;

import java.util.EventListener;

/**
 * Listener for events coming from the {@link CutHandler}
 *
 * @author Joris Kinable
 * @version 20-5-2015
 */
public interface CHListener
    extends EventListener
{

    /**
     * Method invoked when inequalities are being separated
     * 
     * @param startGenerateCutsEvent startGenerateCutsEvent
     */
    void startGeneratingCuts(StartGeneratingCutsEvent startGenerateCutsEvent);

    /**
     * Method invoked when inequalities have been separated
     * 
     * @param finishGenerateCutsEvent finishGenerateCutsEvent
     */
    void finishGeneratingCuts(FinishGeneratingCutsEvent finishGenerateCutsEvent);

}