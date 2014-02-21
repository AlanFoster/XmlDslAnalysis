package foo.language.impl.body

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{CommonTestClasses, TestBase, JavaJDK1_7TestBase}
import foo.language.impl.TestDataInterpolator
import com.intellij.psi.PsiClass
import junit.framework.Assert._
import com.intellij.codeInsight.completion.CompletionType
import org.unitils.reflectionassert.ReflectionAssert._
import org.unitils.reflectionassert.ReflectionComparatorMode._
import scala.Some
import foo.RichTestFixture.toRichTestFixture

/**
 * Tests associated with going from a body reference to the PsiClass it is associated withd
 */
class BodyReferenceTests
  extends LightCodeInsightFixtureTestCase
  with JavaJDK1_7TestBase
  with TestBase
  with CommonTestClasses
  with TestDataInterpolator {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/language/contribution/body/bodyReference")

  /**
   * Represents a known configuration to be used during testing
   * @param fileName The name of the file. None implies that the Camel
   *                 file should not be embedded within an Xml context
   * @param expectedReferences The expected body types to be inferred
   * @param expectedContributions The expected contributions.
   *                              This should always be null, other than non-body acess.
   *                              IE in the scenario of ${in<caret>.body}
   */
  case class TestScenario(fileName: Option[String], expectedReferences: List[String], expectedContributions: List[String] = null) {
    /**
     * Creates a new instance of the TestScenario with a configured expectedBodyType
     * @return
     */
    def withExpectedReferences(expectedReferences: List[String]) =
      copy(expectedReferences = expectedReferences)

    /**
     * Creates a new instance of the TestScenario with a configured expected contribution list
     * @param expectedContributions
     */
    def withExpectedContributions(expectedContributions: List[String]) =
      copy(expectedContributions = expectedContributions)
  }

  val JavaLangObject = TestScenario(Some("BodyIsJavaLangObject.xml"), List("java.lang.Object"))
  val PipelineOrderFactory = TestScenario(Some("BodyPipelineOrderFactory.xml"), List("foo.models.OrderModel"))
  val InvalidPipelineReference = TestScenario(Some("InvalidPipelineReference.xml"), List("java.lang.Object"))
  val MultiplePipeline = TestScenario(Some("MultiplePipeline.xml"), List("foo.models.PersonModel"))
  val JavaLangObject_NoResolvedReferences = JavaLangObject.withExpectedReferences(List())

  /**
   * Tests which should successfully resolve as expected.
   * Note these tests should expect a valid contribution to be performed
   */
  def testBodyAccess_JavaLangObject() { doTest(JavaLangObject.withExpectedContributions(List())) }
  def testInBodyAccess_JavaLangObject() { doTest(JavaLangObject) }
  def testOutBodyAccess_JavaLangObject() { doTest(JavaLangObject)  }

  /**
   * Tests to ensure that in a pipeline the body resolves successfully
   */
  def testBodyAccess_PipelineOrderFactory() { doTest(PipelineOrderFactory.withExpectedContributions(List())) }
  def testInBodyAccess_PipelineOrderFactory() { doTest(PipelineOrderFactory) }
  def testOutBodyAccess_PipelineOrderFactory() { doTest(PipelineOrderFactory)  }

  /**
   * Tests to ensure there are no NPEs when there is an invalid reference
   */
  def testBodyAccess_InvalidPipelineReference() { doTest(InvalidPipelineReference.withExpectedContributions(List())) }
  def testInBodyAccess_InvalidPipelineReference() { doTest(InvalidPipelineReference) }
  def testOutBodyAccess_InvalidPipelineReference() { doTest(InvalidPipelineReference)  }

  /**
   * Tests to ensure that the latest pipeline body type is used
   */
  def testBodyAccess_MultiplePipeline() { doTest(MultiplePipeline.withExpectedContributions(List())) }
  def testInBodyAccess_MultiplePipeline() { doTest(MultiplePipeline) }
  def testOutBodyAccess_MultiplePipeline() { doTest(MultiplePipeline)  }

  /**
   * References which should *not* resolve - IE caret is in the 'wrong' position
   */
  def testMultipleAccess_NoResolvedReferences() { doTest(JavaLangObject_NoResolvedReferences.withExpectedContributions(List())) }
  def testInAccessOnly_NoResolvedReferences() { doTest(JavaLangObject_NoResolvedReferences)}
  def testOutAccessOnly_NoResolvedReferences() { doTest(JavaLangObject_NoResolvedReferences.withExpectedContributions(List("out", "routeId"))) }

  /**
   * Performs the main test scenario with the given data
   *
   * @param testScenario The test scenario outline, IE what XML file to load and
   *                     what are the expected references to be provided
   */
  def doTest(testScenario: TestScenario) {
    loadAllCommon(myFixture)

    val testName = getTestName(false).takeWhile(_ != '_')
    val testData = getTestData(testName, testScenario.fileName, getTestDataPath)
    myFixture.configureByText(testScenario.fileName.getOrElse("camelTest.Camel"), testData)

    // Ensure that contributions are as expected - Note only tested in non body-only access tests
    myFixture.complete(CompletionType.BASIC)
    val lookupStrings = myFixture.getLookupElementStrings

    assertReflectionEquals(
      Option(testScenario.expectedContributions).map(_.toArray).getOrElse(null),
      lookupStrings, LENIENT_ORDER)

    // Attempt reference contribution
    val referenceOption = myFixture.getElementAtCaretSafe
    referenceOption match {
      case None =>
        if(testScenario.expectedReferences.nonEmpty) {
          fail("The element should have resolved to at least one of the following " + testScenario.expectedReferences)
        }
      case Some(reference: PsiClass) =>
        if(testScenario.expectedReferences.isEmpty) {
          fail("The element should not have resolved to any element, instead was :: " + reference)
        }
        // Assert expected references
        for(expectedReference <- testScenario.expectedReferences) {
          assertEquals("The head contribution should be valid as expected",
            reference.getQualifiedName,
            expectedReference)
        }
    }
  }

}
