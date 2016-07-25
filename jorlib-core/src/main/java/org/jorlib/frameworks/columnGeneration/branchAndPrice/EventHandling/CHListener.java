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
 * CHListener.java
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
package org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling;

import org.jorlib.frameworks.columnGeneration.master.cutGeneration.CutHandler;

import java.util.EventListener;

/**
 * Listener for events coming from the {@link CutHandler}
 *
 * @author Joris Kinable
 * @version 20-5-2015
 */
public interface CHListener extends EventListener {

    /**
     * Method invoked when inequalities are being separated
     * @param startGenerateCutsEvent startGenerateCutsEvent
     */
    void startGeneratingCuts(StartGeneratingCutsEvent startGenerateCutsEvent);

    /**
     * Method invoked when inequalities have been separated
     * @param finishGenerateCutsEvent finishGenerateCutsEvent
     */
    void finishGeneratingCuts(FinishGeneratingCutsEvent finishGenerateCutsEvent);

}
