package foo.language.impl.body

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{CommonTestClasses, TestBase, JavaJDK1_7TestBase}
import foo.language.impl.TestDataInterpolator
import scala.Some
import foo.RichTestFixture._
import junit.framework.Assert._
import com.intellij.psi.PsiMethod

/**
 * Tests to ensure that methods are resolved as expected
 */
class MethodResolveTests
  extends LightCodeInsightFixtureTestCase
  with JavaJDK1_7TestBase
  with TestBase
  with TestDataInterpolator
  with CommonTestClasses {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/language/contribution/body/methodResolve")

  /**
   * Definitions for test
   * @param fileName Test file name
   * @param expectedMethodName The expected name of the resolved PsiMethod
   */
  case class TestScenario(fileName: Option[String], expectedMethodName: String = null) {
    /**
     * Creates a new instance of the test scenario with the given expected method name
     * @param expectedMethodName The expected method name
     */
    def withExpectedMethodName(expectedMethodName: String) =
      copy(expectedMethodName = expectedMethodName)
  }

  val BodyIsJavaLangObject = TestScenario(Some("BodyIsJavaLangObject.xml"))
  val BodyIsComplexModel = TestScenario(Some("ComplexModel.xml"))

  /**
   * Ensures that a non-getter method can be contributed
   */
  def testBodyAccessHashCode() {
    doTest(BodyIsJavaLangObject.withExpectedMethodName("hashCode"))
  }

  def testBodyAccessInvalidOperation() {
    doTest(BodyIsJavaLangObject.withExpectedMethodName(null))
  }

  def testBodyAccessInvalidOperationBody() {
    doTest(BodyIsJavaLangObject.withExpectedMethodName(null))
  }

  /**
   * Ensure no NPEs etc happen when there is a very long method call chain
   */
  def testExtremelyLongMethodCallChain() {
    doTest(BodyIsComplexModel.withExpectedMethodName("getSelf"))
  }

  // TODO Implement when the type information can be unioned successfully
  // TODO Implement a test for numbers in the method name
  /*def testBodyAccessGetter() {
    doTest
  }*/

  /**
   * Performs the actual test with the given test scenario
   * @param testScenario
   */
  def doTest(testScenario: TestScenario){
    loadAllCommon(myFixture)

    // Load the file
    val testName = getTestName(false).takeWhile(_ != '_')
    val testData = getTestData(testName, testScenario.fileName, getTestDataPath)
    myFixture.configureByText(testScenario.fileName.getOrElse("camelTest.Camel"), testData)

    // Ensure that our Psi method is referenced as expected
    val referenceOption = myFixture.getElementAtCaretSafe
    referenceOption match {
      case None => assertNull("No element was successfully resolved - Expected <" + testScenario.expectedMethodName + ">", testScenario.expectedMethodName)
      case Some(reference) =>
        // If a reference has been provided, we must fail it.
        if(testScenario.expectedMethodName == null) {
          fail("No reference element should be provided, instead got " + reference)
        }

        assertTrue("The reference should be a PsiMethod - instead got " + reference, reference.isInstanceOf[PsiMethod])
        assertEquals(
          "The Psi method should successfully match the expected method name",
          reference.asInstanceOf[PsiMethod].getName, testScenario.expectedMethodName
        )
    }
  }
}
