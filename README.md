# ProB 2.0

[![Build Status](https://travis-ci.org/bendisposto/prob2.svg?branch=develop)](https://travis-ci.org/bendisposto/prob2)
[![SonarQube Tech Debt](https://img.shields.io/sonar/http/sonarqube.com/prob2kernel/tech_debt.svg?maxAge=2592000)](https://sonarqube.com/dashboard?id=prob2kernel)

**IMPORTANT:** The layout of the repository has changed! The Eclipse/Rodin plugin was moved to a separate repository: http://github.com/bendisposto/prob2-plugin. This repository only contains the Kernel of ProB 2.0.

The last version before the restructuring is tagged as preRestructure.

The project is intended for internal usage, do not rely on any of the features or interfaces in this project.

The source code of the current ProB release is located at http://github.com/bendisposto/prob

## Documentation

* Tutorial: http://stups.hhu.de/ProB/w/Tutorial13
* Developer Handbook: https://www3.hhu.de/stups/handbook/prob2/current/devel/pdf/prob2_devel.pdf

## Bugs

Please report bugs and feature requests at https://probjira.atlassian.net

## Setting up a development environment

ProB 2.0 works with Java 7 on Mac OS, Windows and most Linux distributions.

We assume, you have a fresh copy of Eclipse and an empty workspace at hand. We use Luna SR2. Furthermore, we assume that you have a recent version of gradle (http://www.gradle.org/) installed on your computer. Gradle 1.1 or newer will be sufficient.

1. In Eclipse: Install the Groovy/Grails Tool Suite from the Eclipse Market Place. You can leave out the Grails IDE, Spring Dashboard and all features starting with Pivotal. For Eclipse Mars, see https://tedvinke.wordpress.com/2015/10/17/eclipse-mars-grails-3-1-with-gradle-groovy-and-gsp-support/ for installation instructions. Use the Groovy 2.4 Compiler feature.
2. Clone the prob2 repository to some location (e.g. gitrepo/prob2).
   We assume that gitrepo is a directory outside your Eclipse workspace.
3. cd into gitrepo/prob2/de.prob2.kernel, switch to the development branch (git checkout develop) and run `gradle eclipse`
4. In Eclipse, import project from gitrepo/prob2/de.prob2.kernel


## License

The ProB 2.0 source code is distributed under the [Eclipse Public License - v 2.0](LICENSE).

ProB 2.0 comes with ABSOLUTELY NO WARRANTY OF ANY KIND ! This software is
distributed in the hope that it will be useful but WITHOUT ANY WARRANTY.
The author(s) do not accept responsibility to anyone for the consequences of
using it or for whether it serves any particular purpose or works at all. No
warranty is made about the software or its performance.


(c) 2012-2017 Jens Bendisposto et.al., all rights reserved
