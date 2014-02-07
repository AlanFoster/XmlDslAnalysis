package foo.language.impl

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import com.intellij.codeInsight.completion.CompletionType
import foo.language.Core.LanguageConstants
import foo.TestBase

/**
 * Tests for variable completion within the apache camel language
 */
class VariableCompletionTest
 extends LightCodeInsightFixtureTestCase
 with TestBase {

  override def getTestDataPath: String = testDataMapper("/foo/language/contribution")

  /**
   * Testing that core camel variables are contributed
   */
  def testVariableContribution() {
    //<editor-fold desc="knownCamelVariables">
    doTest(List(
      "body",
      "bodyAs",
      "camelContext",
      "camelId",
      "exception",
      "exchangeId",
      "header",
      "headerAs",
      "headers",
      "id",
      "in",
      "out",
      "mandatoryBodyAs",
      "routeId",
      "sys",
      "sysenv",
      "threadName"
    ))
    //</editor-fold>
  }

  /**
   * Testing that operators are contributed when between camel functions
   */
  // TODO This is a regression!
  def ignoreTestOperatorContribution() {
    doTest(List(
      ">", ">=",
      "<", "<=",
      "||", "&&",
      "=="
    ))
  }

  /**
   * When the caret is between parenthesis, no basic completion should be
   * possible through contribution.
   */
  def testFunctionContribution() {
    doTest(List())
  }


  /**
   * If there are any other existing elements then reference contribution
   * should not occur.
   */
  def testReferenceContributionAfterDot() {
    doTest(List())
  }

  /**
   * Testing to ensure that the psi pattern matches when contributing on a caret
   * position which is a new identifier psi element
   */
  def testReferenceContributionAfterDotAndNewPsiElement() {
    doTest(List())
  }

  /**
   * Test out intellisense
   */
  def testOutVariableContribution() {
    doTest(List(
      "body",
      "header",
      "headers"
    ))
  }

  def testExceptionContribution() {
    doTest(List(
      "message",
      "stacktrace"
    ))
  }

  /**
   * Ensure the user is allowed to access the 'in' variables successfully
   */
  def testInVariableContribution() {
    doTest(List(
      "body",
      "header",
      "headers"
    ))
  }

  /**
   * Performs code completion on the current test file's name
   * @param expectedStrings The expected list of contributed strings
   */
  def doTest(expectedStrings: List[String]) {
    import scala.collection.JavaConverters._
    import org.unitils.reflectionassert.ReflectionAssert._
    import org.unitils.reflectionassert.ReflectionComparatorMode._

    val name = getTestName(false)
    myFixture.configureByFile(s"${name}.${LanguageConstants.extension}")
    myFixture.complete(CompletionType.BASIC, 1)
    val suggestedStrings = myFixture.getLookupElementStrings

    assertReflectionEquals(expectedStrings.asJava, suggestedStrings, LENIENT_ORDER)
  }

}
