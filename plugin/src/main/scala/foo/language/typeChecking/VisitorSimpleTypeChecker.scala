package foo.language.typeChecking

import foo.language.generated.psi._
import foo.language.Core.CamelPsiFile
import com.intellij.psi.{PsiClass, PsiMethodReferenceUtil, CommonClassNames, PsiElement}
import com.intellij.psi.util.PsiUtil
import com.intellij.psi.impl.source.resolve.{ResolveCache, ResolveVariableUtil}

class VisitorSimpleTypeChecker extends SimpleTypeChecker  {
  override def typeCheckCamel(camelPsiFile: CamelPsiFile): Option[String] = {
    typeCheck(camelPsiFile)
  }

  private def typeCheck(psiElement: PsiElement): Option[String] = psiElement match {
    case file:CamelPsiFile =>
      val expressionOption = file.getChildren.lift(0)

      expressionOption match {
        case Some(expression) =>
          typeCheck(expression)
        case None =>
          Some(CommonClassNames.JAVA_LANG_OBJECT)
      }

    case expression: CamelExpression =>
      val literal = typeCheck(expression.getLiteral)

      if(literal.isDefined) literal
      else typeCheck(expression.getCamelExpression)

    case expression: CamelCamelExpression =>
      val hasOperator = Option(expression.getOperator).isDefined

      // If the expression has an operator it is a proposition
      // Checking if the expression is a WFF will be checked by other semantic passes
      if(hasOperator) Some(CommonClassNames.JAVA_LANG_BOOLEAN)
      else typeCheck(expression.getCamelFunction)

    /**
     * CamelFunction consists of "${" e "}"
     */
    case camelFunction:CamelCamelFunction =>
      typeCheck(camelFunction.getCamelFuncBody)

    case camelFuncBody:CamelCamelFuncBody =>
      val variableAccessOption = Option(camelFuncBody.getVariableAccess)


      val resolvedBodyFqcn = camelFuncBody.getReferences.lastOption.map(_.resolve()).collect({
        case psiClass:PsiClass =>
          psiClass.getQualifiedName
      })


/*      val inferredAccess = for {
        variableAccess <- variableAccessOption
        references <- Some(variableAccess.getReferences)
        lastReference <- references.lastOption
      } yield Some(lastReference.resolve())*/

      //inferredAccess.getOrElse(typeCheckCamel(camelFuncBody.getFunctionCall))

      resolvedBodyFqcn

    case _ =>
      None
  }
}
