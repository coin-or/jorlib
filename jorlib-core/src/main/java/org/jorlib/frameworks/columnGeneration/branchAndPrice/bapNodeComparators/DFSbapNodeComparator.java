package org.jorlib.frameworks.columnGeneration.branchAndPrice.bapNodeComparators;

import org.jorlib.frameworks.columnGeneration.branchAndPrice.BAPNode;

import java.util.Comparator;

/**
 * Created by jkinable on 5/3/15.
 *
 * Depth-First-Search comparator: The Branch-and-Price tree is processed in a DFSbapNodeComparator manner (DEFAULT)
 */
public class DFSbapNodeComparator implements Comparator<BAPNode>{
    @Override
    public int compare(BAPNode o1, BAPNode o2) {
        return -Integer.compare(o1.nodeID, o2.nodeID);
    }
}
