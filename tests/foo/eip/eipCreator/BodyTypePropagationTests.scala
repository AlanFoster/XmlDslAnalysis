package foo.eip.eipCreator

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.eip.serializers.BodyTypeEipDagSerializer
import foo.eip.EipDagAssert
import foo.TestBase

/**
 * Type inference tests for ensuring the validity of the EipCreator interface which converts a Dom tree into
 * a more abstracted EIP Dag.
 *
 * These tests specifically target body type inference.
 */
class BodyTypePropagationTests
  extends LightCodeInsightFixtureTestCase
  with TestBase{

  override def getTestDataPath: String = testDataMapper("/foo/eip/eipCreator/types/body")

  /**
   * Ensure that type information is cascaded to following processors within
   * the EIP Dag
   */
  def testPipelineTypePropagation() {
    doTest()
  }

  /**
   * Ensure that the body type information after a setHeader processor definition remains consistent
   */
  def testSingleSetHeader() {
    doTest()
  }

  /**
   * Ensures the test is valid
   */
  def doTest() {
    EipDagAssert.doTest(myFixture, getTestName(false), new BodyTypeEipDagSerializer)
  }
}