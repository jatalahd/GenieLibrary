GenieLibrary
============

Exploratory Adobe Genie keyword Java library for Robot Framework, built upon the AnnotationLibrary provided by the Robot's javalib-core distribution. Please note that this project is currently in a preliminary version status and needs many improvements! The GenieLibrary.html provides the current keyword documentation for the library.

This is a basic Maven project. To create a standalone jar package with dependencies, just type: "mvn clean package" in the directory where the pom.xml is located.

NOTE! The genie-executor version 2.0.0 maven dependency in the pom.xml is satisfied by making a local maven installation of the Executor.jar file found from the Adobe Genie installation directory. Once the Executor.jar has been obtained, the local maven installation is done with the command:

mvn install:install-file -Dfile=Executor.jar -DgroupId=com.adobe.genie -DartifactId=genie-executor -Dversion=2.0.0 -Dpackaging=jar

After this installation the build can be run successfully. The resulting -jar-with-dependencies.jar will contain both the Robot keyword library and the contentst of the Executor.jar

To make things roll with Robot Framework, one can use jybot or the .jar distribution of Robot Framework. When using the .jar distribution, one should set the CLASSPATH as:
set CLASSPATH=robotframework-2.x.x.jar;GenieLibrary-1.0-SNAPSHOT-jar-with-dependencies.jar;

and then run the test case file with the command:
java org.robotframework.RobotFramework run test.txt

Installation instructions and much more useful information can be found from the Adobe Genie UserGuide.pdf, which can be found from the project's official site: http://sourceforge.net/adobe/genie/wiki/Home/ .

In my opinion Genie is a first-class test tool for ActionScript3/Flash applications. Simply awesome.


