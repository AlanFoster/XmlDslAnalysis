package foo.language.impl.body

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{CommonTestClasses, TestBase, JavaJDK1_7TestBase}
import foo.language.impl.TestDataInterpolator
import scala.Some
import com.intellij.codeInsight.completion.CompletionType
import scala.collection.JavaConverters._
import org.unitils.reflectionassert.ReflectionAssert._

/**
 * Tests to ensure that method contribution works as expected within the camel simple language
 */
class MethodContributionTests
  extends LightCodeInsightFixtureTestCase
  with JavaJDK1_7TestBase
  with TestBase
  with CommonTestClasses
  with TestDataInterpolator {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/language/contribution/body/methodContribution")

  /**
   * Definitions for test
   * @param fileName Test file name
   * @param expectedMethods The expected names of variants provided
   */
  case class TestScenario(fileName: Option[String], expectedMethods: List[String])

  val BodyIsJavaLangObject =
    TestScenario(
      Some("BodyIsJavaLangObject.xml"),
      List("class", "equals", "getClass", "hashCode", "notify", "notifyAll", "toString", "wait"))

  val MultiplePipeline =
    TestScenario(
      Some("MultiplePipeline.xml"),
      List("age", "class", "equals", "f", "firstName", "getAge", "getClass", "getF", "getFirstName", "getId",
        "getLastName", "getNumber", "hashCode", "id", "lastName", "notify", "notifyAll", "number", "setAge", "setF",
        "setFirstName", "setId", "setLastName", "setNumber", "toString", "wait"))

  /**
   * Contribution should access super methods
   */
  val ComplexModel = TestScenario(
    Some("ComplexModel.xml"),
    List("additionalInformation", "class", "equals", "getAdditionalInformation", "getClass", "getId", "getSelf",
      "hashCode", "id", "notify", "notifyAll", "self", "setAdditionalInformation", "setId", "toString", "voidReturn", "wait")
  )

  val ChoicePipeline = TestScenario(
    Some("ChoicePipeline.xml"),
     List("additionalInformation", "age", "class", "class", "class", "class", "customerId", "equals", "equals",
       "equals", "equals", "f", "firstName", "getAdditionalInformation", "getAge", "getClass", "getClass", "getClass",
       "getClass", "getCustomerId", "getF", "getFirstName", "getId", "getId", "getLastName", "getNumber", "getOrderId",
       "getOrderType", "getSelf", "hashCode", "hashCode", "hashCode", "hashCode", "id", "id", "lastName", "notify",
       "notify", "notify", "notify", "notifyAll", "notifyAll", "notifyAll", "notifyAll", "number", "orderId", "orderType",
       "self", "setAdditionalInformation", "setAge", "setCustomerId", "setF", "setFirstName", "setId", "setId",
       "setLastName", "setNumber", "setOrderId", "setOrderType", "toString", "toString", "toString", "toString",
       "voidReturn", "wait", "wait", "wait", "wait"
     )
  )

  def testDotAccessAfterBody_BodyIsJavaLangObject() {
    doTest(BodyIsJavaLangObject)
  }

  def testDotAccessAfterBody_MultiplePipeline() {
    doTest(MultiplePipeline)
  }

  def testDotAccessAfterBody_ComplexModel() {
    doTest(ComplexModel)
  }

  def testDotAccessAfterBody_ChoicePipeline() {
    doTest(ChoicePipeline)
  }

/*  def testElvisAccessAfterBody() {
    doTest(BodyIsJavaLangObject)
  }

  def testArrayAccessAfterBody() {
    doTest(BodyIsJavaLangObject)
  }*/

  def doTest(testScenario: TestScenario) {
    loadAllCommon(myFixture)
    // Load the file
    val testName = getTestName(false).takeWhile(_ != '_')
    val testData = getInterpolatedTestData(testName, testScenario.fileName, getTestDataPath)
    myFixture.configureByText(testScenario.fileName.getOrElse("camelTest.Camel"), testData)

    // Invoke code completion and validate our tests
    myFixture.complete(CompletionType.BASIC)
    val suggestedStrings = myFixture.getLookupElementStrings

    assertReflectionEquals(testScenario.expectedMethods.asJava, suggestedStrings)
  }

}
