package foo.language.annotators

import com.intellij.lang.annotation.{AnnotationHolder, Annotator}
import com.intellij.psi.PsiElement
import com.intellij.patterns.PlatformPatterns._
import foo.language.generated.psi.CamelFunctionArg
import com.intellij.psi.tree.IElementType
import com.intellij.lang.ASTNode
import foo.language.{Resolving, CamelArgument, CamelFunction}
import foo.language.typeChecking.CamelSimpleTypeChecker
import foo.intermediaterepresentation.model.types.TypeEnvironment
import scala.Some

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

    val psiArgument = element.asInstanceOf[CamelFunctionArg]

    val addError = (s: String) => {
      holder.createErrorAnnotation(element, s)
      ()
    }

    CamelFunctionUtil.matchFunction(element)({
      case (camelFunctionDefinition@CamelFunction(_, arguments: List[CamelArgument]), psiCamelFunction) => {
        // Calculate our current argument position
        val argList = psiCamelFunction.getFunctionArgs.getFunctionArgList
        val index = argList.indexOf(psiArgument)

        validateArgumentIndex(index, arguments)(addError)
        validateArgumentType(index, arguments, psiArgument)(addError)
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
  def validateArgumentType(currentIndex: Int, arguments: List[CamelArgument], psiArg: CamelFunctionArg)
                          (addError: String => Unit) = arguments.lift(currentIndex) match {
    case Some(camelArgumentDefinition : CamelArgument) => {
          val isValid = isOfType(psiArg, camelArgumentDefinition.requiredFQCN, camelArgumentDefinition.requiredElementType)
          if (!isValid) {
            addError("Expected type: " + camelArgumentDefinition.prettyType)
          }
    }
    case _ =>
  }

  /**
   * @return True if the given psiElement is inferrable to the required Fqcn, or contains the requiredElementType
   *         as a child, otherwise false
   */
  private def isOfType(psiArg: CamelFunctionArg, requiredFqcn: String, requiredElementType: IElementType): Boolean = {
    // Firstly attempt to perform type checking on the current element through type inference rules
    val typeEnvironment = Resolving.getTypeEnvironment(psiArg).getOrElse(TypeEnvironment())
    val matchInferredType = {
        val inferredTypes = new CamelSimpleTypeChecker().typeCheckCamel(typeEnvironment, psiArg)
        inferredTypes.exists(_.contains(requiredFqcn))
    }

    // Attempt to contains element type
    matchInferredType || containsElementType(psiArg.getNode, requiredElementType)
  }


  /**
   * Recursive searches the given ASTNode's tree to check if the required IElementType is false
   * @param parent The parent ASTNode
   * @param requiredType The required IElementType
   * @return true if either the parent, or any of its children (including grandchildren), contain the
   *         given IElementType reference, otherwise false.
   */
  private def containsElementType(parent: ASTNode, requiredType: IElementType): Boolean = parent match {
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