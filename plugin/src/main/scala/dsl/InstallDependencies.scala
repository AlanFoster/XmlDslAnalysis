import java.io.File
import sys.process._
import java.nio.file.{Paths, Path, Files}
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

    configuration match {
      case Configuration(version, path) =>
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
      // Note, this should never occur
      case _ => println("Unexpected configuration")
    }
  }

  def installJar(path: Path, groupId: String, artifactId: String, version: String) {
    // Output SBT information
    println( s""""${groupId}" % "${artifactId}" % "${version}",""")

    println(s"Installing .. groupId: ${groupId}, artifactId: ${artifactId}, version: ${version}, path : ${path}")
    val os = sys.props("os.name")
    val mvnCommand = s"""mvn install:install-file -Dfile="${path}" -DgroupId=${groupId} -DartifactId=$artifactId -Dversion=${version} -Dpackaging=jar"""

    val osCommand =
      if (os == "Linux") mvnCommand
      else if (os.contains("Windows")) s"""cmd /c ${mvnCommand}"""
      else ???

    // Invoke the maven command process
    osCommand ! CommandLineProcessLogger()
  }

  def getConfiguration(args: Array[String]) = {
    if (args == null || args.size != 2) Helper.dieWithReason(Helper.defaultHelp)

    Configuration(args(0), args(1))
  }
}

case class Configuration(intellijVersion: String, intellijPath: String)

object Helper {
  val defaultHelp =
    """
      | Installing Options
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
}