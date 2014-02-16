package foo.language.impl.headers

import foo.{JavaJDK1_7TestBase, TestBase}
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import scala.io.Source
import java.io.File
import foo.language.Core.LanguageConstants

/**
 * Represents the base class for tests which are intended to be ran
 * to test the header language within the apache camel simple language
 */
abstract class HeaderTests
  extends LightCodeInsightFixtureTestCase
  with JavaJDK1_7TestBase
  with TestBase {

  /**
   * Test scenario descriptions - which contain the file name and assocaited expected headers
   * given the context
   */
  case class TestContext(testFileName: Option[String], availableHeaders: List[String]) {
    /**
     * Creates a new TestContext in which no headers are expected, IE used in
     * the scenario of no expected contribution
     */
    def emptyContribution = TestContext(testFileName, List())
  }

  // Test data scenarios to be used within tests
  val ComplexHeaders = TestContext(Some("ComplexHeaders.xml"), ('a' to 'l').map(_.toString).toList)
  val EmptyContext = TestContext(Some("EmptyContext.xml"), List("a"))
  val NoHeaders = TestContext(Some("NoHeaders.xml"), List())

  val Standalone = TestContext(None, List())

  /**
   * Provides the test file for the given test.
   * @param testName The test name used - following the convention of loading a file
   *                 under the testing folder with the given name.
   */
  def getTestData(testName: String, testFileName: Option[String]): String = {
    // Loads the given file name from the test directory and returns the associated content
    val getFileContent = (fileName: String) => Source.fromFile(new File(getTestDataPath, fileName), "utf-8").getLines().mkString("\n")

    // Load our camel file under test
    val camelFile = getFileContent(s"${testName}.${LanguageConstants.extension}")

    // Load the default context for the camel language contribution to occur - if appropriate.
    testFileName match {
      case Some(path) => {
        val defaultContext = getFileContent("../" + path)
        // Create our new interpolated file with the given content
        val interpolatedText = defaultContext.replaceAllLiterally("LANGUAGE_INJECTION_HERE", camelFile)
        interpolatedText
      }
      case _ => camelFile
    }
  }
}
