# HISTORY #

Changes to jORLib in each version:


- **version 1.1** (24-Feb-2016): 
	- Fixed a rare bug which could cause the Branch-and-Price procedure to end prematurely.
	- Fixed an issue where seperating cuts was not counted in the computation time of the master problem
	-When running Branch-and-Price, you can now also query the objective of the root node without having re-solve
	-Fixed a bug in AbstractBranchAndPrice where hasSolution() could return a nullpointer.
	-Fixed: the bound on BAP instances solved to optimality was always 0, instead of the optimal solution

- **version 1.0** (April-2015) : Initial public release.

