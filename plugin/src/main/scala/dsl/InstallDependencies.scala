import java.io.File
import sys.process._
import java.nio.file.{Paths, Path, Files}
import java.net.{URLEncoder, URI}
import java.util
import collection.JavaConverters._

object CommandLineProcessLogger {
  def apply() = new ProcessLogger {
    def out(s: => String): Unit = println(s)
    def err(s: => String): Unit = System.err.println(s)
    def buffer[T](f: => T): T = f
  }
}

object InstallDependencies {
  def main(args: Array[String]) {

    val configuration = getConfiguration(args)

    println(s"Attempting install with the following information\n\t-version ${configuration.intellijVersion} -intellijPath ${configuration.intellijPath}");

    def installJar(path: Path, groupId: String, artifactId: String, version: String) {
      // Output SBT information
      println(s""""${groupId}" % "${artifactId}" % "${version}",""")

      println(s"Installing .. groupId: ${groupId}, artifactId: ${artifactId}, version: ${version}, path : ${path}")
      val os = System.getProperty("os.name")
      val mvnCommand = s"""mvn install:install-file -Dfile="${path}" -DgroupId=${groupId} -DartifactId=$artifactId -Dversion=${version} -Dpackaging=jar"""

      val osCommand =
        if(os == "Linux") mvnCommand
        else if(os.contains("Windows")) s"""cmd /c ${mvnCommand}"""
        else ???

      // Invoke the maven command process
      osCommand ! CommandLineProcessLogger()
    }

    configuration match {
      case Configuration(version, path) => {
        // Get the Lib Directory PATH for the new Java NIO library
        val libPath = {
          val libDirectory = new File(path, "lib").toURI
          Paths.get(libDirectory)
        }
        val directoryFiles: util.Iterator[Path] = Files.newDirectoryStream(libPath).iterator()
        val jarFiles = directoryFiles.asScala.filter(_.toString.endsWith(".jar")).toList

        val groupId = "com.intellij"

        // Install all jar files in parallel
        jarFiles.par.foreach(jarFile => {
          // Use the file name, without the extension, as the artifactId
          val artifactId = jarFile.getFileName.toString.replace(".jar", "")
          installJar(jarFile, groupId, artifactId, version)
        })
      }
      // Note, this should never occur
      case _ => println("Unexpected configuration")
    }
  }

  def getConfiguration(args: Array[String]) = {
    Configuration(args(0), args(1))

    //Configuration("133.139", """C:\Program Files (x86)\JetBrains\IntelliJ IDEA 13.0""")
  }
}

case class Configuration(intellijVersion: String, intellijPath: String)

object ArgsHandler {
  val help =
    """
      | ${scriptName}
      | ------------------------------------
      | Expected Inputs:
      |   -version ${intellijVersion}
      |   -path ${intellijPath}
      | Example usage: "133.139" "C:\Program Files (x86)\JetBrains\IntelliJ IDEA 13.0"
      | Notes:
      | This installer places all dependencies under their respective version number.
      | No attempt is made to 'share' common resources, simply due to the lack of meta-data within
      | jar dependencies.
      | ------------------------------------
    """.stripMargin

  def dieWithReason(reason: String) {
    println(reason)
    System.exit(-1)
  }

  def parse(args: List[String]): (Option[Configuration], Option[String]) = args match {
    case version :: path :: Nil => (Some(Configuration(version, path)), None)
    case Nil => (None, Some("Path and Version not supplied."))
    case version :: _ => (None, Some("Path or version not supplied."))
    case _ => (None, Some("Invalid Arguments. Please supply only Path and version"))
  }
}

trait InputOption
case object DoubleOption extends InputOption
case object FileOption extends InputOption

object ArgumentParser {
  def apply[T](name: String)= new ArgumentParserBuilder[T](name)
}

case class ArgumentParserBuilder[T](name: String) {
  def input[U](argNames: String*): ArgumentParserBuilder[T] = ??? // InputBuilder

}

object Input {
  def apply[C, T](argNames: String*): InputBuilder[C, T] = new InputBuilder[C, T](argNames.toList)
}

// Builders
case class InputBuilder[C, T](
                               argNames: List[String],
                               debugText: String = "",
                               isRequired: Boolean = false,
                               configurer: (C, T) => C = (c: C, _: T) => c) {

  private def updateIsRequired(newIsRequired: Boolean) = copy(isRequired = newIsRequired)

  def required() = updateIsRequired(newIsRequired = true)
  def optional() = updateIsRequired(newIsRequired = false)

  def withText(text: String) = copy(debugText = text)

  def configureWith(f: (C, T) => C) = copy(configurer = f)
}