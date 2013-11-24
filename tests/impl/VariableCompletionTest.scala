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

  override def getTestDataPath: String = testDataMapper("/contribution")

  /**
   * Testing that core camel variables are contributed
   */
  def testVariableContribution() {
    //<editor-fold desc="knownCamelVariables">
    doTest(List(
      "camelId",
      "camelContext.OGNL",
      "exchangeId",
      "id",
      "body",
      "in.body",
      "body.OGNL",
      "in.body.OGNL",
      "bodyAs",
      "mandatoryBodyAs",
      "out.body",
      "header.foo",
      "header[foo]",
      "headers.foo",
      "headers[foo]",
      "in.header.foo",
      "in.header[foo]",
      "in.headers.foo",
      "in.headers[foo]",
      "header.foo[bar]",
      "in.header.foo[bar]",
      "in.headers.foo[bar]",
      "header.foo.OGNL",
      "in.header.foo.OGNL",
      "in.headers.foo.OGNL",
      "out.header.foo",
      "out.header[foo]",
      "out.headers.foo",
      "out.headers[foo]",
      "headerAs",
      "headers",
      "in.headers",
      "property.foo",
      "property[foo]",
      "property.foo.OGNL",
      "sys.foo",
      "sysenv.foo",
      "exception",
      "exception.OGNL",
      "exception.message",
      "exception.stacktrace",
      "date:command:pattern",
      "bean:bean",
      "properties:locations:key",
      "routeId",
      "threadName",
      "ref:xxx",
      "type:name.field"
    ))
    //</editor-fold>
  }

  /**
   * Testing that operators are contributed when between camel functions
   */
  def testOperatorContribution() {
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

}
