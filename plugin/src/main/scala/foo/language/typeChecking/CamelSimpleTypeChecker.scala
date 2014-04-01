package foo.language.typeChecking

import foo.language.generated.psi._
import foo.language.Core.CamelPsiFile
import com.intellij.psi._
import scala.Some
import foo.traversal.MethodTypeInference
import foo.language.references.EipSimpleReference
import foo.intermediaterepresentation.model.types.TypeEnvironment

/**
 * A concrete implementation of a SimpleTypeCheck which relies on recursively
 * resolving a given Camel expression, based on type judgements/inference rules
 */
class CamelSimpleTypeChecker extends SimpleTypeChecker {
  /**
   * {@inheritdoc}
   */
  override def typeCheckCamel(typeEnvironment: TypeEnvironment, camelPsiFile: CamelPsiFile): Option[Set[String]] = {
    typeCheck(typeEnvironment, camelPsiFile)
  }

  /**
   * Performs type checking on the expression
   * @param typeEnvironment The current type environment associated with this expression
   * @param psiElement The current expression to perform type checking on
   * @return The inferred type information associated with this expression
   */
  private def typeCheck(typeEnvironment: TypeEnvironment, psiElement: PsiElement): Option[Set[String]] = psiElement match {
    /**
     * Perform type checking on the entire tree
     */
    case file: CamelPsiFile =>
      val expressionOption = file.getChildren.lift(0)

      expressionOption match {
        case Some(expression) =>
          typeCheck(typeEnvironment, expression)
        case None =>
          Some(Set(CommonClassNames.JAVA_LANG_OBJECT))
      }

    /**
     * Performs type checking on the given camel CamelExpression element
     * IE this consists of either a literal or a camel expression
     */
    case expression: CamelExpression =>
      val literal = typeCheck(typeEnvironment, expression.getLiteral)

      if (literal.isDefined) literal
      else typeCheck(typeEnvironment, expression.getCamelExpression)

    /**
     * Performs type checking on the contents of a given camel expression.
     * Note this consists of the full text `${...} OP ${...}`
     */
    case expression: CamelCamelExpression =>
      val hasOperator = Option(expression.getOperator).isDefined

      // If the expression has an operator it is a proposition
      // Checking if the expression is a WFF will be checked by other semantic passes
      if (hasOperator) Some(Set(CommonClassNames.JAVA_LANG_BOOLEAN))
      else typeCheck(typeEnvironment, expression.getCamelFunction)

    /**
     * CamelFunction consists of "${" e "}"
     */
    case camelFunction: CamelCamelFunction =>
      typeCheck(typeEnvironment, camelFunction.getCamelFuncBody)

    /**
     * Performs type inference for a camel function body.
     * IE Either a camel function call, or body/header access.
     */
    case camelFuncBody: CamelCamelFuncBody =>
      val variableAccessOption = Option(camelFuncBody.getVariableAccess)

      // Attempt to get the last reference within the body element
      val lastReferenceOption: Option[PsiReference] =
        camelFuncBody.getReferences.toList
          .sortBy(_.getRangeInElement.getEndOffset)
          .lastOption

      // Attempt to resolve the reference to a FQCN
      val resolvedReferencesOption: Option[Set[PsiElement]] =
        lastReferenceOption
          .collect({
            case eipReference: EipSimpleReference =>
              eipReference.resolveEip(typeEnvironment)
          })

      val resolvedBodyFqcn: Option[Set[String]] =
          resolvedReferencesOption.map(elems => elems.collect({
            case psiClass: PsiClass =>
              Some(psiClass.getQualifiedName)
            case psiMethod: PsiMethod =>
              MethodTypeInference.getReturnTypeClass(psiMethod).map(_.getQualifiedName)
          }))
          .map(_.flatten)
          .map(s => if(s.isEmpty) Set(CommonClassNames.JAVA_LANG_OBJECT) else s)
      //inferredAccess.getOrElse(typeCheckCamel(camelFuncBody.getFunctionCall))

      resolvedBodyFqcn

    /**
     * Fall through statement - this language can not be resolved.
     * This should never happen however, as the language's type judgements
     * should be completely defined, and therefore defined for all elements
     */
    case _ =>
      None
  }
}
