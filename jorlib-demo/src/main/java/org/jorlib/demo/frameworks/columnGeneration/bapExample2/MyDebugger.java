package org.jorlib.demo.frameworks.columnGeneration.bapExample2;

import org.jorlib.demo.frameworks.columnGeneration.bapExample2.cg.IndependentSet;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.AbstractBranchAndPrice;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling.NodeIsFractionalEvent;
import org.jorlib.frameworks.columnGeneration.io.SimpleDebugger;

import java.util.List;

/**
 * Created by jkinable on 6/28/16.
 */
public class MyDebugger extends SimpleDebugger{
    public MyDebugger(AbstractBranchAndPrice bap, boolean captureColumnGenerationEventsBAP) {
        super(bap, true);
    }

    @Override
    public void nodeIsFractional(NodeIsFractionalEvent nodeIsFractionalEvent) {
        super.nodeIsFractional(nodeIsFractionalEvent);
        for(IndependentSet is: (List<IndependentSet>)nodeIsFractionalEvent.node.getSolution())
            System.out.println(is);
    }
}
