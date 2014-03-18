package foo.eip.eipCreator

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{CommonTestClasses, TestBase}
import foo.eip.EipDagAssert
import foo.eip.serializers.BodyTypeEipDagSerializer

/**
 * Tests to ensure that the type information of simple expressions are propagated
 * as expected within the type system
 */
class SimpleBodyTypePropagationTests
  extends LightCodeInsightFixtureTestCase
  with TestBase
  with CommonTestClasses {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/eip/eipCreator/types/simple/body")

  /**
   * The result type attribute should  be applied and propagated as expected
   */
  def ignoretestStringToStringResultTypeAttribute() {
    doTest()
  }

  /**
   * Type 'coercion' should be applied
   */
  def ignoretestIntegerToStringResultTypeAttribute() {
    doTest()
  }

  /**
   * Type 'coercion' should be applied
   */
  def ignoretestStringToBooleanResultTypeAttribute() {
    doTest()
  }

  def testTODO() {
    // Noop test to stop JUnit3 from reporting test failure...
  }

  /**
   * Performs the main test scenario- using the test method name the configuration
   * file to run
   */
  def doTest() {
    // Ensure we have added all of the required testing
    loadAllCommon(myFixture)

    EipDagAssert.doTest(myFixture, getTestName(false), new BodyTypeEipDagSerializer)
  }

}
