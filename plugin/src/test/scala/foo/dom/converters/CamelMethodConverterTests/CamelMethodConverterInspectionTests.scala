package foo.dom.converters.CamelMethodConverterTests

import com.intellij.testFramework.fixtures.{LightCodeInsightFixtureTestCase, CodeInsightTestFixture}
import foo.{JavaJDK1_7TestBase, TestBase}
import foo.dom.inspections.BlueprintDomElementInspector

/**
 * Tests to ensure that elements which do not resolve successfully are highlighted as expected
 */
class CamelMethodConverterInspectionTests
  extends LightCodeInsightFixtureTestCase
  with TestBase
  with JavaJDK1_7TestBase {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/dom/converters/CamelMethodConverterContribution/inspection")

  /**
   * Test that methods are resolved as expected
   */
  def testIncorrectMethodName() {
    doTest()
  }

  /**
   * Performs the main test with the convention of method names being associated
   * with the file to configure the tests with
   */
  def doTest() {
    // Configure the fixture
    myFixture.configureByFile(s"${getTestName(false)}.xml")

    // Note, you need to manually enable inspections using your local inspector
    myFixture.enableInspections(classOf[BlueprintDomElementInspector])

    // Trigger highlighting testing, note this automatically asserts
    myFixture.checkHighlighting(true, true, true)
  }
}
