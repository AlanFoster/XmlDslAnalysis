package foo.language.impl.headers

import junit.framework.Assert._
import foo.dom.Model.SetHeaderProcessorDefinition
import com.intellij.util.xml.DomManager
import com.intellij.psi.xml.XmlTag
import scala.util.Try


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
   * with the relevent test file to use
   */
  def doTest(testContext: TestContext, expectedHeaderName: Option[String]) {
    // Configure the fixture
    val testData = getTestData(getTestName(false).takeWhile(_ != '_'), testContext.testFileName)
    myFixture.configureByText(testContext.testFileName.getOrElse("camelTest.Camel"), testData)

    val reference = Try(myFixture.getElementAtCaret).getOrElse(null)

    // Ensure we have resolved successfully, if it is expected
    expectedHeaderName match {
      case None => assertNull("No reference should be resolved for this scenario, instead found :: " + reference, reference)
      case Some(headerName) =>
        assertTrue("The contributed element should be an Xml Element", reference.isInstanceOf[XmlTag])
        val xmlTag = reference.asInstanceOf[XmlTag]
        val domElement = DomManager.getDomManager(getProject).getDomElement(xmlTag)

        assertTrue("The contributed dom element should be a SetHeaderProcessorDefinition", domElement.isInstanceOf[SetHeaderProcessorDefinition])

        val headerElement = domElement.asInstanceOf[SetHeaderProcessorDefinition]
        assertEquals("The header name should be as expected for this scenario", headerName, headerElement.getHeaderName.getStringValue)
    }
  }
}