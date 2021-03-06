package foo.eip.eipCreator

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.tooling.serializers.CoreEipDagSerializer
import foo.eip.EipDagAssert
import foo.TestBase

/**
 * Core tests for ensuring the validity of the EipCreator interface which converts a Dom tree into
 * a more abstracted EIP Dag
 */
class CoreEipCreatorTests
  extends LightCodeInsightFixtureTestCase
  with TestBase {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/eip/eipCreator/core")

  /**
   * Performs the expected assertion between the loaded dom file and the created Eip DAG
   */
  def testSimpleRoute() {
    doTest()
  }

  /**
   * Ensures choice statement generation is as expected
   */
  def testSimpleChoiceStatement() {
    doTest()
  }

  /**
   * Ensure beans can be used in a pipline
   */
  def testChainedBean() {
    doTest()
  }

  /**
   * A route with no from method should produce an empty DAG
   */
  def testMissingFrom() {
    doTest()
  }

  /**
   * A camel context with no routes should produce an empty DAG
   */
  def testNoRoutes() {
    doTest()
  }

  /**
   * Ensure no NPEs if there is no camel context defined
   */
  def testNoCamelContext() {
    doTest()
  }

  /**
   * Test to ensure that the choice statement can be pipelined as expected
   */
  def testPipelineChoice() {
    doTest()
  }

  /**
   * Test to ensure that the choice statement can be pipelined as expected
   * with multiple when statements
   */
  def testPipelineChoiceMultipleWhen() {
    doTest()
  }

  /**
   * Test to ensure that an unresolved bean ref is handled successfully
   */
  def testUnresolvedBeanRef() {
    doTest()
  }

  /**
   * Test to ensure that the wiretap processor is mapped correctly
   */
  def testWireTap() {
    doTest()
  }

  /**
   * Ensures the test is valid
   */
  def doTest() {
    EipDagAssert.doTest(myFixture, getTestName(false), new CoreEipDagSerializer)
  }
}
