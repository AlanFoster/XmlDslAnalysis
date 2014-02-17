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
   * @param expectedBodyTypes The expected body types to be inferred
   */
  case class TestScenario(fileName: Option[String], expectedBodyTypes: List[String])
  val JavaLangObject = TestScenario(Some("BodyIsJavaLangObject.xml"), List("java.lang.Object"))


  def testBodyAccess_JavaLangObject() {
    doTest(JavaLangObject)
  }

  def doTest(testScenario: TestScenario) {
    val testName = getTestName(false).takeWhile(_ != '_')
    val testData = getTestData(testName, testScenario.fileName, getTestDataPath)
    myFixture.configureByText(testScenario.fileName.getOrElse("camelTest.Camel"), testData)
    println(testData)

    val element = Try(myFixture.getElementAtCaret).toOption
    element match {
      case None =>
        if(testScenario.expectedBodyTypes.nonEmpty) {
          fail("The element should have resolved to at least one of the following " + testScenario.expectedBodyTypes)
        }
      case Some(reference: PsiClass) =>
        assertEquals("The head contribution should be valid as expected", reference.getQualifiedName, testScenario.expectedBodyTypes(0))
    }
  }

}
