package foo.language.annotators

import com.intellij.lang.annotation.{AnnotationHolder, Annotator}
import com.intellij.psi.PsiElement
import com.intellij.patterns.PlatformPatterns._
import foo.language.generated.psi.CamelFunctionCall
import foo.CamelFunction

/**
 * Ensures that the given function name exists within the camel language
 */
class CamelFunctionAnnotator extends Annotator {
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

    CamelFunctionUtil.matchFunction(element)(
      success = { case (camelFunctionDefinition, psiCamelFunction) => {
        validateAllArgumentsPresent(camelFunctionDefinition, psiCamelFunction)(addError)
      }},
      // Provide error highlighting on the function name, if the function doesn't exist
      () => holder.createErrorAnnotation(element, "Function does not exist")
    )
  }

  /**
   * Ensures the function is of the correct argument length
   */
  def validateAllArgumentsPresent(camelFunctionDefinition:CamelFunction,
                                  psiCamelFunction: CamelFunctionCall)
                                 (addError: String => Unit) {
    val expectedArgumentCount = camelFunctionDefinition.arguments.length
    val currentArgumentCount = { for {
      functionArgs <- Option(psiCamelFunction.getFunctionArgs)
      size = functionArgs.getFunctionArgList.size()
    } yield size } getOrElse 0
    if(expectedArgumentCount != currentArgumentCount) {
      addError("Expected: " + camelFunctionDefinition.prettyPrint())
    }
  }

}
