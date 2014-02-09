package foo.language.impl

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{TestBase, JavaJDK1_7TestBase}
import foo.language.Core.LanguageConstants
import com.intellij.codeInsight.completion.CompletionType
import java.io.File
import scala.io.Source

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
   * Perform tests to ensure that the patterns work as expected
   * Note - these were automatically generated, hence no specific comments
   */
  def testHeaderArrayAccess() {doTest()}
  def testHeaderAs() {doTest()}
  def testHeaderDotAccess() {doTest()}
  def testHeaderDotHeaderDotAccess() {doTest()}
  def testHeaderElvisAccess() {doTest()}
  def testHeadersArrayAccess() {doTest()}
  def testHeadersDotAccess() {doTest()}
  def testheadersElvisAcccess() {doTest()}
  def testInHeaderDotAccess() {doTest()}
  def testInHeaderElvisAccess() {doTest()}
  def testInHeadersArrayAccess() {doTest()}
  def testInHeadersDotAccess() {doTest()}
  def testInHeadersElvisAccess() {doTest()}
  def testOutHeaderArrayAccess() {doTest()}
  def testOutHeaderDotAccess() {doTest()}
  def testOutHeaderElvisAccess() {doTest()}
  def testOutHeadersArrayAccess() {doTest()}
  def testOutHeadersDotAccess() {doTest()}
  def testOutHeadersElvisAccess() {doTest()}


  /**
   * Performs the test, using the convention of test name being associated
   * with the relevent test file to use
   */
  def doTest() {
    import scala.collection.JavaConverters._
    import org.unitils.reflectionassert.ReflectionAssert._
    import org.unitils.reflectionassert.ReflectionComparatorMode._

    // Loads the given file name from the test directory and returns the associated content
    val getFileContent = (fileName: String) => Source.fromFile(new File(getTestDataPath, fileName), "utf-8").getLines().mkString

    // Load the default context for the camel language contribution to occurr
    val defaultContext = getFileContent("DefaultContext.xml")

    // Load our camel file under test
    val camelFile = getFileContent(s"${getTestName(false)}.${LanguageConstants.extension}")

    // Create our new interpolated file with the given content
    val interpolatedText = defaultContext.replaceAllLiterally("LANGUAGE_INJECTION_HERE", camelFile)
    myFixture.configureByText("context.xml", interpolatedText)

    // Perform the given test
    myFixture.complete(CompletionType.BASIC, 1)
    val suggestedStrings = myFixture.getLookupElementStrings

    val expectedStrings = 'a' to 'l'

    assertReflectionEquals(expectedStrings.asJava, suggestedStrings, LENIENT_ORDER)
  }
}
