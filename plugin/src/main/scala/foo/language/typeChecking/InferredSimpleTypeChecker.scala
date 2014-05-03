package foo.language.typeChecking

import foo.language.generated.psi._
import foo.language.Core.CamelPsiFile
import com.intellij.psi._
import scala.Some
import foo.language.references.EipSimpleReference
import foo.intermediaterepresentation.model.types.{CamelReferenceType, BaseType, CamelType, TypeEnvironment}
import scala.collection.JavaConverters._
import com.intellij.openapi.project.Project
import foo.intermediaterepresentation.model.{CoreConstants, ReadonlyTypeEnvironment}
import foo.intermediaterepresentation.model.types.CamelStaticTypes.ACSLFqcn

/**
 * A concrete implementation of a SimpleTypeCheck which relies on recursively
 * resolving a given Camel expression, based on type judgements/inference rules
 */
class InferredSimpleTypeChecker extends SimpleTypeChecker with ReadonlyTypeEnvironment {
  /**
   * {@inheritdoc}
   */
  override def typeCheckCamel(typeEnvironment: TypeEnvironment, camelPsiFile: PsiElement): Option[Set[String]] = {
    typeCheck(typeEnvironment, camelPsiFile)
  }

  /**
   * Performs type checking on the expression
   * @param typeEnvironment The current type environment associated with this expression
   * @param psiElement The current expression to perform type checking on
   * @return The inferred type information associated with this expression
   */
  private def typeCheck(typeEnvironment: TypeEnvironment, psiElement: PsiElement): Option[Set[ACSLFqcn]] = psiElement match {
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
     * Camel Literals - IE numbers, string, truthy values etc.
     * These represent the axioms within the type checking rules.
     */
    case literal:CamelLiteral =>

     if(literal.getTruthy != null) Some(Set(CommonClassNames.JAVA_LANG_BOOLEAN))
     else if(literal.getString != null) Some(Set(CommonClassNames.JAVA_LANG_STRING))
     else if(literal.getNumber != null) Some(Set(CommonClassNames.JAVA_LANG_DOUBLE))
     else if(literal.getNully != null) Some(Set(CommonClassNames.JAVA_LANG_OBJECT))
     else None

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
     * A camel argument definition, attempts to resolve to the first non-null psi element
     * and return the type environment of that
     */
    case camelArg: CamelFunctionArg =>
      val resolvedArgument =
        List(Option(camelArg.getCamelFunction), Option(camelArg.getFqcn), Option(camelArg.getLiteral))
          .flatten.headOption

      val inferredType = resolvedArgument.flatMap(typeCheckCamel(typeEnvironment, _))
      inferredType

    /**
     * Infers the type information for a camel function call, IE
     * bodyAs(e), mandatoryBodyAs(e), headerAs(s1, e1)
     */
    case camelFunctionCall: CamelFunctionCall =>
      // Attempts to resolve the type information for the given fqcnText and project
      def resolveFqcnArgument(argument: CamelFunctionArg, project: Project): Option[Set[ACSLFqcn]] = {
        val fqcnOptionText = Option(argument.getFqcn).map(_.getText)
        // As per the specification \Omega(fqcn) should resolve, otherwise default.
        val resolvedClass = fqcnOptionText.flatMap(fqcnText => omega(fqcnText, project))

        resolvedClass match {
          case Some(ref) => Some(Set(ref.getQualifiedName))
          case None => Some(Set(CommonClassNames.JAVA_LANG_OBJECT))
        }
      }

      // Extract the function name and args, avoiding a possible NPE
      val functionName = camelFunctionCall.getFunctionName
      val functionArgs = Option(camelFunctionCall.getFunctionArgs).map(_.getFunctionArgList.asScala.toList).getOrElse(List())
      val project = camelFunctionCall.getProject

      // Attempt to match the given function name
      // And subsequently attempt to extract the FQCN reference from the appropriate argument
      val resolvedType = (functionName.getText, functionArgs) match {
        case ("bodyAs" | "mandatoryBodyAs", arg1 :: args) =>
          resolveFqcnArgument(arg1, project)
        case ("headerAs", _ :: arg2 :: args) =>
          resolveFqcnArgument(arg2, project)
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
   * Attempts to infer the resolved method access for the given CamelCamelFuncBody expression
   * @param typeEnvironment The current type environment
   * @param camelFuncBody The current CamelCamelFuncBody instance
   * @return The inferred body types which may or may not successfully have been resolved.
   */
  private def inferMethodAccess(typeEnvironment: TypeEnvironment, camelFuncBody: CamelCamelFuncBody): Option[Set[ACSLFqcn]] = {
    // Attempt to get the last reference within the body element
    val lastReferenceOption: Option[PsiReference] =
      camelFuncBody.getReferences.toList
        .sortBy(_.getRangeInElement.getEndOffset)
        .lastOption

    if(lastReferenceOption.isDefined) inferBodyAccess(typeEnvironment, lastReferenceOption)
    else inferHeaderAccess(typeEnvironment, camelFuncBody)
  }

  private def inferHeaderAccess(typeEnvironment: TypeEnvironment, camelFuncBody: CamelCamelFuncBody): Option[Set[ACSLFqcn]] = {
    def findLastAccess(access: CamelVariableAccess): CamelVariableAccess = {
        val accessList = access.getVariableAccessList
        val lastOption = accessList.asScala.lastOption
        lastOption match {
          case Some(newAccess) => findLastAccess(newAccess)
          case None => access
        }
    }

    val resolvedEipReference = for {
      topAccess <- Option(camelFuncBody.getVariableAccess)
      lastAccess = findLastAccess(topAccess)
      references = lastAccess.getReferences
      lastReference = references.lastOption
      eipReference <- resolveEipReference(typeEnvironment, lastReference)
    } yield eipReference

    resolvedEipReference
  }

  /**
   * Infers the body access of a given reference
   */
  private def inferBodyAccess(typeEnvironment: TypeEnvironment, lastReferenceOption: Option[PsiReference]): Option[Set[ACSLFqcn]] =
    resolveEipReference(typeEnvironment, lastReferenceOption)

  /**
   * Attempts to resolve an EIP reference into the appropriate types
   */
  private def resolveEipReference(typeEnvironment: TypeEnvironment, reference: Option[PsiReference]): Option[Set[ACSLFqcn]] = {
    val resolvedReferencesOption: Option[Set[CamelType]] =
      reference.collect({
          case eipReference: EipSimpleReference =>
            eipReference.resolveEip(typeEnvironment)
        })

    val typesOption = resolvedReferencesOption.map(set => set.map({
      case BaseType(fqcn) => fqcn
      case CamelReferenceType(BaseType(fqcn), _) => fqcn
    }))

    typesOption.map(set =>
      if(set.isEmpty) Set(CoreConstants.DEFAULT_INFERRED_TYPE)
      else set
    )
  }
}
