package foo.language.annotators

import com.intellij.psi.PsiElement
import foo.language.psi.CamelFunctionCall
import foo.{CamelFunction, CamelFunctions}
import com.intellij.psi.util.PsiTreeUtil

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 01/12/13
 * Time: 01:30
 * To change this template use File | Settings | File Templates.
 */
object CamelFunctionUtil {
  def matchFunction(element: PsiElement)
                   (success: ((CamelFunction, CamelFunctionCall)) => Unit,
                      fail: () => Unit) {
    // Search for our function definition
    val psiCamelFunctionCall = PsiTreeUtil.getParentOfType(element, classOf[CamelFunctionCall], false)
    val functionName = psiCamelFunctionCall.getFunctionName.getText
    val functionDefinition = CamelFunctions.knownFunctions.find(_.functionName == functionName)

    // Provide error highlighting on the function name, if the function doesn't exist
    functionDefinition match {
      case Some(func) => success((func, psiCamelFunctionCall))
      case None => fail()
    }
  }

}
