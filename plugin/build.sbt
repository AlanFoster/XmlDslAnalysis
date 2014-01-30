// Configuration
lazy val intellijPath = SettingKey[String]("intellijPath")

lazy val ideaHome = SettingKey[String]("ideaHome")

lazy val pluginsPath = SettingKey[String]("pluginsPath")

// It is not possible to override this value within SBT command line... due to scoping issues.
// Instead system environment variables are used. If you are using teamcity these can be set per build under
// C:\TeamCity\buildAgent\conf\buildAgent.properties - adding an item `env.intellijPath=...`
intellijPath := System.getenv("intellijPath")

ideaHome := intellijPath.value + "/system/plugins-sandbox/test"

pluginsPath := intellijPath.value + "/system/plugins-sandbox/plugins"

val pluginid = "foo.initial"

name := "StaticAnalysis"

version := "1.0"

scalaVersion := "2.10.0"

// Additionally add a reference to our local m2 repository
// According to SBT this is apparently done by default, but doesn't appear to
resolvers += "Local Maven Repository" at Path.userHome.asFile.toURI.toURL + ".m2/repository"

// Enable JVM forking during SBT tests, as we require a separate running JVM instance
// If this was not used, then `javaOptions in Test` would not trigger otherwise
fork in Test := true

// Enable the required JVM options when running tests. Note tests are forked in their own JVM instance
// Intellij requires specific JVM args when running, which we set them here
javaOptions in Test ++= Seq(
  "-ea",
  "-Dfoo=bar",
  s"-Didea.load.plugins.id=${pluginid}",
  "-Didea.home.path=" + file(ideaHome.value),
  "-Didea.plugins.path=" + file(pluginsPath.value),
  "-Dfile.encoding=UTF-8"
)

// TODO is plugins.path needed?

// Disable Parallel exceution - IntelliJ can not support this
parallelExecution in Test := false

testOptions in Test := Seq(Tests.Filter(s => {
  true //!s.endsWith("RenameTest")
}))

// Java tools is only available from the lib JDK folder, and is not accessible over Maven
// We use the tools lib for debugging related classes for example
fullClasspath in Test += Attributed.blank(file(System.getProperty("java.home")) / "../lib/tools.jar")

