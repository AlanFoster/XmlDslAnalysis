package foo.intermediaterepresentation.typeInference

import foo.intermediaterepresentation.model._
import com.intellij.psi.CommonClassNames
import scala.Some
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil
import foo.language.Core.CamelPsiFile
import foo.language.typeChecking.CamelSimpleTypeChecker
import foo.intermediaterepresentation.model.types.{NotInferred, Inferred, TypeEnvironment}
import foo.intermediaterepresentation.model.references.{NoReference, ExpressionReference}
import foo.intermediaterepresentation.model.expressions.{Expression, Simple, Constant}
import foo.intermediaterepresentation.model.processors._
import foo.intermediaterepresentation.model.expressions.Constant
import foo.intermediaterepresentation.model.processors.Route
import scala.Some
import foo.intermediaterepresentation.model.expressions.Simple
import foo.intermediaterepresentation.model.types.Inferred
import foo.intermediaterepresentation.model.references.ExpressionReference
import foo.intermediaterepresentation.model.processors.From
import foo.intermediaterepresentation.model.processors.To

/**
 * Performs type inference on a given Abstract Model representation,
 * by using the concept of type information 'flowing' between
 * processors, and unioning their type information.
 */
class DataFlowTypeInference extends AbstractModelTypeInference {
  /**
   * Performs type inference on the given model
   * @param route The untyped model
   * @return The typed model
   */
  override def performTypeInference(route: Route): Route = {
    val startingTypeEnvironment = TypeEnvironment(Set(CommonClassNames.JAVA_LANG_OBJECT), Map())
    performTypeInference(startingTypeEnvironment, route).asInstanceOf[Route]
  }

  /**
   * Performs type inference on the given processor, and returns the node with an
   * updated set of typed semantics
   * @param typeEnvironment The current type environment
   * @param processor The current processor to infer type inference on
   * @tparam T The generic type of the processor to be returned
   * @return A new processor with updated type semantics
   */
  def performTypeInference[T >: Processor](typeEnvironment: TypeEnvironment,
                                           processor: T): T = processor match {
    /**
     * Ensure that Route information takes into consideration its children
     */
    case route@Route(children, _, _) =>

      // Apply map by folding left with the new type environment
      val (newTypeEnv, newChildren) = children.foldLeft((typeEnvironment, List[Processor]()))({
        case (((typeEnv), previous), next) =>
          val mappedProcessor = performTypeInference(typeEnv, next)
          val newEnv = mappedProcessor.typeInformation match {
            case NotInferred =>
              throw new Error("Could not infer the correct data type")
            case Inferred(_, after) => after
          }
          (newEnv, previous :+ mappedProcessor)
      })

      route.copy(
        children = newChildren,
        typeInformation = Inferred(typeEnvironment, newTypeEnv)
      )

    /**
     * Camel Choice element, ensuring that When elements are allocated as expected
     */
    case choice@Choice(whens, _, _) =>
      // Note that each child when node has access to the choice element's initial environment always
      // And not the previous node - which differs to the implementation of the when processor implementation
      val (newTypeEnvs, newChildren) = whens.foldLeft((List(typeEnvironment), List[When]()))({
        case ((accumulatedTypeEnvironments, accumulatedWhenExpressions), next) =>
          // Note that the parent choice type environment is used for each when expression!
          val mappedProcessor = performTypeInference(typeEnvironment, next)
          val newEnv = mappedProcessor.typeInformation match {
            case NotInferred =>
              throw new Error("Could not infer the correct data type")
            case Inferred(_, after) => after
          }
          // Return the tuple of the every accumulated type environment and every when element
          (newEnv :: accumulatedTypeEnvironments, accumulatedWhenExpressions :+ mappedProcessor.asInstanceOf[When])
      })

      // Union the newTypeEnvs together as expected
      val mergedTypeEnv = mergeTypeEnvironment(typeEnvironment, newTypeEnvs)

      choice.copy(
        whens = newChildren,
        typeInformation = Inferred(typeEnvironment, mergedTypeEnv)
      )

    /**
     * Ensure Bean type information propagates as expected within the bean environment
     */
    case bean@Bean(_, method, _, _) =>
      // Extract the return type of the referenced method within the bean environment
      val inferredBody = method match {
        case Some(psiElement) =>
          Option(psiElement.getValue)
            .map(_.getReturnType.getCanonicalText)
            .getOrElse(CommonClassNames.JAVA_LANG_OBJECT)
        case _ => CommonClassNames.JAVA_LANG_OBJECT
      }
      val newTypeInformation = typeEnvironment.copy(body = Set(inferredBody))

      bean.copy(typeInformation = Inferred(typeEnvironment, newTypeInformation))

    /**
     * Ensure that When elements have their children's type information accounted for
     * as expected
     */
    case when@When(expression, children, _, _) =>
      val (newTypeEnv, newChildren) = children.foldLeft((typeEnvironment, List[Processor]()))({
        case (((typeEnv), previous), next) =>
          val mappedProcessor = performTypeInference(typeEnv, next)
          val newEnv = mappedProcessor.typeInformation match {
            case NotInferred =>
              throw new Error("Could not infer the correct data type")
            case Inferred(_, after) => after
          }
          (newEnv, previous :+ mappedProcessor)
      })

      when.copy(
        children = newChildren,
        typeInformation = Inferred(typeEnvironment, newTypeEnv)
      )

    /**
     * Currently to processor definitions do not provide additional type information,
     * IE they are treated as in-only
     */
    case to: To =>
      to.copy(typeInformation = Inferred(typeEnvironment, typeEnvironment))

    /**
     * By default the from definition does not contain any possible type inference information
     * This could be updated within a Camel PR perhaps
     */
    case from: From =>
      from.copy(typeInformation = Inferred(typeEnvironment, typeEnvironment))

    /**
     * The type information of expressions applied to setHeader can update the type environment
     */
    case setHeader@SetHeader(headerName, expression, reference, _) =>
      val inferredTypeEnvironment = {
        val expressionTypeInformation = inferExpressionTypeInformation(typeEnvironment, expression)
        val newHeaders = typeEnvironment.headers + (headerName -> (expressionTypeInformation.headOption.getOrElse(CommonClassNames.JAVA_LANG_OBJECT), reference))
        val newTypeEnvironment = typeEnvironment
          .copy(headers = newHeaders)
        newTypeEnvironment
      }
      setHeader.copy(typeInformation = Inferred(typeEnvironment, inferredTypeEnvironment))

    /**
     * The body's type information may be updated after running through an expression element
     */
    case setBody@SetBody(expression, _, _) =>
      val inferredTypeEnvironment = {
        val expressionTypeInformation = inferExpressionTypeInformation(typeEnvironment, expression)
        val newTypeEnvironment = typeEnvironment.copy(body = expressionTypeInformation)
        newTypeEnvironment
      }

      setBody.copy(typeInformation = Inferred(typeEnvironment, inferredTypeEnvironment))
  }

