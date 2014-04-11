package foo.language.typeChecking

import foo.language.generated.psi._
import foo.language.Core.CamelPsiFile
import com.intellij.psi._
import scala.Some
import foo.traversal.MethodTypeInference
import foo.language.references.EipSimpleReference
import foo.intermediaterepresentation.model.types.TypeEnvironment
import scala.collection.JavaConverters._
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope

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
      val inferredMethodAccess = inferMethodAccess(typeEnvironment, camelFuncBody)

      if(inferredMethodAccess.isDefined) inferredMethodAccess
      else typeCheck(typeEnvironment, camelFuncBody.getFunctionCall)

    /**
     * Infers the type information for a camel function call, IE
     * bodyAs(e), mandatoryBodyAs(e), headerAs(s1, e1)
     */
    case camelFunctionCall: CamelFunctionCall =>
      val functionName = camelFunctionCall.getFunctionName

      // Attempt to match the given function name
      val functionArgs = Option(camelFunctionCall.getFunctionArgs).map(_.getFunctionArgList.asScala.toList).getOrElse(List())
      val resolvedType = (functionName.getText, functionArgs) match {
        case ("bodyAs" | "mandatoryBodyAs", arg1 :: args) =>
          val fqcnText = Option(arg1.getFqcn).map(_.getText)
          // As per the specification \Omega(fqcn) should resolve, otherwise default.
          val resolvedClass = fqcnText.flatMap(fqcn => omega(fqcn, arg1.getProject))

          resolvedClass match {
            case Some(ref) => Some(Set(ref.getQualifiedName))
            case None => Some(Set(CommonClassNames.JAVA_LANG_OBJECT))
          }
        case _ => None
      }

      resolvedType

    /**
     * Fall through statement - this language can not be resolved.
     * This should never happen however, as the language's type judgements
     * should be completely defined, and therefore defined for all elements.
     *
     * Note this will additionally handle the scenario of a null child element
     * for instance.
     */
    case _ =>
      None
  }

  /**
   * Function which provides access to resolve a given text to a resolved class
   * @param fqcnText The FQCN text
   * @param project The associated project
   * @return the associated type information for this fqcn, otherwise None
   */
  private def omega(fqcnText: String, project: Project): Option[PsiClass] = {
    val resolvedClass =  Option(JavaPsiFacade.getInstance(project).findClass(fqcnText, GlobalSearchScope.allScope(project)))
    resolvedClass
  }

  /**
   * Attempts to infer the resolved method access for the given CamelCamelFuncBody expression
   * @param typeEnvironment The current type environment
   * @param camelFuncBody THe current CamelCamelFuncBody instane
   * @return The inferred body types which may or may not successfully have been resolved.
   */
  private def inferMethodAccess(typeEnvironment: TypeEnvironment, camelFuncBody: CamelCamelFuncBody): Option[Set[String]] = {
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
  }
}
