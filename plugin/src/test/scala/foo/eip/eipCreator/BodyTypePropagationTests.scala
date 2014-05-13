package foo.eip.eipCreator

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.tooling.serializers.BodyTypeEipDagSerializer
import foo.eip.EipDagAssert
import foo.{CommonTestClasses, TestBase}

/**
 * Type inference tests for ensuring the validity of the EipCreator interface which converts a Dom tree into
 * a more abstracted EIP Dag.
 *
 * These tests specifically target body type inference.
 */
class BodyTypePropagationTests
  extends LightCodeInsightFixtureTestCase
  with TestBase
  with CommonTestClasses {

  /**
   * {@inheritdoc}
   */
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
   * Ensure that body information is propagated successfully when a calling a method
   * in which the type information is obvious
   */
  def testValidPipelineMethodCall() {
    doTest()
  }

  /**
   * Test to ensure that an invalid reference id does not result in NPE
   */
  def testInvalidPipelineRef() {
    doTest()
  }

  /**
   * Test to ensure that an invalid method doesn't result in an NPE
   */
  def testInvalidPipelineMethodCall() {
    doTest()
  }

  /**
   * Tests to ensure that pipelines are treated as expected when using custom types
   */
  def testMultipleValidPipeline() {
    doTest()
  }

  /**
   * Ensure type information propagates as expected, and defaults to Object when type
   * can not successfully be inferred
   */
  def testMultipleMixedInvalidPipeline() {
    doTest()
  }

  /**
   * Ensure type information propagates as expected after a choice statement
   */
  def testSingleChoice() {
    doTest()
  }

  /**
   * Ensure information is able to union as expected multiple times
   */
  def testMultipleChoice() {
    doTest()
  }

  def testComplexNestedRoute() {
    doTest()
  }

  /**
   * Constructor methods do not return a return type and shouldn't throw exceptions
   */
  def testConstructorMethodCalled() {
    doTest()
  }

  /**
   * Ensures the test is valid
   */
  def doTest() {
    // Ensure we have added all of the required testing
    loadAllCommon(myFixture)

    EipDagAssert.doTest(myFixture, getTestName(false), new BodyTypeEipDagSerializer)
  }
}
