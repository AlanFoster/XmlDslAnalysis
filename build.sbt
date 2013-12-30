name := "StaticAnalysis"

version := "1.0"

scalaVersion := "2.10.0"

libraryDependencies ++= Seq(
  "net.sf.jung" % "jung-samples" % "2.0.1",
  "org.unitils" % "unitils-core" % "3.3" % "test",
  "info.cukes" % "cucumber-java" % "1.1.5" % "test"
)