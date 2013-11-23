package impl

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import com.intellij.codeInsight.completion.CompletionType
import scala.collection.JavaConverters._
import org.unitils.reflectionassert.ReflectionAssert._
import org.unitils.reflectionassert.ReflectionComparatorMode._
import foo.language.Core.LanguageConstants

/**
 * Tests for variable completion within the apache camel language
 */
class VariableCompletionTest
 extends LightCodeInsightFixtureTestCase
 with TestBase {

  def testVariableContribution() {
    doTest(List(
      "body",
      "in",
      "in.body",
      "headers",
      "in.headers"
    ))
  }

  /**
   * Performs code completion on the current test file's name
   * @param expectedStrings The expected list of contributed strings
   */
  def doTest(expectedStrings: List[String]) {
    val name = getTestName(false)
    myFixture.configureByFile(s"${name}.${LanguageConstants.extension}")
    myFixture.complete(CompletionType.BASIC, 1)
    val suggestedStrings = myFixture.getLookupElementStrings

    assertReflectionEquals(expectedStrings.asJava, suggestedStrings, LENIENT_ORDER)
  }


  override def getTestDataPath: String = testDataMapper("/contribution")

}
