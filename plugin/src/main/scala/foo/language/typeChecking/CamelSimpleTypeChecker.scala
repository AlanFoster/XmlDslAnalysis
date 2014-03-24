package foo.language.typeChecking

import foo.language.generated.psi._
import foo.language.Core.CamelPsiFile
import com.intellij.psi._
import scala.Some
import foo.traversal.MethodTypeInference
import foo.eip.model.TypeEnvironment
import foo.language.references.EipSimpleReference

class CamelSimpleTypeChecker extends SimpleTypeChecker {
  override def typeCheckCamel(typeEnvironment: TypeEnvironment, camelPsiFile: CamelPsiFile): Option[Set[String]] = {
    typeCheck(typeEnvironment, camelPsiFile)
  }

  private def typeCheck(typeEnvironment: TypeEnvironment, psiElement: PsiElement): Option[Set[String]] = psiElement match {
    case file: CamelPsiFile =>
      val expressionOption = file.getChildren.lift(0)

      expressionOption match {
        case Some(expression) =>
          typeCheck(typeEnvironment, expression)
        case None =>
          Some(Set(CommonClassNames.JAVA_LANG_OBJECT))
      }

    case expression: CamelExpression =>
      val literal = typeCheck(typeEnvironment, expression.getLiteral)

      if (literal.isDefined) literal
      else typeCheck(typeEnvironment, expression.getCamelExpression)

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

    case _ =>
      None
  }
}
