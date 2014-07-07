# Experimental ProB 2.0 

This project contains experiments for ProB 2.0.
It is intended for internal usage, do not rely on any of the features or interfaces in this project. 

The source code of the current ProB release is located at http://github.com/bendisposto/prob

# Bugs
Please report bugs and feature requests at http://jira.cobra.cs.uni-duesseldorf.de/

# Setting up a development environment

ProB 2.0 works with Java 6 on Mac OS, Windows and some Linux distributions. Because of a bug in Eclipse ProB 2.0 crashes on some Linux systems after using it for about 3 minutes. If you have problems you can use JavaFX as an alternative. To switch to JavaFX add the line 
 `-DenforceJavaFX=true`
after -vmargs in your rodin.ini file. You will need Oracle's Java 7 distribution, OpenJDK 7 will not work. The JavaFX version is not yet tested under Java 8. 


We assume, you have a fresh copy of Eclipse (for RCP development) and an empty workspace at hand. We use Kepler but Juno will probably work as well. Luna does not have support for Groovy 2.3 yet. Furthermore, we assume that you have a recent version of gradle (http://www.gradle.org/) installed on your computer. Gradle 1.1 or newer will be sufficient.

1. Install the Groovy plug-in. We use a development version from http://dist.springsource.org/snapshot/GRECLIPSE/e4.3/ 
2. Clone the prob2 repository to some location (e.g. gitrepo/prob2). 
   We assume that gitrepo is a directory outside your Eclipse workspace. 
3. cd into gitrepo/prob2, switch to the development branch (git checkout develop) and run `gradle magic` 
4. In Eclipse, choose File | Import | General | Existing projects into workspace. Select gitrepo/prob2 as root directory. 
6. Import all projects.
7. Open the file de.prob.core.rodin/rodin.target and click "Set as Target Platform" (upper right corner). 
   This will take a while ...
8. Select "Run as Eclipse application" from the context menu of de.prob.ui. This will open a new Eclipse Window and create a fresh run configuration.
9. Close the new Eclipse window and edit the run configuration. Change "Run a product" to org.rodinp.platform.product.
10. Start the configuration again. This time it will start Rodin.
11. Open the Groovy Console view (in the view category "Others") and type 'upgrade "latest"'. This will download a fresh copy of the Prolog binaries.

IMPORTANT:
-  When adding additional projects which are dependant on de.prob.core.kernel make sure to mark it as optional in their MANIFEST files for the tycho build

-  You can find a tutorial and an example how to use the tycho build script here: https://github.com/birkhoff/tychoBuildScriptTutorial

(c) 2012 Jens Bendisposto et.al. , all rights reserved
