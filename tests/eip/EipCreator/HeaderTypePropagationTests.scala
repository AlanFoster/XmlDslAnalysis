package eip.eipCreator

import impl.TestBase
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.eip.serializers.BodyTypeEipDagSerializer
import eip.EipDagAssert

/**
 * Type inference tests for ensuring the validity of the EipCreator interface which converts a Dom tree into
 * a more abstracted EIP Dag.
 *
 * These tests specifically target header type inference.
 */
class HeaderTypePropagationTests
  extends LightCodeInsightFixtureTestCase
  with TestBase{

  override def getTestDataPath: String = testDataMapper("/eip/eipCreator/types/header")

  /**
   * Ensures the test is valid
   */
  def doTest() {
    EipDagAssert.doTest(myFixture, getTestName(false), new BodyTypeEipDagSerializer)
  }
}
