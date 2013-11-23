package impl

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import com.intellij.codeInsight.completion.CompletionType
import scala.collection.JavaConverters._
import org.unitils.reflectionassert.ReflectionAssert._
import org.unitils.reflectionassert.ReflectionComparatorMode._

/**
 * Tests for variable completion within the apache camel language
 */
class VariableCompletionTest
 extends LightCodeInsightFixtureTestCase
 with TestBase {

  def testVariableCompletion() {
    myFixture.configureByFile("contribution.Camel")
    myFixture.complete(CompletionType.BASIC, 1)
    val suggestedStrings = myFixture.getLookupElementStrings
    val expectedStrings = List(
      "body",
      "in",
      "in.body",
      "headers",
      "in.headers"
    ).asJava

    assertReflectionEquals(expectedStrings, suggestedStrings, LENIENT_ORDER)
  }

  override def getTestDataPath: String = testDataMapper("/contribution")

}
