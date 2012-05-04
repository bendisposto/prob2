# Experimental ProB core

This project contains experiments for the new ProB 2.0 Core API.
It is intended for internal usage, do not rely on any of the features or interfaces in this project. 

The sourcecode of the current ProB release is located at https://github.com/bendisposto/prob

Building:
 `./gradlew deploy`

Running:
 `java -jar build/libs/probcli-standalone-VERSION.jar`
 
 `-s,--shell           start ProB's Groovy shell`
 `-test <script/dir>   run a Groovy test script or all .groovy files from a directory`
 
 
(c) 2011 Jens Bendisposto, all rights reserved
