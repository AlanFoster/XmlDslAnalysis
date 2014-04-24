package foo.eip.eipCreator

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.tooling.serializers.{HeaderTypeEipDagSerializer, BodyTypeEipDagSerializer}
import foo.eip.EipDagAssert
import foo.TestBase

/**
 * Type inference tests for ensuring the validity of the EipCreator interface which converts a Dom tree into
 * a more abstracted EIP Dag.
 *
 * These tests specifically target header type inference.
 */
class HeaderTypePropagationTests
  extends LightCodeInsightFixtureTestCase
  with TestBase{

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/eip/eipCreator/types/header")

  /**
   * Tests header information is union within a pipeline
   */
  def testHeaderPropagationPipeline() {
    doTest()
  }

  /**
   * Ensure header information converges when completing a choice statement
   */
  def testHeaderPropagationChoiceConverging() {
    doTest()
  }

  /**
   * Ensure that malformed attributes are handled as expected. IE This header information
   * will be ignored until it is resolved successfully.
   */
  def testHeaderPropagationMalformedHeaderName(){
    doTest()
  }

  /**
   * Ensure that header information unions within nested choice statements that converge
   */
  def testNestedChoiceConverging() {
    doTest()
  }

  /**
   * Ensure header propagation occurs in more complex nested choice statements
   */
  def testThreeNestedChoice() {
    doTest()
  }

  def testChoiceOtherwise() {
    doTest()
  }

  def testComplexOtherwise() {
    doTest()
  }

  def testSingleChoiceNoElements() {
    doTest()
  }

  /**
   * Ensures the test is valid - using the test name as the configuration file
   */
  def doTest() {
    EipDagAssert.doTest(myFixture, getTestName(false), new HeaderTypeEipDagSerializer)
  }
}
