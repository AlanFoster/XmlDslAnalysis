package foo.language.actions

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.{CommonTestClasses, JavaJDK1_7TestBase}
import com.intellij.refactoring.util.CommonRefactoringUtil.RefactoringErrorHintException
import junit.framework.Assert
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil

/**
 * Tests to ensure that the the refactoring support provided for introducing
 * sub expressions works as expected within DOM routes
 */
class CamelIntroduceExpressionVariableTest
  extends LightCodeInsightFixtureTestCase
  with JavaJDK1_7TestBase
  with CommonTestClasses {

  /**
   * {@inheritdoc}
   */
  override def getTestDataPath: String = testDataMapper("/foo/language/actions/introduceExpressionVariable")

  /**
   * Failure Scenarios
   */

  def testEmptySelection() {
    doTest(Some("No elements currently selected"))
  }

  def testNoSelection() {
    doTest(Some("No elements currently selected"))
  }

  def testDomAdditionallySelected() {

  }

  def testOperatorSelected() {

  }

  def testInvalidSelection() {

  }

  /**
   * Edge Cases
   */

  def testUserCancelsRefactoring() {

  }

  /**
   * Happy Path
   */


  def testRefactorAllCamelExpression() {
    doTest()
  }

  def testRefactorAllCamelExpressionWhenExpression() {
    doTest()
  }

  def testRefactorAllStringLiteral() {
    doTest()
  }

  def testRefactorInPipeline() {
    doTest()
  }

  def testRefactorInWhenExpression() {

  }

  def testRefactorInSetBody() {

  }

  def testRefactorInSetHeader() {

  }

  def testRefactorWithDuplicateHeaderName() {

  }

  def testRefactorRightHandSide() {
    // ${foo} == <selection>10</selection>
  }


  /**
   * Performs the main refactoring support logic for this test
   */
  def doTest(expectedValidationError: Option[String] = None) {
    doTestWithError(expectedValidationError)
  }

  def invokeRefactoring() {
    val testName = getTestName(false)

    val expectedFileName = s"${testName}_expected.xml"

    // Configure both the expected and actual files
    myFixture.configureByFile(expectedFileName)
    val targetXmlFile = myFixture.configureByFile(s"${testName}.xml")

    val caretPosition: Int = myFixture.getEditor.getSelectionModel.getSelectionStart

    // Extract the injected language and editor
    val camelFile = InjectedLanguageUtil.findInjectedPsiNoCommit(targetXmlFile, caretPosition)
    val camelEditor = InjectedLanguageUtil.getInjectedEditorForInjectedFile(myFixture.getEditor, camelFile)

    // Create our method handler as expected for testing
    new CamelIntroduceExpressionVariable()
      .invoke(myFixture.getProject, camelEditor, camelFile, null)

    myFixture.checkResultByFile(expectedFileName)
  }

  /**
   * Performs the refactoring, with an expectation that the user will be told a validation
   * message.
   * @param expectedErrorOption The expected validation error
   */
  def doTestWithError(expectedErrorOption: Option[String]) {
    expectedErrorOption match {
      case None => invokeRefactoring()
      case Some(expectedError) =>
        try {
          invokeRefactoring()
          Assert.fail("Should have received validation error :: " + expectedError)
        } catch {
          case e:RefactoringErrorHintException =>
            Assert.assertEquals("The user error message should have been the same", expectedError, e.getMessage)
          case _:Throwable =>
            Assert.fail("Should have received validation error :: " + expectedError)
        }
    }
  }
}
