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
  def testStringToStringResultTypeAttribute() {
    doTest()
  }

  /**
   * Type 'coercion' should be applied
   */
  def testIntegerToStringResultTypeAttribute() {
    doTest()
  }

  /**
   * Type 'coercion' should be applied
   */
  def testStringToBooleanResultTypeAttribute() {
    doTest()
  }

  /**
   * Regardless of LHS and RHS the end result will always be a boolean
   */
  def testSimpleProposition() {
    doTest()
  }

  /**
   * Ensure GT operator works as expected
   */
  def testGreaterThanProposition() {
    doTest()
  }

  /**
   * A call to body by itself should result in the same type information
   */
  def testSimpleBodyPropagation() {
    doTest()
  }

  /**
   * Ensure that the expression results are propagated as expected when
   * accessing a method directly
   */
/*  def testSimpleStringMethodCall() {
    doTest()
  }*/

  /**
   * Ensure that the expression results are propagated as expected when
   * accessing a method directly
   */
/*  def testSimpleIntegerMethodCall() {
    doTest()
  }*/

  /**
   * Test complex nested recursive type call
   */
/*  def testComplexRecursiveComplexTypeMethodCall() {
    doTest()
  }*/

  /**
   * Test complex nested recursive type call followed by an integer access
   */
/*  def testComplexRecursiveComplexTypeThenIntegerMethodCall() {
    doTest()
  }*/

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
