package foo.language.annotators

import com.intellij.lang.annotation.{AnnotationHolder, Annotator}
import com.intellij.psi.PsiElement
import com.intellij.patterns.PlatformPatterns._
import foo.language.generated.psi.CamelFunctionArg
import foo.{CamelArgument, CamelFunction}
import com.intellij.psi.tree.IElementType
import com.intellij.lang.ASTNode

/**
 * Represents the annotator which ensures that the camel functions are called as expected.
 * IE provides semantic analysis for the camel functions, and highlights errors.
 */
class CamelArgumentAnnotator extends Annotator {
  /**
   * {@inheritdoc}
   */
  def annotate(element: PsiElement, holder: AnnotationHolder) {
    // Only accept psiElements which are a CamelFunctionArg
    val isAccepted = psiElement(classOf[CamelFunctionArg]).accepts(element)
    if (!isAccepted) return

    val addError = (s: String) => {
      holder.createErrorAnnotation(element, s)
      ()
    }

    CamelFunctionUtil.matchFunction(element)({
      case (camelFunctionDefinition@CamelFunction(_, arguments: List[CamelArgument]), psiCamelFunction) => {
        // Calculate our current argument position
        val index = psiCamelFunction.getFunctionArgs.getFunctionArgList.indexOf(element)

        validateArgumentIndex(index, arguments)(addError)
        validateArgumentType(index, arguments, element)(addError)
      }
    }, () => ())
  }

  /**
   * Ensures that the given argument is within the boundaries of the function
   */
  def validateArgumentIndex(currentIndex: Int, arguments: List[CamelArgument])
                           (addError: String => Unit) {
    if (!arguments.isDefinedAt(currentIndex)) {
      addError("Unexpected argument")
    }
  }

  /**
   * Ensures that the current argument has a valid type matching the original function
   * definition
   */
  def validateArgumentType(currentIndex: Int, arguments: List[CamelArgument], psiArg: PsiElement)
                          (addError: String => Unit) = arguments.lift(currentIndex) match {
    case Some(camelArgumentDefinition : CamelArgument) => {
          val isValid = containsElementType(psiArg.getNode, camelArgumentDefinition.requiredElementType)
          if (!isValid) {
            addError("Expected type: " + camelArgumentDefinition.prettyType)
          }
    }
    case _ =>
  }

  /**
   * Recursive searches the given ASTNode's tree to check if the required IElementType is false
   * @param parent The parent ASTNode
   * @param requiredType The required IElementType
   * @return true if either the parent, or any of its children (including grandchildren), contain the
   *         given IElementType reference, otherwise false.
   */
  def containsElementType(parent: ASTNode, requiredType: IElementType): Boolean = parent match {
    case matched: ASTNode =>
      if (matched.getElementType == requiredType) true
      else {
        val children = matched.getChildren(null)
        for {child <- children} {
          if (containsElementType(child, requiredType)) {
            return true
          }
        }
        false
      }
    case null => false
  }
}