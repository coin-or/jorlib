# jORLib
Java OR Library

Released: April, 2015</p>

Written by Joris Kinable and Contributors

(C) Copyright 2015, by [Joris Kinable](mailto:jkinable@cs.cmu.edu) and Contributors. All rights
reserved.

Please address all contributions, suggestions, and inquiries to the current project administrator [Joris Kinable](mailto:joris.kinable@kuleuven.be)

## Introduction ##

jORLib is a Java class library that provides implementations for Operations Research problems. The code requires JDK 1.8 or later, and is released under the terms of GPLv3. Many of the implementations are derived from journal papers/books and include frequently reoccuring problems such as separation algorithms to solve knapsack, TSP, packing problems, separation algorithms for valid initialInequalities, etc.

A copy of the [GPLv3](LICENSE) is included in the download.

Please note that jORLib is distributed WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

Please refer to the license for details.


## Getting Started ##

The package `org.jorlib.demo` includes small demo applications to help you get started. If you spawn your own demo app and think others can use it, please send it to us and we will add it to that package.


## Documentation ##

Extensive javadocs can be found for each version on the [release page](https://github.com/jkinable/jorlib/releases)

## Dependencies ##

- jORLib requires JDK 1.8 or later to build.
- [JUnit](http://www.junit.org) is a unit testing framework. You need JUnit only if you want to run the unit tests.  JUnit is licensed under the terms of the IBM Common Public License.  The JUnit tests included with jORLib have been created using JUnit `4.12`.
- [jGraphT](http://jgrapht.org/) is a free Java graph library that provides mathematical graph-theory objects and algorithms and supports various types of graphs. jGraphT is required to model several problems, such as the TSP network for the subtour elimination algorithm. jGraphT version 0.9.0 is used in jORLib. 
- [Guava](https://code.google.com/p/guava-libraries/) The Guava project contains several of Googles core libraries for Java-based projects: collections, caching, primitives support, concurrency libraries, common annotations, string processing, I/O, and so forth. Guava version 14.0.1 is needed for the Branch-and-Price framework.
- [Logback](http://logback.qos.ch/) Provides logging fascilities. logback-classic-0.9.28.jar and logback-core-0.9.28.jar are required for the Branch-and-Price framework.
- [Slf4j](http://www.slf4j.org/) The Simple Logging Facade for Java (SLF4J) serves as a simple facade or abstraction for various logging frameworks (e.g. java.util.logging, logback, log4j) allowing the end user to plug in the desired logging framework at deployment time. slf4j-api-1.6.1.jar is required for the Branch-and-Price framework.

## Online Resources ##

Source code is hosted on [github](https://github.com/jkinable/jorlib). You can send contributions as pull requests there.

## Your Improvements ##
Your contributions are highly welcome. Literally anything related to OR will be considered:
-Heuristic/exact algorithms related to frequently reoccuring problems, preferably backed by a scientific paper or book
-Visualization tools
-Readers/parsers for common IO such as TSPLib instances
-etc
If you add improvements to jORLib please send them to us as pull requests on github. We will add them to the next release so that everyone can enjoy them. You might also benefit from it: others may fix bugs in your source files or may continue to enhance them.

## Using jORLib ##
If you are using jORLib in your work, you are cordially invited to drop a note on our mailing lists (), describing your work and what you used jORLib for. This will give us a better understanding of how the Library is used and what people\'s interests are. Feel free to report any issues, feature requests, etc on our issue tracker on Github (https://github.com/jkinable/jorlib/issues).

## Thanks ##

With regards from

[Joris Kinable](mailto:jkinable@cs.cmu.edu), jORLib Project Creator

