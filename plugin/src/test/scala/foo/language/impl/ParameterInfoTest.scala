package foo.language.impl

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.language.CamelParameterInfoHandler
import com.intellij.codeInsight.hint.ShowParameterInfoContext
import junit.framework.Assert._
import foo.language.generated.psi.CamelFunctionCall
import ideaCommunity.{MockCreateParameterInfoContext, MockUpdateParameterInfoContext, MockParameterInfoUIContext}
import com.intellij.lang.parameterInfo.ParameterInfoHandler
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import foo.language.Core.CamelFileType
import foo.TestBase

/**
 * Tests to ensure that the ParameterInfo implementation is valid.
 * Currently ParameterInfo can be invoked with ctrl+p on methods by default.
 */
class ParameterInfoTest
  extends LightCodeInsightFixtureTestCase
  with TestBase {

  /**
   * Note this test does not explicitly make use of external data, instead tests are constructed
   * via strings
   * @return
   */
  override def getTestDataPath: String = testDataMapper("/foo/language/parameterInfo")

  /**
   * The type signature of the ParameterInfoHandler implementation
   */
  type Handler = ParameterInfoHandler[CamelFunctionCall, CamelFunctionCall]


  /**
   * Test that the parameters will be shown as expected for an empty list
   */
  def testBodyAs_NoArguments() {
    doTest(
      "${bodyAs(<caret>)}",
      "bodyAs(type: Class)",
      0)
  }

  /**
   * Test argument position valid for bodyAs
   */
  def testBodyAs_FirstArgument() {
    doTest(
      "${bodyAs(java.lang.String<caret>)}",
      "bodyAs(type: Class)",
      0)
  }


  /**
   * Test headerAs valid for no arguments
   */
  def testHeaderAs_NoArguments() {
    doTest(
      "${headerAs(<caret>)}",
      "headerAs(key: String, type: Class)",
      0)
  }

  /**
   * Test headerAs valid for one argument
   */
  def testHeaderAs_FirstArgument() {
    doTest(
      "${headerAs('key'<caret>)}",
      "headerAs(key: String, type: Class)",
      0)
  }


  /**
   * Test headerAs is valid for a second argument, with the given comma
   */
  def testHeaderAs_SecondArgument() {
    doTest(
      "${headerAs('key', java.lang.String<caret>)}",
      "headerAs(key: String, type: Class)",
      1)
  }

  /**
   * Test headerAs parameter information valid for an invalid psi tree
   */
  def testHeaderAs_SecondArgument_InvalidPsiTree() {
    doTest(
      "${headerAs('key', <caret>)}",
      "headerAs(key: String, type: Class)",
      1)
  }

  /**
   * Runs the test, following the test method name convention
   *
   * @param testData The data to load into the open file
   * @param expectedParameters The string version of the expected shown text
   * @param expectedIndex The expected argument index
   */
  def doTest(testData: String,
             expectedParameters: String,
             expectedIndex: Int) {

    // Configure the testing framework with our loaded file
    val testName = getTestName(true)
    val psiFile = myFixture.configureByText(CamelFileType, testData)

    val editor = myFixture.getEditor

    val offset = myFixture.getCaretOffset
    // Create our new context manually to test our implementation

    val handler = new CamelParameterInfoHandler

    // Call our assert requirements for show parameter
    assertUIText(handler, editor, psiFile, expectedParameters)
    assertIndex(handler, psiFile, expectedIndex)
  }

  /**
   * Assert that the UI is created correctly, and that the expected text is shown
   * @param handler The ParameterInfoHandler implementation
   * @param editor The editor
   * @param psiFile The currently opened PsiFile
   * @param expectedParameters The string version of the expected shown text
   */
  def assertUIText(
                    handler: Handler,
                    editor: Editor,
                    psiFile: PsiFile,
                    expectedParameters: String
                    ) {
    val context = new MockCreateParameterInfoContext(editor, psiFile)
    val handlerElem = handler.findElementForParameterInfo(context)
    assertNotNull(handlerElem)

    // Enforce getItemsToShow
    handler.showParameterInfo(handlerElem, context)

    val items = context.getItemsToShow
    assertEquals(1, items.length)

    for(item <- items) {
      val mockUIContext = new MockParameterInfoUIContext[CamelFunctionCall](handlerElem)
      handler.updateUI(item.asInstanceOf[CamelFunctionCall], mockUIContext)
      assertEquals(expectedParameters, mockUIContext.getText)
    }
  }

  /**
   * Assert that the correct index is calculated for the given input file
   * @param handler The ParameterInfoHandler implementation
   * @param psiFile The currently opened PsiFile
   * @param expectedIndex The expected index value of the argument list
   */
  def assertIndex(handler: Handler, psiFile: PsiFile, expectedIndex: Int) {
    val context = new MockUpdateParameterInfoContext(psiFile, myFixture)
    handler.findElementForUpdatingParameterInfo(context)

    val element = handler.findElementForUpdatingParameterInfo(context)
    assertNotNull(element)

    handler.updateParameterInfo(element, context)
    assertEquals(expectedIndex, context.getCurrentParameter)
  }


}
