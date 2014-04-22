package foo.language.impl.body

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{CommonTestClasses, TestBase, JavaJDK1_7TestBase}
import foo.language.impl.TestDataInterpolator
import com.intellij.psi.{ResolveResult, PsiPolyVariantReference, PsiClass}
import junit.framework.Assert._
import com.intellij.codeInsight.completion.CompletionType
import org.unitils.reflectionassert.ReflectionAssert._
import org.unitils.reflectionassert.ReflectionComparatorMode._
import foo.RichTestFixture.toRichTestFixture
import com.intellij.codeInsight.TargetElementUtilBase
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil
import scala.Some
import collection.JavaConverters._

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
   * @param expectedResolve The expected body type to be inferred
   * @param expectedContributions The expected contributions.
   *                              This should always be null, other than non-body acess.
   *                              IE in the scenario of ${in<caret>.body}
   */
  case class TestScenario(fileName: Option[String],
                          expectedResolve: Option[String],
                          expectedContributions: List[String] = null,
                           expectedMultiResolve: Option[List[String]] = None) {
    /**
     * Creates a new instance of the TestScenario with a configured expectedBodyType
     * @return A new instance of this test scenario
     */
    def withExpectedResolve(expectedResolve: Option[String]) =
      copy(expectedResolve = expectedResolve)

    /**
     * Creates a new instance of the TestScenario with a configured expected contribution list
     * @param expectedContributions The expected contributions within this scenario
     * @return A new instance of this test scenario
     */
    def withExpectedContributions(expectedContributions: List[String]) =
      copy(expectedContributions = expectedContributions)

    /**
     * Creates a new instance of TestScenario with a configured expected multi resolve
     * @param expectedMultiResolve An option of the expected values that this reference
     *                             should multi resolve to
     * @return A new instance of this test scenario
     */
    def withMultiResolve(expectedMultiResolve: Option[List[String]]) =
      copy(expectedMultiResolve = expectedMultiResolve)
  }

  val JavaLangObject = TestScenario(Some("BodyIsJavaLangObject.xml"), Some("java.lang.Object"))
  val PipelineOrderFactory = TestScenario(Some("BodyPipelineOrderFactory.xml"), Some("foo.models.OrderModel"))
  val InvalidPipelineReference = TestScenario(Some("InvalidPipelineReference.xml"), Some("java.lang.Object"))
  val MultiplePipeline = TestScenario(Some("MultiplePipeline.xml"), Some("foo.models.PersonModel"))
  val ChoicePipeline = TestScenario(
    Some("ChoicePipeline.xml"), None, null,
    Some(List("java.lang.Object", "foo.models.ComplexModel", "foo.models.PersonModel", "foo.models.OrderModel"))
  )
  val JavaLangObject_NoResolvedReferences = JavaLangObject.withExpectedResolve(None)

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
   * Tests to ensure it it possible to resolve to multiple things
   */
  def testBodyAccess_ChoicePipeline() { doTest(ChoicePipeline.withExpectedContributions(List())) }
  def testInBodyAccess_ChoicePipeline() { doTest(ChoicePipeline) }
  def testOutBodyAccess_ChoicePipeline() { doTest(ChoicePipeline)  }


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
    val testData = getInterpolatedTestData(testName, testScenario.fileName, getTestDataPath)
    myFixture.configureByText(testScenario.fileName.getOrElse("camelTest.Camel"), testData)

    assertContributions(testScenario.expectedContributions)
    assertResolved(testScenario.expectedResolve)
    assertMultiresolve(testScenario)
  }

  /**
   * Ensure that contributions are as expected.
   * Note contribution asserts only required in Note only tested in non body-only access tests
   * @param expectedContributions
   */
  def assertContributions(expectedContributions: List[String]) {
    myFixture.complete(CompletionType.BASIC)
    val lookupStrings = myFixture.getLookupElementStrings

    assertReflectionEquals(
      lookupStrings,
      Option(expectedContributions).map(_.toArray).getOrElse(null),
      LENIENT_ORDER)
  }

  /**
   * Asserts that the references are resolved as expected
   * @param expectedResolve The expected resolved reference FQCN
   */
  def assertResolved(expectedResolve: Option[String]) {
    val referenceOption = myFixture.getElementAtCaretSafe
    referenceOption match {
      case None =>
        if(expectedResolve.isDefined) {
          fail("The element should have resolved to at least one of the following " + expectedResolve)
        }
      case Some(reference: PsiClass) =>
        if(expectedResolve.isEmpty) {
          fail("The element should not have resolved to any element, instead was :: " + reference)
        }
        // Assert expected references
        assertEquals("The head contribution should be valid as expected",
          reference.getQualifiedName,
          expectedResolve.get)
      case _ => fail("Unexpected scenario")
    }
  }

  /**
   * Performs assertion on a reference multiresolving as expected
   * @param testScenario The expected test scenario to be used
   */
  def assertMultiresolve(testScenario: TestScenario) {
    // Extract the Camel body reference from the document
    val injectedEditor = InjectedLanguageUtil.getEditorForInjectedLanguageNoCommit(myFixture.getEditor, myFixture.getFile)
    val reference = TargetElementUtilBase.findReference(injectedEditor).asInstanceOf[PsiPolyVariantReference]

    // Compute our expected resolved results, by either using the provided list, or construct a new
    // single multi-element resolve which contains our single resolve element within it
    val expectedMultiResolve = (testScenario.expectedResolve, testScenario.expectedMultiResolve) match {
      case (Some(fqcn), None) => Some(List(fqcn))
      case (_, option@Some(expected)) => option
      case _ => None
    }

    // Stop a potential NPE by asserting whether or not the reference should be null and carry on testing
    val resolved =
       if(reference != null) reference.multiResolve(false)
       else {
         assertTrue(testScenario.expectedMultiResolve.isEmpty)
         Array[ResolveResult]()
       }

    // Perform the main test
    assertResolveResults(expectedMultiResolve, resolved)
  }

  /**
   * Asserts the expected resolved results
   * @param expectedMultiResolve The expected resolved results
   * @param resolved The actual resolve results
   */
  def assertResolveResults(expectedMultiResolve: Option[List[String]], resolved: Array[ResolveResult]) {
    (expectedMultiResolve, resolved) match {
      case (None, Array()) =>

      case (Some(expected), Array()) =>
        fail("Element did not resolve successfully, expected " + expected)

      case (Some(expected), multiResolve) =>
        val multiResolveNames = multiResolve.map(_.getElement.asInstanceOf[PsiClass].getQualifiedName)
        assertReflectionEquals(
          "The reference should multi resolve as expected",
          expected.asJava,
          multiResolveNames,
          LENIENT_ORDER)

      case _ =>
        fail(
          s"""
            | Unexpected scenario:
            | Expected: ${expectedMultiResolve}
            | Actual: ${resolved.mkString(", ")}
          """.stripMargin)
    }
  }
}
