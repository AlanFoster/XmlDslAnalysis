package foo.language.impl.body

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{CommonTestClasses, TestBase, JavaJDK1_7TestBase}
import foo.language.impl.TestDataInterpolator
import scala.Some
import com.intellij.codeInsight.completion.CompletionType
import scala.collection.JavaConverters._
import org.unitils.reflectionassert.ReflectionAssert._

/**
 * Tests to ensure that method access can be chained such as a.b.c?.d
 */
class ChainedMethodContribution
  extends LightCodeInsightFixtureTestCase
  with JavaJDK1_7TestBase
  with TestBase
  with CommonTestClasses
  with TestDataInterpolator {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/language/contribution/body/chainedMethodContribution")

  /**
   * Definitions for test
   * @param fileName Test file name
   * @param expectedMethods The expected names of variants provided
   */
  case class TestScenario(fileName: Option[String], expectedMethods: List[String] = Nil) {
    def withMethods(expected: List[String]) =
      copy(expectedMethods = expected)
  }

  val STRING_SUGGESTIONS =
    List("bytes", "charAt", "class", "codePointAt", "codePointBefore", "codePointCount", "compareTo",
      "compareToIgnoreCase", "concat", "contains", "contentEquals", "copyValueOf", "endsWith", "equals",
      "equalsIgnoreCase", "format", "getBytes", "getClass", "hashCode", "indexOf", "intern", "lastIndexOf",
      "length", "matches", "notify", "notifyAll", "offsetByCodePoints", "regionMatches", "replace",
      "replaceAll", "replaceFirst", "split", "startsWith", "subSequence", "substring", "toCharArray",
      "toLowerCase", "toString", "toUpperCase", "trim", "valueOf", "wait"
    )

  val INTEGER_SUGGESTIONS = List("bitCount", "byteValue", "class", "compareTo", "decode", "doubleValue", "equals",
    "floatValue", "getClass", "getInteger", "hashCode", "highestOneBit", "integer", "intValue", "longValue",
    "lowestOneBit", "notify", "notifyAll", "numberOfLeadingZeros", "numberOfTrailingZeros", "parseInt", "reverse",
    "reverseBytes", "rotateLeft", "rotateRight", "shortValue", "signum", "toBinaryString", "toHexString",
    "toOctalString", "toString", "valueOf", "wait")

  /**
   * Contribution should access super methods
   */
  val ComplexModel = TestScenario(Some("ComplexModel.xml"))
    .withMethods(STRING_SUGGESTIONS)

  def testFirstMethodSuccessDotAccessAfterBody_MultiplePipeline() {
    doTest(ComplexModel)
  }

  def testFirstMethodFailDotAccessAfterBody_MultiplePipeline() {
    doTest(ComplexModel.withMethods(Nil))
  }

  def testMultipleChainedDotAccessAfterBody_MultiplePipeline() {
    doTest(ComplexModel.withMethods(STRING_SUGGESTIONS))
  }

  def testMultipleChainedWithUnresolvedDotAccessAfterBody_MultiplePipeline() {
    doTest(ComplexModel.withMethods(Nil))
  }

  def testPrimitiveIntCallDotAccessAfterBody_MultiplePipeline() {
    doTest(ComplexModel.withMethods(INTEGER_SUGGESTIONS))
  }

  def testVoidReturnTypeDotAccessAfterBody_MultiplePipeline() {
    doTest(ComplexModel.withMethods(Nil))
  }

  /**
   * Performs the given test scenario under the test instance
   * @param testScenario The information about the current test to run
   */
  def doTest(testScenario: TestScenario) {
    loadAllCommon(myFixture)
    // Load the file
    val testName = getTestName(false).takeWhile(_ != '_')
    val testData = getTestData(testName, testScenario.fileName, getTestDataPath)
    myFixture.configureByText(testScenario.fileName.getOrElse("camelTest.Camel"), testData)

    // Invoke code completion and validate our tests
    myFixture.complete(CompletionType.BASIC)
    val suggestedStrings = myFixture.getLookupElementStrings

    assertReflectionEquals(testScenario.expectedMethods.asJava, suggestedStrings)
  }
}
