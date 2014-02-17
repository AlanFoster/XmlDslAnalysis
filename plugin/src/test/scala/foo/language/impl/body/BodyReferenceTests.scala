package foo.language.impl.body

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{TestBase, JavaJDK1_7TestBase}
import foo.language.impl.TestDataInterpolator
import scala.util.Try
import com.intellij.psi.PsiClass
import junit.framework.Assert._

/**
 * Tests associated with going from a body reference to the PsiClass it is associated withd
 */
class BodyReferenceTests
  extends LightCodeInsightFixtureTestCase
  with JavaJDK1_7TestBase
  with TestBase
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
   */
  case class TestScenario(fileName: Option[String], expectedReferences: List[String]) {
    /**
     * Creates a new instance of the TestScenario with a configured expectedBodyType
     * @return
     */
    def withExpectedReferences(expectedReferences: List[String]) =
      copy(expectedReferences = expectedReferences)
  }

  val JavaLangObject = TestScenario(Some("BodyIsJavaLangObject.xml"), List("java.lang.Object"))
  val JavaLangObject_NoResolvedReferences = JavaLangObject.withExpectedReferences(List())

  /**
   * Tests which should successfully resolve as expected
   */
  def testBodyAccess_JavaLangObject() { doTest(JavaLangObject) }
  def testInBodyAccess_JavaLangObject() { doTest(JavaLangObject) }
  def testOutBodyAccess_JavaLangObject() { doTest(JavaLangObject) }

  /**
   * References which should *not* resolve - IE caret is in the 'wrong' position
   */
  def testMultipleAccess_NoResolvedReferences() { doTest(JavaLangObject_NoResolvedReferences) }
  def testInAccessOnly_NoResolvedReferences() { doTest(JavaLangObject_NoResolvedReferences)}
  def testOutAccessOnly_NoResolvedReferences() { doTest(JavaLangObject_NoResolvedReferences) }

  /**
   * Performs the main test scenario with the given data
   *
   * @param testScenario The test scenario outline, IE what XML file to load and
   *                     what are the expected references to be provided
   */
  def doTest(testScenario: TestScenario) {
    val testName = getTestName(false).takeWhile(_ != '_')
    val testData = getTestData(testName, testScenario.fileName, getTestDataPath)
    myFixture.configureByText(testScenario.fileName.getOrElse("camelTest.Camel"), testData)

    val element = Try(myFixture.getElementAtCaret).toOption
    element match {
      case None =>
        if(testScenario.expectedReferences.nonEmpty) {
          fail("The element should have resolved to at least one of the following " + testScenario.expectedReferences)
        }
      case Some(reference: PsiClass) =>
        if(testScenario.expectedReferences.isEmpty) {
          fail("The element should not have resolved to any element, instead was :: " + reference)
        }
        assertEquals("The head contribution should be valid as expected", reference.getQualifiedName, testScenario.expectedReferences(0))
    }
  }

}
