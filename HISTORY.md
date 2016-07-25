# HISTORY #

Changes to jORLib in each version:

- **version 1.1.1** (24-Jul-2016):
	- Fixed a bug where the number of processed nodes in BAP was not calculated correctly (pruning nodes did not increment the nodes processed counter)
	- Added BAP example GraphColoring
	- Fixed a bug where an externally computed bound on an optimization problem was completely ignored by BAP/CG. For example, the size of a maximum clique forms a valid bound for the chromatic number of a graph. This bound, although passed correctly to BAP, would not be taken into account.
	- Fixed a bug where in a maximization problem a bound computed through the function calculateBoundOnMasterObjective(solver) would be ignored because the optimization sense was not taken into account correctly.
	- Disabled an exclude in the main pom.xml file; this exclude seemed to cause AllFrameworksTests.java to fail when the jORLib was loaded as a maven project in Eclipse. [reported by Gabor Marotig]
	- Added method to query the depth of a BAPNode; included a method to initialize BAPNode
	- Switched license to LGPLv2.1

- **version 1.1** (24-Feb-2016): 
	- Fixed a rare bug which could cause the Branch-and-Price procedure to end prematurely.
	- Fixed an issue where seperating cuts was not counted in the computation time of the master problem
	-When running Branch-and-Price, you can now also query the objective of the root node without having to re-solve
	- Fixed a bug in AbstractBranchAndPrice where hasSolution() could return a nullpointer.
	- Fixed: the bound on BAP instances solved to optimality was always 0, instead of the optimal solution

- **version 1.0** (April-2015) : Initial public release.

