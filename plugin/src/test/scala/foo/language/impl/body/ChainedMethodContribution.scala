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
  override def getTestDataPath: String = testDataMapper("/foo/language/contribution/body/ChainedMethodContribution")

  /**
   * Definitions for test
   * @param fileName Test file name
   * @param expectedMethods The expected names of variants provided
   */
  case class TestScenario(fileName: Option[String], expectedMethods: List[String] = Nil) {
    def withMethods(expected: List[String]) =
      copy(expectedMethods = expected)
  }


  /**
   * Contribution should access super methods
   */
  val ComplexModel = TestScenario(Some("ComplexModel.xml"))
    .withMethods(
      List("toUpperCase", "toLowerCase")
    )


  def testDotAccessAfterBody_MultiplePipeline() {
    doTest(ComplexModel)
  }

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
