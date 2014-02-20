package foo.dom.converters.BlueprintBeanConverterTests

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.TestBase
import scala.util.Try
import junit.framework.Assert._
import foo.dom.Model.BlueprintBean

/**
 * Tests to ensure that the convert provides references as expected within
 * the given blueprint file
 */
class BlueprintBeanReferenceTests
  extends LightCodeInsightFixtureTestCase
  with TestBase {

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

    val reference = Try(myFixture.getElementAtCaret).getOrElse(null)

    if(expectedReferenceName == null) {
      assertNull("The contributed reference should be null, instead got :: " + reference, reference)
    } else {
      assertEquals("The reference name should be as expected",
        reference.asInstanceOf[BlueprintBean].getId.getStringValue,
        expectedReferenceName)
    }
  }
}
