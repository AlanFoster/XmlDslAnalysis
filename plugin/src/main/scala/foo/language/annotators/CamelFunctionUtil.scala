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
   * @return Some(function) otherwise None
   */
  def matchFunction(element: PsiElement): Option[(CamelFunction, CamelFunctionCall)] = {
    // Search for our function definition
    val psiCamelFunctionCall = PsiTreeUtil.getParentOfType(element, classOf[CamelFunctionCall], false)
    val functionName = psiCamelFunctionCall.getFunctionName.getText
    val functionDefinition = CamelFunctions.knownFunctions.find(_.functionName == functionName)

    functionDefinition.map((_, psiCamelFunctionCall))
  }
}
