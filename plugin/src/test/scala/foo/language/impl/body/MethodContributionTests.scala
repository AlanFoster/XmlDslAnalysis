package foo.language.impl.body

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{TestBase, JavaJDK1_7TestBase}
import foo.language.impl.TestDataInterpolator
import scala.util.Try
import junit.framework.Assert._
import scala.Some
import com.intellij.psi.PsiClass
import com.intellij.codeInsight.completion.CompletionType
import org.unitils.reflectionassert.ReflectionAssert._
import org.unitils.reflectionassert.ReflectionComparatorMode._

/**
 * Tests to ensure that method contribu
 */
class MethodContributionTests
  extends LightCodeInsightFixtureTestCase
  with JavaJDK1_7TestBase
  with TestBase
  with TestDataInterpolator {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/language/contribution/body/methodContribution")

  /**
   * Definitions for test
   * @param fileName Test file name
   * @param expectedHeaders The expected names of variants provided
   */
  case class TestScenario(fileName: Option[String], expectedHeaders: List[String])

  val BodyIsJavaLangObject =
    TestScenario(
      Some("BodyIsJavaLangObject.xml"),
      List("class", "equals", "getClass", "hashCode", "notify", "notifyAll", "toString", "wait", "wait", "wait"))

  def testDotAccessAfterBody() {
    doTest(BodyIsJavaLangObject)
  }

/*  def testElvisAccessAfterBody() {
    doTest(BodyIsJavaLangObject)
  }

  def testArrayAccessAfterBody() {
    doTest(BodyIsJavaLangObject)
  }*/

  def doTest(testScenario: TestScenario) {
    import scala.collection.JavaConverters._
    import org.unitils.reflectionassert.ReflectionAssert._
    import org.unitils.reflectionassert.ReflectionComparatorMode._

    // Load the file
    val testName = getTestName(false).takeWhile(_ != '_')
    val testData = getTestData(testName, testScenario.fileName, getTestDataPath)
    myFixture.configureByText(testScenario.fileName.getOrElse("camelTest.Camel"), testData)

    // Invoke codecompletion and validate our tests
    myFixture.complete(CompletionType.BASIC)
    val suggestedStrings = myFixture.getLookupElementStrings

    assertReflectionEquals(testScenario.expectedHeaders.asJava, suggestedStrings, LENIENT_ORDER)
  }

}
