package foo.language.impl.headers

import junit.framework.Assert._
import foo.dom.Model.SetHeaderProcessorDefinition
import com.intellij.util.xml.{DomTarget, DomManager}
import com.intellij.psi.xml.XmlTag
import foo.RichTestFixture.toRichTestFixture
import com.intellij.pom.PomTargetPsiElement


/**
 * Tests to ensure that headers elements resolve to the expected value
 */
class HeaderReferenceTest
  extends HeaderTests {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/language/contribution/headers/reference")

  /**
   * Happy path tests
   */
  def testHeadersA_ComplexResolve() { doTest(ComplexHeaders, Some("a")) }
  def testHeadersArrayB_ComplexResolve() { doTest(ComplexHeaders, Some("b")) }

  /**
   * Tests which should not resolve to anything
   */
  def testHeadersA_NoResolve() { doTest(NoHeaders, None) }
  def testHeadersArrayB_NoResolve() { doTest(NoHeaders, None) }

  /**
   * Tests which are not contained within an XML file
   */
  def testHeadersA_Standalone() { doTest(Standalone, None) }
  def testHeadersArrayB_Standalone() { doTest(Standalone, None) }

  /**
   * Performs the test, using the convention of test name being associated
   * with the relevant test file to use
   */
  def doTest(testContext: TestContext, expectedHeaderName: Option[String]) {
    // Configure the fixture
    val testData = getInterpolatedTestData(getTestName(false).takeWhile(_ != '_'), testContext.testFileName, getTestDataPath)
    myFixture.configureByText(testContext.testFileName.getOrElse("camelTest.Camel"), testData)

    val referenceOption = myFixture.getElementAtCaretSafe

    // Ensure we have resolved successfully, if it is expected
    (expectedHeaderName, referenceOption) match {
      case (None, None) =>
      case (None, Some(_)) =>
        fail("No reference should be resolved for this scenario, instead found :: " + referenceOption)
      case (Some(expected), Some(reference)) =>
        assertTrue("The contributed element should be an Pom Target Psi Element", reference.isInstanceOf[PomTargetPsiElement])
        val domElement = reference.asInstanceOf[PomTargetPsiElement].getTarget.asInstanceOf[DomTarget].getDomElement

        assertTrue("The contributed dom element should be a SetHeaderProcessorDefinition", domElement.isInstanceOf[SetHeaderProcessorDefinition])

        val headerElement = domElement.asInstanceOf[SetHeaderProcessorDefinition]
        assertEquals("The header name should be as expected for this scenario", expected, headerElement.getHeaderName.getStringValue)
      case _ => fail("Unexpected scenario")
    }
  }
}
