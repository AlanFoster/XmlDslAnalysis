package foo.dom.converters.BlueprintBeanConverterTests

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{CommonTestClasses, JavaJDK1_7TestBase, TestBase}
import com.intellij.codeInsight.completion.CompletionType
import scala.collection.JavaConverters._
import org.unitils.reflectionassert.ReflectionAssert._
import org.unitils.reflectionassert.ReflectionComparatorMode._


/**
 * Tests to ensure that the blueprint bean contribution is as expected
 * within a given XML blueprint file.
 */
class BlueprintBeanContributionTest
  extends LightCodeInsightFixtureTestCase
  with JavaJDK1_7TestBase
  with TestBase
  with CommonTestClasses {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/dom/converters/BlueprintBeanConverter/contribution")

  /**
   * Base case in which no reference contribution is possible
   */
  def testNoBeans() {
    doTest(List())
  }

  /**
   * Contributing one bean within the same file
   */
  def testOneBean() {
    doTest(List("first"))
  }

  /**
   * Contribution of many beans within the same file
   */
  def testMultipleBeans() {
    doTest(List("first", "second", "third"))
  }

  /**
   * Performs the given test by invoking code completion at the given caret position
   * @param expectedStrings The list of expected strings to assert against
   */
  def doTest(expectedStrings: List[String]) {
    val testData = s"${getTestName(false)}.xml"
    myFixture.configureByFile(testData)
    myFixture.complete(CompletionType.BASIC)
    val suggestedStrings = myFixture.getLookupElementStrings

    assertReflectionEquals(expectedStrings.asJava, suggestedStrings, LENIENT_ORDER)
  }
}
