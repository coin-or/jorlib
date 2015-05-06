package org.jorlib.frameworks.columnGeneration.branchAndPrice.bapNodeComparators;

import org.jorlib.frameworks.columnGeneration.branchAndPrice.BAPNode;

import java.util.Comparator;

/**
 * Created by jkinable on 5/3/15.
 */
public class BFSbapNodeComparator implements Comparator<BAPNode>{
    @Override
    public int compare(BAPNode o1, BAPNode o2) {
        return Integer.compare(o1.nodeID, o2.nodeID);
    }
}
