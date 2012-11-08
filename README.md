# Experimental ProB 2.0

This project contains experiments for ProB 2.0.
It is intended for internal usage, do not rely on any of the features or interfaces in this project. 

The source code of the current ProB release is located at http://github.com/bendisposto/prob

# Setting up a development environment

We assume, you have a fresh copy of Eclipse 4.2 and an empty workspace at hand. Furthermore, we assume that you have a recent version of gradle (http://www.gradle.org/) installed on your computer. Gradle 1.1 or newer will be sufficient.

1. Install Groovy-Eclipse for Juno using the Eclipse Marketplace.
2. Clone the prob2 repository to some location (e.g. gitrepo/prob2). 
   We assume that gitrepo is a directory outside your Eclipse workspace. 
3. cd into gitrepo/prob2 and run `gradle clean cleanEclipse eclipse createBuildConstants setClassPath deployKernel`
4. In Eclipse, choose File | Import | General | Existing projects into workspace. Select gitrepo/prob2 as root directory. 
5. Import all four projects.
6. Open the file de.prob.core.rodin/prob_target.target and click "Set as Target Platform" (upper right corner). 
   This will take a while ...
7. Select "Run as Eclipse application" from the context menu of de.prob.ui. This will open a new Eclipse Window and create a fresh run configuration.
8. Close the new Eclipse window and edit the run configuration. Change "Run a product" to org.rodinp.platform.product.
9. Start the configuration again. This time it will start Rodin.
10. Open the Groovy Console view (in the view category "Others") and type 'upgrade latest'. This will download a fresh copy of the Prolog binaries.

  
(c) 2012 Jens Bendisposto, all rights reserved