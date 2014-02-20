package foo.dom.converters.RemoveHeaderResolvingConverter

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.TestBase
import com.intellij.codeInsight.completion.CompletionType
import scala.collection.JavaConverters._
import org.unitils.reflectionassert.ReflectionAssert._
import org.unitils.reflectionassert.ReflectionComparatorMode._

/**
 * Tests for ensuring that clear header contributions are completed as expected
 */
class RemoveHeaderVariants
  extends LightCodeInsightFixtureTestCase
  with TestBase {

  override def getTestDataPath: String = testDataMapper("/foo/dom/converters/RemoveHeaderResolvingConverter/Variants")

  /**
   * Ensure that within a pipeline scenario all headers are suggested to the user
   */
  def testPipelineRemoveHeader() {
    doTest(List("firstHeader", "fourth", "secondHeader", "thirdHeader"))
  }

  /**
   * Ensure that nested choice statements are correctly handled
   */
  def testComplexNestedChoice() {
    // We expect all headers between a to l to be suggested
    doTest(('a' to 'l').map(_.toString).toList)
  }

  /**
   * Ensure that when no headers are found, an empty list is returned
   */
  def testNoHeaders() {
    // We expect all headers between a to l to be suggested
    doTest(List())
  }

  /**
   * Performs the given test by invoking code completion at the given caret position
   * @param expectedStrings The list of expected string sto assert against
   */
  def doTest(expectedStrings: List[String]) {
    val testData = s"${getTestName(false)}.xml"
    myFixture.configureByFile(testData)
    myFixture.complete(CompletionType.BASIC)
    val suggestedStrings = myFixture.getLookupElementStrings

    assertReflectionEquals(expectedStrings.asJava, suggestedStrings, LENIENT_ORDER)
  }
}
