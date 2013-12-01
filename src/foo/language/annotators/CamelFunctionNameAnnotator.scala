package foo.language.annotators

import com.intellij.lang.annotation.{AnnotationHolder, Annotator}
import com.intellij.psi.PsiElement
import com.intellij.patterns.PlatformPatterns._
import foo.language.psi.{CamelFunctionCall, CamelCamelFunction, CamelFunctionArg}
import scala.None
import foo.CamelFunctions
import com.intellij.psi.util.PsiTreeUtil

/**
 * Ensures that the given function name exists within the camel language
 */
class CamelFunctionNameAnnotator extends Annotator {
  def annotate(element: PsiElement, holder: AnnotationHolder) {
    // Only accept psiElements which are a CamelFunction
    val isAccepted = psiElement(classOf[CamelFunctionCall]).accepts(element)
    if (!isAccepted) return

    CamelFunctionUtil.matchFunction(element)(
      success = _ => (),
      // Provide error highlighting on the function name, if the function doesn't exist
      () => holder.createErrorAnnotation(element, "Function does not exist")
    )
  }
}
