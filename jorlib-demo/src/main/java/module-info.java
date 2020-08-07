module jorlib.demo {

    exports org.jorlib.demo.alg.knapsack;
    exports org.jorlib.demo.alg.knapsack.separation;
    exports org.jorlib.demo.alg.packing.circlepacking;
    exports org.jorlib.demo.alg.tsp.separation;
    exports org.jorlib.demo.frameworks.columngeneration.cuttingstockcg;
    exports org.jorlib.demo.frameworks.columngeneration.cuttingstockcg.cg;
    exports org.jorlib.demo.frameworks.columngeneration.cuttingstockcg.model;
    exports org.jorlib.demo.frameworks.columngeneration.graphcoloringbap;
    exports org.jorlib.demo.frameworks.columngeneration.graphcoloringbap.bap;
    exports org.jorlib.demo.frameworks.columngeneration.graphcoloringbap.bap.branching;
    exports org.jorlib.demo.frameworks.columngeneration.graphcoloringbap.bap.branching.branchingDecisions;
    exports org.jorlib.demo.frameworks.columngeneration.graphcoloringbap.cg;
    exports org.jorlib.demo.frameworks.columngeneration.graphcoloringbap.cg.master;
    exports org.jorlib.demo.frameworks.columngeneration.graphcoloringbap.model;

    exports org.jorlib.demo.frameworks.columngeneration.tspbap;
    exports org.jorlib.demo.frameworks.columngeneration.tspbap.bap;
    exports org.jorlib.demo.frameworks.columngeneration.tspbap.bap.branching;
    exports org.jorlib.demo.frameworks.columngeneration.tspbap.bap.branching.branchingDecisions;
    exports org.jorlib.demo.frameworks.columngeneration.tspbap.cg;
    exports org.jorlib.demo.frameworks.columngeneration.tspbap.cg.master;
    exports org.jorlib.demo.frameworks.columngeneration.tspbap.cg.master.cuts;
    exports org.jorlib.demo.frameworks.columngeneration.tspbap.model;

    exports org.jorlib.demo.frameworks.columngeneration.tspcg;
    exports org.jorlib.demo.frameworks.columngeneration.tspcg.cg;
    exports org.jorlib.demo.frameworks.columngeneration.tspcg.cg.master;
    exports org.jorlib.demo.frameworks.columngeneration.tspcg.cg.master.cuts;
    exports org.jorlib.demo.frameworks.columngeneration.tspcg.model;

    exports org.jorlib.demo.io.tsplibreader;

    requires transitive jorlib.core;
    requires cplex;
    requires java.desktop;
    requires jgrapht.ext;
//    requires ilog.concert;
}