  /**
   * Merges the given environments into a single type environment
   * @param current The current type environment
   * @param envs The list of possible type environments
   * @return A single unified instance of a TypeEnvironment
   */
  def mergeTypeEnvironment(current: TypeEnvironment, envs: List[TypeEnvironment]): TypeEnvironment = {
    envs.foldLeft(current)(_ + _)
  }

  /**
   * Performs type inference on the given expression instance
   * @param expression The expression to infer type information on
   * @return The inferred type information of the given expression
   */
  def inferExpressionTypeInformation(typeEnvironment: TypeEnvironment, expression: Expression): Set[String] = expression match {
    /**
     * Constants may only return Strings
     */
    case constant: Constant => Set(CommonClassNames.JAVA_LANG_STRING)

    /**
     * A simple expression with a supplied resultType will be used over the inferred
     * output of the expression value
     */
    case Simple(value, Some(fqcn),_ ) =>
      Set(fqcn)

    /**
     * When no resultType is set we should infer it
     */
    case Simple(value, None, reference) =>
      val psiElementOption = reference match {
        case NoReference => None
        case ExpressionReference(element) =>
          Some(element)
      }

      psiElementOption match {
        case None => Set(CommonClassNames.JAVA_LANG_OBJECT)
        case Some(element) =>
          val psiFile = element.getContainingFile
          val textOffset = element.getValue.getTextRange.getStartOffset

          // Attempt to infer the type of the camel expression
          val camelPsiFile = InjectedLanguageUtil.findInjectedPsiNoCommit(psiFile, textOffset).asInstanceOf[CamelPsiFile]
          val resolvedFqcn = new CamelSimpleTypeChecker().typeCheckCamel(typeEnvironment, camelPsiFile)

          resolvedFqcn.getOrElse(Set(CommonClassNames.JAVA_LANG_OBJECT))
      }

    /**
     * By default we should supply no known type information for unknown type expressions
     */
    case _ => Set(CommonClassNames.JAVA_LANG_OBJECT)
  }
}