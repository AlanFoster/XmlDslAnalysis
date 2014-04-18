package foo.eip.eipCreator

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{JavaJDK1_7TestBase, CommonTestClasses, TestBase}
import foo.eip.EipDagAssert
import foo.tooling.serializers.BodyTypeEipDagSerializer

/**
 * Tests to ensure that the type information of simple expressions are propagated
 * as expected within the type system
 */
class SimpleBodyTypePropagationTests
  extends LightCodeInsightFixtureTestCase
  with TestBase
  with JavaJDK1_7TestBase
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
   * Ensure that when the result type is invalid, it is as a defualt type
   */
  def testInvalidResultTypeAttribute() {
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
   * Ensure that the set of PsiClass types propagates throughout the system
   */
  def testSimpleBodyPropagationAfterUnionTypes() {
    doTest()
  }

  /**
   * Ensure that the expression results are propagated as expected when
   * accessing a method directly
   */
  def testSimpleStringMethodCall() {
    doTest()
  }

  /**
   * Ensure that the expression results are propagated as expected when
   * accessing a method directly
   */
  def testSimpleIntegerMethodCall() {
    doTest()
  }

  /**
   * Test complex nested recursive type call
   */
  def testComplexRecursiveComplexTypeMethodCall() {
    doTest()
  }

  /**
   * Ensure no NPEs etc happen when there is a very long method call chain
   */
  def testExtremelyLongMethodCallChain() {
    doTest()
  }

  /**
   * Test complex nested recursive type call followed by an integer access
   */
  def testComplexRecursiveComplexTypeThenIntegerMethodCall() {
    doTest()
  }

  /**
   * Ensure that chained accessor notation is allowed
   */
  def testChainedMethodCalls() {
    doTest()
  }

  /**
   * Chained method calls separated by ?.
   */
  def testChainedMethodCallsElvis() {
    doTest()
  }

  /**
   * Ensure that bodyAs works as expected when given a valid FQCN
   */
  def testBodyAsValid() {
    doTest()
  }

  /**
   * Test to ensure that mandatoryBodyAs works as expected when given a valid FQCN
   */
  def testMandatoryBodyAsValid() {
    doTest()
  }

  /**
   * Ensure that bodyAs defaults to java.lang.Object when not a valid FQCN
   */
  def testBodyAsInvalid() {
    doTest()
  }

  /**
   * Tests to see if a nested expression can be resolved as expected
   */
  def testBodyAsNestedExpression() {
    doTest()
  }

  /**
   * Ensure that a number literal is handled as expected
   */
  def testBodyAsNumberLiteral() {
    doTest()
  }

  /**
   * The first argument should only be used, as there will be a separate semantic highlight
   */
  def testBodyAsMultipleArguments() {
    doTest()
  }

  /**
   * Ensure there are no NPEs etc when no arguments are applied
   */
  def testBodyAsZeroArguments() {
    doTest()
  }

  /**
   * Ensure there is no NPE when using a function call that doesn't exist
   */
  def testInvalidFunctionCall() {
    doTest()
  }

  /**
   * Happy path headerAs tests
   */
  def testHeaderAsValid() {
    doTest()
  }

  /**
   * Ensure that one argument can be supplied
   */
  def testHeaderAsOneArgument() {
    doTest()
  }

  /**
   * Ensure that a class refernece which doesn't resolve successfully is defaulted
   */
  def testHeaderAsInvalid() {
    doTest()
  }

  /**
   * Test what happens when the arguments are applied in the wrong order for the headerAs function
   */
  def testHeaderAsWrongOrder() {
    doTest()
  }

  /**
   * Ensure that type information propagates as expected when the body is set to a header
   */
  def testSimpleHeaderAccessValid() {
    doTest()
  }

  /**
   * Ensure that type information propagates as expected when the body is set to a header
   */
  def testSimpleHeaderAccessInvalid() {
    doTest()
  }

  /**
   * The notion of array access should be supported
   */
  def testSimpleArrayAccess() {
    doTest()
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
