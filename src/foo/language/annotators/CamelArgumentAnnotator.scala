package foo.language.annotators

import com.intellij.lang.annotation.{AnnotationHolder, Annotator}
import com.intellij.psi.PsiElement
import com.intellij.patterns.PlatformPatterns._
import foo.language.psi.{CamelFunctionArg, CamelFunctionCall}
import com.intellij.psi.util.PsiTreeUtil
import foo.{CamelFunction, CamelFunctions}
import com.intellij.psi.tree.IElementType
import com.intellij.lang.ASTNode

/**
 * Represents the annotator which ensures that the camel functions are called as expected.
 * IE provides semantic analysis for the camel functions, and highlights errors.
 */
class CamelArgumentAnnotator extends Annotator {
  def annotate(element: PsiElement, holder: AnnotationHolder) {
    // Only accept psiElements which are a CamelFunctionArg
    val isAccepted = psiElement(classOf[CamelFunctionArg]).accepts(element)
    if (!isAccepted) return

    // Extract the function call to perform analysis on
    val camelFunction = PsiTreeUtil.getParentOfType(element, classOf[CamelFunctionCall])
    val functionName = camelFunction.getFunctionName.getText
    // Search for our function definition
    val functionDefinition = CamelFunctions.knownFunctions.find(_.functionName == functionName)

    functionDefinition match {
      case Some(CamelFunction(_, arguments)) => {
        // Calculate our current argument position
        val index = camelFunction.getFunctionArgs.getFunctionArgList.indexOf(element)
        if (arguments.isDefinedAt(index)) {
          val camelArgumentDefinition = arguments(index)
          //  val visitor = new FunctionArgumentValidator(camelArgumentDefinition.requiredElementType)
          // element.accept(visitor)
          // element.getNode
          val isValid = containsElementType(element.getNode, camelArgumentDefinition.requiredElementType)
          if (!isValid) {
            holder.createErrorAnnotation(element, "Expected type: " + camelArgumentDefinition.prettyType)
          }
        } else {
          holder.createErrorAnnotation(element, "Unexpected argument")
        }
      }
    }
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