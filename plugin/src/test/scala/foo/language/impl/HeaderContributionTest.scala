package foo.language.impl

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{TestBase, JavaJDK1_7TestBase}
import foo.language.Core.LanguageConstants
import com.intellij.codeInsight.completion.CompletionType
import java.io.File
import scala.io.Source
import scala.collection.JavaConverters._
import org.unitils.reflectionassert.ReflectionAssert._
import org.unitils.reflectionassert.ReflectionComparatorMode._

/**
 * Tests to ensure that contribution is performed within the expected areas
 */
class HeaderContributionTest
  extends LightCodeInsightFixtureTestCase
  with JavaJDK1_7TestBase
  with TestBase {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/language/contribution/headers")

  /**
   * Test scenario descriptions - which contain the file name and assocaited expected headers
   * given the context
   */
  case class TestContext(testFileName: String, expectedHeaders: List[String]) {
    /**
     * Creates a new TestContext in which no headers are expected, IE used in
     * the scenario of no expected contribution
     */
    def emptyContribution = TestContext(testFileName, List())
  }
  val ComplexHeaders = TestContext("ComplexHeaders.xml", ('a' to 'l').map(_.toString).toList)
  val EmptyContext = TestContext("EmptyContext.xml", List())

  /**
   * Perform tests to ensure that the patterns work as expected and provide
   * the expected headers within the XML Documentation context
   * Note - these were automatically generated, hence no specific comments.
   * Note - separate methods have been provided for JUnit 3 logging compatability
   */
  def testHeaderArrayAccess_ComplexHeaders() {doTest(ComplexHeaders)}
  def ignoretestHeaderAs_ComplexHeaders() {doTest(ComplexHeaders)}
  def testHeaderDotAccess_ComplexHeaders() {doTest(ComplexHeaders)}
  def testHeaderDotHeaderDotAccess_ComplexHeaders() { doTest(ComplexHeaders.emptyContribution) }
  def testHeaderElvisAccess_ComplexHeaders() {doTest(ComplexHeaders)}
  def testHeadersArrayAccess_ComplexHeaders() {doTest(ComplexHeaders)}
  def testHeadersDotAccess_ComplexHeaders() {doTest(ComplexHeaders)}
  def testheadersElvisAcccess_ComplexHeaders() {doTest(ComplexHeaders)}
  def testInHeaderDotAccess_ComplexHeaders() {doTest(ComplexHeaders)}
  def testInHeaderElvisAccess_ComplexHeaders() {doTest(ComplexHeaders)}
  def testInHeadersArrayAccess_ComplexHeaders() {doTest(ComplexHeaders)}
  def testInHeadersDotAccess_ComplexHeaders() {doTest(ComplexHeaders)}
  def testInHeadersElvisAccess_ComplexHeaders() {doTest(ComplexHeaders)}
  def testOutHeaderArrayAccess_ComplexHeaders() {doTest(ComplexHeaders)}
  def testOutHeaderDotAccess_ComplexHeaders() {doTest(ComplexHeaders)}
  def testOutHeaderElvisAccess_ComplexHeaders() {doTest(ComplexHeaders)}
  def testOutHeadersArrayAccess_ComplexHeaders() {doTest(ComplexHeaders)}
  def testOutHeadersDotAccess_ComplexHeaders() {doTest(ComplexHeaders)}
  def testOutHeadersElvisAccess_ComplexHeaders() {doTest(ComplexHeaders)}


  /**
   * Perform tests to ensure that the patterns work as expected and provide
   * the expected headers within the XML Documentation context
   * Note - these were automatically generated, hence no specific comments
   * Note - separate methods have been provided for JUnit 3 logging compatability
   */
/*  def testHeaderArrayAccess_EmptyContext() {doTest(EmptyContext)}
  def testHeaderAs_EmptyContext() {doTest(EmptyContext)}
  def testHeaderDotAccess_EmptyContext() {doTest(EmptyContext)}
  def testHeaderDotHeaderDotAccess_EmptyContext() {doTest(ComplexHeaders.emptyContribution)}
  def testHeaderElvisAccess_EmptyContext() {doTest(EmptyContext)}
  def testHeadersArrayAccess_EmptyContext() {doTest(EmptyContext)}
  def testHeadersDotAccess_EmptyContext() {doTest(EmptyContext)}
  def testheadersElvisAcccess_EmptyContext() {doTest(EmptyContext)}
  def testInHeaderDotAccess_EmptyContext() {doTest(EmptyContext)}
  def testInHeaderElvisAccess_EmptyContext() {doTest(EmptyContext)}
  def testInHeadersArrayAccess_EmptyContext() {doTest(EmptyContext)}
  def testInHeadersDotAccess_EmptyContext() {doTest(EmptyContext)}
  def testInHeadersElvisAccess_EmptyContext() {doTest(EmptyContext)}
  def testOutHeaderArrayAccess_EmptyContext() {doTest(EmptyContext)}
  def testOutHeaderDotAccess_EmptyContext() {doTest(EmptyContext)}
  def testOutHeaderElvisAccess_EmptyContext() {doTest(EmptyContext)}
  def testOutHeadersArrayAccess_EmptyContext() {doTest(EmptyContext)}
  def testOutHeadersDotAccess_EmptyContext() {doTest(EmptyContext)}
  def testOutHeadersElvisAccess_EmptyContext() {doTest(EmptyContext)}*/

  /**
   * Performs the test, using the convention of test name being associated
   * with the relevent test file to use
   */
  def doTest(testContext: TestContext) {
    // Configure the fixture
    val testData = getTestData(getTestName(false).takeWhile(_ != '_'), testContext.testFileName)
    myFixture.configureByText("camelTest.xml", testData)

    myFixture.complete(CompletionType.BASIC)
    val suggestedStrings = myFixture.getLookupElementStrings

    assertReflectionEquals(testContext.expectedHeaders.asJava, suggestedStrings, LENIENT_ORDER)
  }

  /**
   * Provides the test file for the given test.
   * @param testName The test name used - following the convention of loading a file
   *                 under the testing folder with the given name
   */
  private def getTestData(testName: String, testFileName: String): String = {
    // Loads the given file name from the test directory and returns the associated content
    val getFileContent = (fileName: String) => Source.fromFile(new File(getTestDataPath, fileName), "utf-8").getLines().mkString

    // Load the default context for the camel language contribution to occur
    val defaultContext = getFileContent(testFileName)

    // Load our camel file under test
    val camelFile = getFileContent(s"${testName}.${LanguageConstants.extension}")

    // Create our new interpolated file with the given content
    val interpolatedText = defaultContext.replaceAllLiterally("LANGUAGE_INJECTION_HERE", camelFile)

    interpolatedText
  }
}
