package foo.dom.converters.BlueprintBeanConverterTests

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{JavaJDK1_7TestBase, TestBase}
import scala.util.{Failure, Success, Try}
import junit.framework.Assert._
import foo.dom.Model.BlueprintBean
import com.intellij.util.xml.DomUtil
import foo.RichTestFixture.toRichTestFixture

/**
 * Tests to ensure that the convert provides references as expected within
 * the given blueprint file
 */
class BlueprintBeanReferenceTests
  extends LightCodeInsightFixtureTestCase
  with TestBase
  with JavaJDK1_7TestBase {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("foo/dom/converters/BlueprintBeanConverter/resolving")


  /**
   * Ensure the null scenario is handled successfully when no beans are available
   */
  def testNoReferenceNoBeans() {
    doTest(null)
  }

  /**
   * Ensure that the scenario of beans existing, without a valid contribution existing
   */
  def testNoReferenceWithBeans() {
    doTest(null)
  }

  /**
   * Ensure that resolving works as expected when multiple beans are
   * available
   */
  def testSingleReference() {
    doTest("resolvedReference")
  }

  /**
   * Performs the main test scenario
   * @param expectedReferenceName
   *                              The expected reference contribution name.
   *                              Note that if this element is null,
   *                              then no reference is expected.
   */
  def doTest(expectedReferenceName: String) {
    val testName = s"${getTestName(false)}.xml"
    myFixture.configureByFile(testName)

    // Attempt to extract the element information if it is valid
    val referenceOption = myFixture.getElementAtCaretSafe

    referenceOption match {
      // Failure if expected name is null, and the reference is not null
      case Some(reference) if expectedReferenceName == null =>
        assertNull("The contributed reference should be null, instead got :: " + referenceOption, reference)

      // Ensure that the element was as expected
      case Some(reference) =>
        val domElement = DomUtil.findDomElement(reference, classOf[BlueprintBean], false)
        assertNotNull(
          "The reference should be of type BlueprintBean, instead got " + reference,
          domElement)

        assertEquals("The reference name should be as expected",
          domElement.getId.getStringValue,
          expectedReferenceName)

      case None => assertNull(expectedReferenceName)
    }
  }
}
