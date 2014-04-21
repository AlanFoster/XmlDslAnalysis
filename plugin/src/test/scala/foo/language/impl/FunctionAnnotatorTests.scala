package foo.language.impl

import foo.language.Core.LanguageConstants
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{JavaJDK1_7TestBase, TestBase}

/**
 * Tests for ensuring the function annotator works as expected with the Apache Camel
 * function
 */
class FunctionAnnotatorTests
  extends LightCodeInsightFixtureTestCase
  with JavaJDK1_7TestBase
  with TestBase {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/language/annotators/functions")

  /**
   * Tests for string in a place of a class
   */
  def testInvalidArgumentStringButExpectedClass() {
    doTest()
  }

  /**
   * Tests for an integer n a place of a class
   */
  def testInvalidArgumentIntButExpectedClass() {
    doTest()
  }

  /**
   * Test multiple arguments within headerAs
   */
  def testInvalidArgumentClassButExpectedString() {
    doTest()
  }

  /**
   * Test a not yet completed argument
   */
  // TODO Add annotation for incomplete argument
  def ignoreNotCompletedArgument() {
    doTest()
  }

  /**
   * Ensure the function is highlighted if it doesn't exist
   * And the function definition has no defined arguments
   */
  def testIncorrectFuncNameNoArgs(){
    doTest()
  }

  /**
   * Ensure the function is highlighted if it doesn't exist
   * And the function definition has some defined arguments
   */
  def testIncorrectFuncNameWithArgs() {
    doTest()
  }

  /**
   * Ensure the camel function is highlighted when not all
   * arguments are applied as expected
   */
  def testNotEnoughArguments() {
    doTest()
  }

  def ignoretestExtraCommas() {
    doTest()
  }

  def testNestedExpressionIsString() {
    doTest()
  }

  def testNestedExpressionIsInvalid() {
    doTest()
  }

  private def doTest() {
    val testName = getTestName(false)

    // Load the file and the expected errors with the given naming convention
    val loadedFile = s"${testName}.${LanguageConstants.extension}"
    val expectedErrors = s"${testName}_expected.${LanguageConstants.extension}"
    myFixture.configureByFiles(expectedErrors, loadedFile)

    myFixture.checkHighlighting(false, false, true)
  }
}
