Developing
===========

The following build steps are used by developers.


Pre-Requisites
---------------

#### Tools
The following developer tools are required

- [IntelliJ 13](http://www.jetbrains.com/idea/) 
- [IntelliJ 13 sources](https://github.com/JetBrains/intellij-community)
- [Java 1.7 SE JDK](http://www.oracle.com/technetwork/java/javase/index.html)
- [Scala 2.10.3](http://www.scala-lang.org/download/)
- [Maven 3.0](http://maven.apache.org/) 
- [SBT](http://www.scala-sbt.org/)
	

#### Plugins
The following IDEA plugins are beneficial to install 

- Grammar-Kit - Provides support for EBNF-esque grammars and jflex support
- Scala - This plugin is currently developed using the Scala language
- PsiViewer - Visualisation tools for viewing a File's PSI (Program Structure Interfaces) elements

Note the SBT plugin is *not* required, as we currently use `sbt gen-idea` to generate IntelliJ's required *.iml files

Building
------------------

This application makes use of both [Maven 3.0](http://maven.apache.org/)  and [SBT](http://www.scala-sbt.org/) to manage both dependencies and interacting with the [CI server](http://en.wikipedia.org/wiki/Continuous_integration).

##### Installing IDEA Dependencies

In order to make use of IDEA jar dependencies they must be accessible within a repository, this could be offered via a publically hosted repository such as Nexus - however Jetbrains do not *currently* provide such a resource. As such the following may be used to install the dependencies to a local `.m2` folder.

The following scala script has been created to make this process smoother 

	   > cd ${project_root}\plugin\src\main\scala\dsl
       > scalac InstallDependencies.scala "133.139" "C:\Program Files (x86)\JetBrains\IntelliJ IDEA 13.0"
	
Expected Inputs:

- version
- path

*Note* - This installer places all dependencies under their respective version number.
No attempt is made to 'share' common resources, simply due to the lack of meta-data within jar dependencies.


##### SBT

The Scala Built tool offers the ability to run tests from the command line using `sbt test`. However, the following system environment variables are required within the build tools -

- intellijPath - Used to denote the root testing location. An example value would be `C:\Users\a\.IntelliJIdea13` 


Installation Steps 
------------------
	


	sbt gen-idea


