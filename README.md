# Experimental ProB 2.0 

**IMPORTANT:** The layout of the repository has changed! The Eclipse/Rodin plugin was moved to a separate repository: http://github.com/bendisposto/prob2-plugin. This repository only contains the Kernel of ProB 2.0. 

The last version before the restructuring is tagged as preRestructure. 

The project is intended for internal usage, do not rely on any of the features or interfaces in this project. 

The source code of the current ProB release is located at http://github.com/bendisposto/prob

# Bugs
Please report bugs and feature requests at https://probjira.atlassian.net

# Setting up a development environment

ProB 2.0 works with Java 6 on Mac OS, Windows and most Linux distributions. 

We assume, you have a fresh copy of Eclipse (for RCP development) and an empty workspace at hand. We use Kepler (not the latest SR with Java 8 support) but Juno will probably work as well. Luna and the last Kepler SR (SR2-Java8) do not have support for Groovy 2.3 yet. Furthermore, we assume that you have a recent version of gradle (http://www.gradle.org/) installed on your computer. Gradle 1.1 or newer will be sufficient.

1. Install the Groovy plug-in. We use a development version from http://dist.springsource.org/snapshot/GRECLIPSE/e4.3/ 
2. Clone the prob2 repository to some location (e.g. gitrepo/prob2). 
   We assume that gitrepo is a directory outside your Eclipse workspace. 
3. cd into gitrepo/prob2/de.prob2.kernel, switch to the development branch (git checkout develop) and run `gradle eclipse` 
4. In Eclipse, import  gitrepo/prob2/de.prob2.kernel  

(c) 2012-2014 Jens Bendisposto et.al. , all rights reserved
