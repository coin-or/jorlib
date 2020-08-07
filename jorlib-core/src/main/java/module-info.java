module jorlib.core {
    exports org.jorlib.alg.knapsack;
    exports org.jorlib.alg.knapsack.separation;
    exports org.jorlib.alg.packing.circlepacking;
    exports org.jorlib.alg.packing.circlepacking.util;
    exports org.jorlib.alg.tsp.separation;
    exports org.jorlib.frameworks.columngeneration.branchandprice;
    exports org.jorlib.frameworks.columngeneration.branchandprice.bapnodecomparators;
    exports org.jorlib.frameworks.columngeneration.branchandprice.branchingdecisions;
    exports org.jorlib.frameworks.columngeneration.branchandprice.eventhandling;
    exports org.jorlib.frameworks.columngeneration.colgenmain;
    exports org.jorlib.frameworks.columngeneration.io;
    exports org.jorlib.frameworks.columngeneration.master;
    exports org.jorlib.frameworks.columngeneration.master.cutGeneration;
    exports org.jorlib.frameworks.columngeneration.model;
    exports org.jorlib.frameworks.columngeneration.pricing;
    exports org.jorlib.frameworks.columngeneration.util;
    exports org.jorlib.io.tsplibreader;
    exports org.jorlib.io.tsplibreader.distanceFunctions;
    exports org.jorlib.io.tsplibreader.fieldtypesandformats;
    exports org.jorlib.io.tsplibreader.graph;

    requires transitive org.slf4j;
    requires transitive com.google.common;
    requires transitive jgrapht.core;
    requires java.desktop;
}