package foo.language.annotators

import com.intellij.lang.annotation.{AnnotationHolder, Annotator}
import com.intellij.psi.PsiElement
import com.intellij.patterns.PlatformPatterns._
import foo.language.generated.psi.CamelFunctionCall
import foo.language.functions.CamelFunction

/**
 * Ensures that the given function name exists within the camel language, and that
 * the supplied arguments are valid.
 */
class CamelFunctionNameAnnotator extends Annotator {
  /**
   * {@inheritdoc}
   */
  def annotate(element: PsiElement, holder: AnnotationHolder) {
    // Only accept psiElements which are a CamelFunction
    val isAccepted = psiElement(classOf[CamelFunctionCall]).accepts(element)
    if (!isAccepted) return

    val addError = (s: String) => {
      holder.createErrorAnnotation(element, s)
      ()
    }

    val matchingFunction = CamelFunctionUtil.matchFunction(element)
    matchingFunction match {
      case Some((camelFunctionDefinition, psiCamelFunction)) =>
        validateAllArgumentsPresent(camelFunctionDefinition, psiCamelFunction)(addError)
      // Provide error highlighting on the function name, if the function doesn't exist
      case _ => holder.createErrorAnnotation(element, "Function does not exist")
    }
  }

  /**
   * Ensures the function is of the correct argument length
   */
  def validateAllArgumentsPresent(camelFunctionDefinition: CamelFunction,
                                  psiCamelFunction: CamelFunctionCall)
                                 (addError: String => Unit) {
    val expectedArgumentCount = camelFunctionDefinition.arguments.length
    val currentArgumentCount = {
      for {
        functionArgs <- Option(psiCamelFunction.getFunctionArgs)
        size = functionArgs.getFunctionArgList.size()
      } yield size
    } getOrElse 0
    if (expectedArgumentCount != currentArgumentCount) {
      addError("Expected: " + camelFunctionDefinition.prettyPrint())
    }
  }
}
