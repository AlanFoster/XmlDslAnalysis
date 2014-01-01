name := "StaticAnalysis"

version := "1.0"

scalaVersion := "2.10.0"

//sourceDirectories in Compile ++= Seq(baseDirectory.value / "gen")

// Additionally add a reference to our local m2 repository
// According to SBT this is apparently done by default, but doesn't appear to
resolvers += "Local Maven Repository" at Path.userHome.asFile.toURI.toURL + ".m2/repository"

// Enable JVM forking during SBT tests, as we require a separate running JVM instance
fork in Test := true

// Enable the required JVM options when running tests. Note tests are forked in their own JVM instance

parallelExecution in Test := false

javaOptions in Test ++= Seq(
  "-ea",
  "-Dfoo=bar",
  "-Didea.load.plugins.id=foo.initial",
  "-Didea.home.path=C:\\Users\\alan\\.IntelliJIdea13\\system\\plugins-sandbox\\test",
  "-Didea.plugins.path=C:\\Users\\alan\\.IntelliJIdea13\\system\\plugins-sandbox\\plugins",
//  "-Didea.launcher.port=7532",
 // "\"-Didea.launcher.bin.path=C:\\Program Files (x86)\\JetBrains\\IntelliJ IDEA 13.0\\bin\"",
  "-Dfile.encoding=UTF-8"
)

testOptions in Test := Seq(Tests.Filter(s => {
  //s.endsWith("FooTest")  ||
  true
}))

// TODO Compile didn't work by itself, and don't use hard coded path
fullClasspath in Test += Attributed.blank(file("C:/Program Files/Java/jdk1.7.0_25/lib/tools.jar"))

libraryDependencies ++= Seq(
  // hope some sun.tools are included
  "com.sun.xml.bind" % "jaxb-xjc" % "2.2.4-1",
  // Core dependencies
  "net.sf.jung" % "jung-samples" % "2.0.1",
  "org.unitils" % "unitils-core" % "3.3" % "test",
  "info.cukes" % "cucumber-java" % "1.1.5" % "test",
  // Testing support
  "com.novocode" % "junit-interface" % "0.8" % "test->default",
  // SUN dependencies - These should exist on the user's computer
  //"com.sun" % "tools" % "1.6.0" % "system",
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

