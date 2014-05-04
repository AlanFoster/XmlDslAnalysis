package foo.language.functions

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import foo.language.generated.psi.CamelFunctionCall

/**
 * Concrete documentation handler for the inbuilt apache camel functions
 */
// NOTE - Doesn't work, as the camel inbuilt functions do not have reference contributions
// Therefore this method is not called appropriately for these tokens..
class CamelFunctionDocumentationProvider extends AbstractDocumentationProvider {
  /**
   * @inheritdoc
   */
  override def generateDoc(element: PsiElement, originalElement: PsiElement): String = {
    val camelFunctionCall = Option(PsiTreeUtil.getParentOfType(element, classOf[CamelFunctionCall], false))
    camelFunctionCall match {
      case Some(functionCall) =>
        val functionCallName = functionCall.getFunctionName.toString
        // Find the function name which matches, and extract the documentation, if it exists
        CamelFunctions.knownFunctions
          .find(_.functionName == functionCallName)
          .map(_.documentation)
          .getOrElse(null)
      case _ => null
    }
  }
}
