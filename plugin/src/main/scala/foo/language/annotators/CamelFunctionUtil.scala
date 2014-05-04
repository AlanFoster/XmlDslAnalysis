package foo.language.annotators

import com.intellij.psi.PsiElement
import foo.language.generated.psi.CamelFunctionCall
import com.intellij.psi.util.PsiTreeUtil
import foo.language.functions.{CamelFunctions, CamelFunction}

/**
 * Functional utilities for interacting with Camel Function Psi Elements
 */
object CamelFunctionUtil {
  /**
   * Attempts to find a parent CamelFunctionCall from the given PsiElement, and the associated
   * camel function definition
   * @param element the psi element
   * @param success Called if the function is known within the camel system
   * @param fail Called when a matching function is not found
   * @return
   */
  def matchFunction(element: PsiElement)
                   (success: ((CamelFunction, CamelFunctionCall)) => Unit,
                      fail: () => Unit) {
    // Search for our function definition
    val psiCamelFunctionCall = PsiTreeUtil.getParentOfType(element, classOf[CamelFunctionCall], false)
    val functionName = psiCamelFunctionCall.getFunctionName.getText
    val functionDefinition = CamelFunctions.knownFunctions.find(_.functionName == functionName)

    // Invoke the appropriate call back based on our match
    functionDefinition match {
      case Some(func) => success((func, psiCamelFunctionCall))
      case None => fail()
    }
  }

}
