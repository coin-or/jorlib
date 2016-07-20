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
 * CGListener.java
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

import org.jorlib.frameworks.columnGeneration.colgenMain.AbstractColumn;

import java.util.EventListener;

/**
 * Listener for Column Generation events
 *
 * @author Joris Kinable
 * @version 20-5-2015
 */
public interface CGListener extends EventListener {

    /**
     * Method invoked when column generation is started
     * @param startEvent startEvent
     */
    void startCG(StartEvent startEvent);

    /**
     * Method invoked when column generation is finished (either the optimal solution has been found, or the process is terminated due to a time limit)
     * @param finishEvent startBAPEvent
     */
    void finishCG(FinishEvent finishEvent);

    /**
     * Method invoked when CG starts solving the master
     * @param startMasterEvent startMasterEvent
     */
    void startMaster(StartMasterEvent startMasterEvent);

    /**
     * Method invoked when CG finished solving the master
     * @param finishMasterEvent finishMasterEvent
     */
    void finishMaster(FinishMasterEvent finishMasterEvent);

    /**
     * Method invoked when CG starts solving the pricing problem
     * @param startPricing startPricing
     */
    void startPricing(StartPricingEvent startPricing);

    /**
     * Method invoked when CG finished the pricing problem
     * @param finishPricingEvent finishPricingEvent
     */
    void finishPricing(FinishPricingEvent finishPricingEvent);

    /**
     * Method invoked when the column generation process is terminated due to a time out
     * @param timeLimitExceededEvent timeLimitExceededEvent
     */
    void timeLimitExceeded(TimeLimitExceededEvent timeLimitExceededEvent);
}
