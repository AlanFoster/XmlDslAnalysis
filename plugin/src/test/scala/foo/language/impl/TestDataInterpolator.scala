package foo.language.impl

import scala.io.Source
import java.io.File
import foo.language.Core.LanguageConstants

/**
 * Represents the trait to be used during testing when one wishes to gain access
 * to loadable test data which can contain camel interpolation using the
 * associated ConstantReplacement to associate what camel text to replace.
 */
trait TestDataInterpolator {
  /**
   * The associated constant to be replaced within the interpolated file
   */
  val ConstantReplacement = "LANGUAGE_INJECTION_HERE"

  def getInterpolatedTestData(fileName: String, camelText: String, testDataPath: String): String = {
    val camelFile = getFileContent(testDataPath, fileName)

    val interpolatedText = camelFile.replaceAllLiterally(ConstantReplacement, camelText)
    interpolatedText
  }

  /**
   * Provides the test file for the given test.
   * @param testName The test name used - following the convention of loading a file
   *                 under the testing folder with the given name.
   */
  def getInterpolatedTestData(testName: String, testFileName: Option[String], testDataPath: String): String = {
    // Load our camel file under test
    val camelFile = getFileContent(testDataPath, s"${testName}.${LanguageConstants.extension}")

    // Load the default context for the camel language contribution to occur - if appropriate.
    testFileName match {
      case Some(path) =>
        val defaultContext = getFileContent(testDataPath, "../" + path)
        // Create our new interpolated file with the given content
        val interpolatedText = defaultContext.replaceAllLiterally(ConstantReplacement, camelFile)
        interpolatedText
      case _ => camelFile
    }
  }

  private def getFileContent(testDataPath:String, fileName: String) = {
    Source.fromFile(new File(testDataPath, fileName), "utf-8").getLines().mkString("\n")
  }
}
