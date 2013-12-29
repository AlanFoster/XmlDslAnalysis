package foo.dom.converters

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.JavaJDK1_7TestBase
import com.intellij.codeInsight.completion.CompletionType

/**
 * Tests for ensuring that clear header contributions are completed as expected
 */
class RemoveHeaderResolvingConverter
  extends LightCodeInsightFixtureTestCase
  with JavaJDK1_7TestBase {

  override def getTestDataPath: String = testDataMapper("/foo/dom/converters/RemoveHeaderResolvingConverter")

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

  def doTest(expectedStrings: List[String]) {
    import scala.collection.JavaConverters._
    import org.unitils.reflectionassert.ReflectionAssert._
    import org.unitils.reflectionassert.ReflectionComparatorMode._

    val testData = s"${getTestName(false)}.xml"
    myFixture.configureByFile(testData)
    myFixture.complete(CompletionType.BASIC)
    val suggestedStrings = myFixture.getLookupElementStrings

    assertReflectionEquals(expectedStrings.asJava, suggestedStrings, LENIENT_ORDER)
  }
}