libraryDependencies ++= Seq(
  // TODO May not be required
  "com.sun.xml.bind" % "jaxb-xjc" % "2.2.4-1",
  // Core dependencies
  "net.sf.jung" % "jung-samples" % "2.0.1",
  "org.unitils" % "unitils-core" % "3.3" % "test",
  "info.cukes" % "cucumber-java" % "1.1.5" % "test",
  // Testing support dependency
  "com.novocode" % "junit-interface" % "0.8" % "test->default",
  // IntelliJ dependencies - Note these are generated
  "com.intellij" % "alloy" % "133.139" % "compile",
  "com.intellij" % "jps-server" % "133.139" % "compile",
  "com.intellij" % "guava-14.0.1" % "133.139" % "compile",
  "com.intellij" % "annotations" % "133.139" % "compile",
  "com.intellij" % "resolver" % "133.139" % "compile",
  "com.intellij" % "icons" % "133.139" % "compile",
  "com.intellij" % "jsch-0.1.50" % "133.139" % "compile",
  "com.intellij" % "resources" % "133.139" % "compile",
  "com.intellij" % "idea-jsp-openapi" % "133.139" % "compile",
  "com.intellij" % "asm-commons" % "133.139" % "compile",
  "com.intellij" % "resources_en" % "133.139" % "compile",
  "com.intellij" % "jsp-api" % "133.139" % "compile",
  "com.intellij" % "asm-tree-3.0" % "133.139" % "compile",
  "com.intellij" % "idea" % "133.139" % "compile",
  "com.intellij" % "jsr166e" % "133.139" % "compile",
  "com.intellij" % "rhino-js-1_7R4" % "133.139" % "compile",
  "com.intellij" % "idea_rt" % "133.139" % "compile",
  "com.intellij" % "asm" % "133.139" % "compile",
  "com.intellij" % "rngom-20051226-patched" % "133.139" % "compile",
  "com.intellij" % "jsr173_1.0_api" % "133.139" % "compile",
  "com.intellij" % "isorelax" % "133.139" % "compile",
  "com.intellij" % "junit-4.10" % "133.139" % "compile",
  "com.intellij" % "asm4-all" % "133.139" % "compile",
  "com.intellij" % "sanselan-0.98-snapshot" % "133.139" % "compile",
  "com.intellij" % "jasper21_rt" % "133.139" % "compile",
  "com.intellij" % "junit" % "133.139" % "compile",
  "com.intellij" % "serviceMessages" % "133.139" % "compile",
  "com.intellij" % "jasper2_rt" % "133.139" % "compile",
  "com.intellij" % "servlet-api" % "133.139" % "compile",
  "com.intellij" % "automaton" % "133.139" % "compile",
  "com.intellij" % "javac2" % "133.139" % "compile",
  "com.intellij" % "jzlib-1.1.1" % "133.139" % "compile",
  "com.intellij" % "batik" % "133.139" % "compile",
  "com.intellij" % "snappy-java-1.0.5" % "133.139" % "compile",
  "com.intellij" % "log4j" % "133.139" % "compile",
  "com.intellij" % "jaxen-1.1.3" % "133.139" % "compile",
  "com.intellij" % "swingx-core-1.6.2" % "133.139" % "compile",
  "com.intellij" % "markdownj-core-0.4.2-SNAPSHOT" % "133.139" % "compile",
  "com.intellij" % "boot" % "133.139" % "compile",
  "com.intellij" % "trang-core" % "133.139" % "compile",
  "com.intellij" % "microba" % "133.139" % "compile",
  "com.intellij" % "jayatana-1.2.4" % "133.139" % "compile",
  "com.intellij" % "trove4j" % "133.139" % "compile",
  "com.intellij" % "bootstrap" % "133.139" % "compile",
  "com.intellij" % "jcip-annotations" % "133.139" % "compile",
  "com.intellij" % "miglayout-swing" % "133.139" % "compile",
  "com.intellij" % "cglib-2.2.2" % "133.139" % "compile",
  "com.intellij" % "trove4j_src" % "133.139" % "compile",
  "com.intellij" % "nanoxml-2.2.3" % "133.139" % "compile",
  "com.intellij" % "jdkAnnotations" % "133.139" % "compile",
  "com.intellij" % "util" % "133.139" % "compile",
  "com.intellij" % "cli-parser-1.1" % "133.139" % "compile",
  "com.intellij" % "jdom" % "133.139" % "compile",
  "com.intellij" % "nekohtml-1.9.14" % "133.139" % "compile",
  "com.intellij" % "commons-codec-1.8" % "133.139" % "compile",
  "com.intellij" % "jettison-1.3.2" % "133.139" % "compile",
  "com.intellij" % "velocity" % "133.139" % "compile",
  "com.intellij" % "commons-httpclient-3.1-patched" % "133.139" % "compile",
  "com.intellij" % "jgoodies-common-1.2.1" % "133.139" % "compile",
  "com.intellij" % "netty-all-4.1.0.Alpha1" % "133.139" % "compile",
  "com.intellij" % "commons-logging-1.1.1" % "133.139" % "compile",
  "com.intellij" % "jgoodies-forms" % "133.139" % "compile",
  "com.intellij" % "winp-1.17-patched" % "133.139" % "compile",
  "com.intellij" % "commons-net-3.1" % "133.139" % "compile",
  "com.intellij" % "jgoodies-looks-2.4.2" % "133.139" % "compile",
  "com.intellij" % "openapi" % "133.139" % "compile",
  "com.intellij" % "ecj-4.2.1" % "133.139" % "compile",
  "com.intellij" % "xbean" % "133.139" % "compile",
  "com.intellij" % "optimizedFileManager" % "133.139" % "compile",
  "com.intellij" % "xerces" % "133.139" % "compile",
  "com.intellij" % "jh" % "133.139" % "compile",
  "com.intellij" % "oromatcher" % "133.139" % "compile",
  "com.intellij" % "extensions" % "133.139" % "compile",
  "com.intellij" % "jing" % "133.139" % "compile",
  "com.intellij" % "picocontainer" % "133.139" % "compile",
  "com.intellij" % "xml-apis" % "133.139" % "compile",
  "com.intellij" % "protobuf-2.5.0" % "133.139" % "compile",
  "com.intellij" % "jna-utils" % "133.139" % "compile",
  "com.intellij" % "xmlrpc-2.0" % "133.139" % "compile",
  "com.intellij" % "proxy-vole_20120920" % "133.139" % "compile",
  "com.intellij" % "forms_rt" % "133.139" % "compile",
  "com.intellij" % "xpp3-1.1.4-min" % "133.139" % "compile",
  "com.intellij" % "jna" % "133.139" % "compile",
  "com.intellij" % "freemarker" % "133.139" % "compile",
  "com.intellij" % "pty4j-0.3" % "133.139" % "compile",
  "com.intellij" % "jps-launcher" % "133.139" % "compile",
  "com.intellij" % "xstream-1.4.3" % "133.139" % "compile",
  "com.intellij" % "purejavacomm" % "133.139" % "compile",
  "com.intellij" % "fxHelpBrowser" % "133.139" % "compile",
  "com.intellij" % "yjp-controller-api-redist" % "133.139" % "compile",
  "com.intellij" % "groovy-all-2.0.6" % "133.139" % "compile",
  "com.intellij" % "gson-2.2.3" % "133.139" % "compile"
)

