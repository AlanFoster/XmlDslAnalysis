package impl

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import foo.language.CamelParameterInfoHandler
import com.intellij.codeInsight.hint.ShowParameterInfoContext
import junit.framework.Assert._
import foo.language.psi.CamelFunctionCall
import ideaCommunity.{MockCreateParameterInfoContext, MockUpdateParameterInfoContext, MockParameterInfoUIContext}

/**
 * Tests to ensure that the ParameterInfo implementation is valid.
 * Currently ParameterInfo can be invoked with ctrl+p on methods by default.
 */
class ParameterInfoTest
  extends LightCodeInsightFixtureTestCase
  with TestBase {

  override def getTestDataPath: String = testDataMapper("/parameterInfo")


  def testFunctionElementCorrectlyIdentified() {
    // Configure the testing framework with our loaded file
    val file = "Initial.Camel"
    val psiFile = myFixture.configureByFile(file)

    val editor = myFixture.getEditor

    val offset = myFixture.getCaretOffset
    // Create our new context manually to test our implementation
    val context = new MockCreateParameterInfoContext(editor, psiFile)
    val handler = new CamelParameterInfoHandler

    // Assert that the UI is created correctly, and that the expected text is shown
    def assertUIText(expectedText: String) {
      val handlerElem = handler.findElementForParameterInfo(context)
      assertNotNull(handlerElem)

      // Enforce getItemsToShow
      handler.showParameterInfo(handlerElem, context)

      val items = context.getItemsToShow
      assertEquals(1, items.length)

      for(item <- items) {
        val mockUIContext = new MockParameterInfoUIContext[CamelFunctionCall](handlerElem)
        handler.updateUI(item.asInstanceOf[CamelFunctionCall], mockUIContext)
        assertEquals(expectedText, mockUIContext.getText)
      }
    }

    // Assert that the correct index is calculated for the given input file
    def assertIndex(expectedIndex: Int) {
      val context = new MockUpdateParameterInfoContext(psiFile, myFixture)
      handler.findElementForUpdatingParameterInfo(context)

      val element = handler.findElementForUpdatingParameterInfo(context)
      assertNotNull(element)

      handler.updateParameterInfo(element, context)
      assertEquals(expectedIndex, context.getCurrentParameter)
    }

    // Call our assert requirements for show parameter
    assertUIText("bodyAs(type: Class)")
    assertIndex(0)
  }

}
