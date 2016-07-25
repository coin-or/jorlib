# jORLib
Java Operations Research Library

Released: April, 2015</p>

Written by Joris Kinable and Contributors

(C) Copyright 2015-2016, by [Joris Kinable](mailto:jkinable@cs.cmu.edu) and Contributors. All rights
reserved.

Please address all contributions, suggestions, and inquiries to the current project administrator [Joris Kinable](mailto:jkinable@cs.cmu.edu)

- The website of this project where the latest version of jORLib can be downloaded: [http://jkinable.github.io/jorlib/](http://jkinable.github.io/jorlib/)

## Introduction ##

jORLib is a Java class library that provides implementations for Operations Research problems. The code requires JDK 1.8 or later, and is released under the terms of LGPLv2.1. Many of the implementations are derived from journal papers and books. jORLib currently includes a framework for Column Generation and Branch-and-Price, routines to separate valid inequalities (knapsack, TSP), parsers for TSPLib instances, as well as several other useful algorithmic implementations. To simplify working with jORLib, documentation is provided, as well as an extensive set of working examples.

A copy of the [LGPLv2.1](LICENSE) is included in the download.

Please note that jORLib is distributed WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

Please refer to the license for details.


## Getting Started ##

The package `org.jorlib.demo` includes small demo applications to help you get started. If you spawn your own demo app and think others can benefit from it, please send it to us and we will add it to that package.</p>
Running examples directly from the demo package is easy. Try for example:
- "java -cp jorlib-demo-<VERSION>-uber.jar org.jorlib.demo.alg.tsp.separation.SubtourSeparatorDemo" to run the SubtourSeparatorDemo
or
- "java -Djava.library.path=/opt/ILOG/CPLEX_Studio1261/cplex/bin/x86-64_linux/ -cp jorlib-demo-<VERSION>-uber.jar org.jorlib.demo.frameworks.columnGeneration.bapExample.TSPSolver" to run the Branch-and-Price example. Obviously, you need to ensure that java.library.path points to your own Cplex installation.

## Documentation ##
Javadoc for the latest version can be found [here](http://jkinable.github.io/jorlib/apidocs/); documentation per version is bundled with every release. Releases can be found on our [release page](https://github.com/jkinable/jorlib/releases). There is also a [manual](http://jkinable.github.io/jorlib/manual/manual.pdf) with an in-depth discussion on the Column Generation and Branch-and-Price features of the library.

## Dependencies ##

- jORLib requires JDK 1.8 or later to build.
- [JUnit](http://www.junit.org) is a unit testing framework. You need JUnit only if you want to run the unit tests.  JUnit is licensed under the terms of the IBM Common Public License.  The JUnit tests included with jORLib have been created using JUnit `4.12`.
- [jGraphT](http://jgrapht.org/) is a free Java graph library that provides mathematical graph-theory objects and algorithms and supports various types of graphs. jGraphT is required to model several problems, such as the TSP network for the subtour elimination algorithm. jGraphT version 0.9.0 is used in jORLib. 
- [Guava](https://code.google.com/p/guava-libraries/) The Guava project contains several of Googles core libraries for Java-based projects: collections, caching, primitives support, concurrency libraries, common annotations, string processing, I/O, and so forth. Guava version 14.0.1 is needed for the Branch-and-Price framework.
- [Logback](http://logback.qos.ch/) Provides logging fascilities. logback-classic-0.9.28.jar and logback-core-0.9.28.jar are required for the Branch-and-Price framework.
- [Slf4j](http://www.slf4j.org/) The Simple Logging Facade for Java (SLF4J) serves as a simple facade or abstraction for various logging frameworks (e.g. java.util.logging, logback, log4j) allowing the end user to plug in the desired logging framework at deployment time. slf4j-api-1.6.1.jar is required for the Branch-and-Price framework.
- OPTIONAL: IBM Cplex. If you want to run the Column Generation and Branch-and-Price examples in the demo package required a CPLEX installation; this is installation is not required for jorlib-core package, or for any of the other examples.
The demo package jorlib-demo-1.0-uber.jar contains all the required dependencies. The above packages are only required if you plan to compile the source code yourself. 

## Online Resources ##

Source code is hosted on [github](https://github.com/jkinable/jorlib). You can send contributions as pull requests there. If you intend to contribute code, please use the development branch as this branch contains the latest version of the code. The master branch always contains the source code of the latest stable release.

## Your Improvements ##
Your contributions are highly welcome. Literally anything related to OR will be considered:
- Heuristic/exact algorithms related to frequently reoccurring problems, preferably backed by a scientific paper or book
- Visualization tools
- Readers/parsers for common IO such as TSPLib instances
- etc
If you add improvements to jORLib please send them to us as pull requests on github. We will add them to the next release so that everyone can enjoy them. You might also benefit from it: others may fix bugs in your source files or may continue to enhance them.

## Using jORLib ##
If you are using jORLib in your work, you are cordially invited to drop a note on our mailing lists (soon to come), describing your work and what you used jORLib for. This will give us a better understanding of how the Library is used and what people's interests are. Feel free to report any issues, feature requests, etc on our issue tracker on Github (https://github.com/jkinable/jorlib/issues).

If you are using jORLib in your research, products, etc, please include a reference to the library; this increases the visibility of the project and may attract additional developers.

## Thanks ##

With regards from

[Joris Kinable](mailto:jkinable@cs.cmu.edu), jORLib Project Creator

