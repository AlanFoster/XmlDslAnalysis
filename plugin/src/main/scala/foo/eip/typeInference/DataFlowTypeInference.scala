package foo.eip.typeInference

import foo.eip.model._
import com.intellij.psi.CommonClassNames
import foo.eip.model.TypeEnvironment
import foo.eip.model.Inferred
import foo.eip.model.From
import foo.eip.model.Route
import scala.Some
import foo.eip.model.To

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
    // TODO - Need type classes perhaps
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
  def performTypeInference[T >: Processor](typeEnvironment: TypeEnvironment, processor: T): T = processor match {
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
      val (newTypeEnvs, newChildren) = whens.foldLeft((List(typeEnvironment), List[When]()))({
        case ((typeEnvs@(typeEnv :: _), previous), next) =>
          val mappedProcessor = performTypeInference(typeEnv, next)
          val newEnv = mappedProcessor.typeInformation match {
            case NotInferred =>
              throw new Error("Could not infer the correct data type")
            case Inferred(_, after) => after
          }
          (newEnv :: typeEnvs, previous :+ mappedProcessor.asInstanceOf[When])
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
      val expressionTypeInformation = inferExpressionTypeInformation(expression)
      val newHeaders = typeEnvironment.headers + (headerName -> (expressionTypeInformation, reference))
      val newTypeEnvironment = typeEnvironment
        .copy(headers = newHeaders)
      setHeader.copy(typeInformation = Inferred(typeEnvironment, newTypeEnvironment))

    /**
     * The body's type information may be updated after running through an expression element
     */
    case setBody@SetBody(expression, _, _) =>
      val expressionTypeInformation = inferExpressionTypeInformation(expression)
      val newTypeEnvironment = typeEnvironment.copy(body = Set(expressionTypeInformation))
      setBody.copy(typeInformation = Inferred(typeEnvironment, newTypeEnvironment))
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
  def inferExpressionTypeInformation(expression: Expression): String = expression match {
    /**
     * Constants may only return Strings
     */
    case constant: Constant => CommonClassNames.JAVA_LANG_STRING

    /**
     * The
     */
    case Simple(value, Some(fqcn)) =>
      fqcn

    /**
     * TODO A lexer/parser instance may need spawned on this expression to infer this information
     */
    case Simple(value, None) =>
      // TODO Complex/Concrete implementation
      CommonClassNames.JAVA_LANG_OBJECT

    /**
     * By default we should supply no known type information for unknown type expressions
     */
    case _ => CommonClassNames.JAVA_LANG_OBJECT
  }
}
