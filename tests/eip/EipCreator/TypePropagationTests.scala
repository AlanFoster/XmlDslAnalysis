package eip.eipCreator

import impl.TestBase
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.eip.serializers.TypeEipDagSerializer
import eip.EipDagAssert

/**
 * Core tests for ensuring the validity of the EipCreator interface which converts a Dom tree into
 * a more abstracted EIP Dag
 */
class TypePropagationTests
  extends LightCodeInsightFixtureTestCase
  with TestBase{

  override def getTestDataPath: String = testDataMapper("/eip/eipCreator/types")

  /**
   * Ensure that type information is cascaded to following processors within
   * the EIP Dag
   */
  def ignoretestPipelineTypePropagation() {
    doTest()
  }

  /**
   * Ensure that the type information from a call to setHeader understands the
   * resultType attribute, and propagates information accordingly.
   */
  def ignoretestSingleSetHeader() {
    doTest()
  }

  /**
   * Ensures the test is valid
   */
  def doTest() {
    EipDagAssert.doTest(myFixture, getTestName(false), new TypeEipDagSerializer)
  }
}
