package foo.intermediaterepresentation.typeInference

import foo.intermediaterepresentation.model._
import com.intellij.psi.CommonClassNames
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil
import foo.language.Core.CamelPsiFile
import foo.language.typeChecking.SimpleTypeChecker
import foo.intermediaterepresentation.model.types.TypeEnvironment
import foo.intermediaterepresentation.model.references.{DomReference, NoReference, ExpressionReference}
import foo.intermediaterepresentation.model.expressions.Expression
import foo.intermediaterepresentation.model.processors._
import foo.intermediaterepresentation.model.expressions.Constant
import foo.intermediaterepresentation.model.processors.Route
import scala.Some
import foo.intermediaterepresentation.model.expressions.Simple
import foo.intermediaterepresentation.model.types.Inferred
import foo.intermediaterepresentation.model.processors.From
import foo.intermediaterepresentation.model.processors.To
import CoreConstants._

/**
 * Performs type inference on a given Abstract Model representation,
 * by using the concept of type information 'flowing' between
 * processors, and unioning their type information.
 */
class TypePropagationTypeInference(simpleTypeChecker: SimpleTypeChecker) extends AbstractModelTypeInference with ReadonlyTypeEnvironment {

  /**
   * Performs type inference on the given model
   * @param route The untyped model
   * @return The typed model
   */
  override def performTypeInference(route: Route): Route = {
    val startingTypeEnvironment = TypeEnvironment(Set(DEFAULT_INFERRED_TYPE), Map())
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
  private def performTypeInference[T >: Processor](typeEnvironment: TypeEnvironment,
                                           processor: T): T = processor match {
    /**
     * Ensure that Route information takes into consideration its children
     */
    case route@Route(children, _, _) =>
      val (newTypeEnv, newChildren) = pipelineChildren(typeEnvironment, children)

      route.copy(
        children = newChildren,
        typeInformation = Inferred(typeEnvironment, newTypeEnv)
      )

    /**
     * Camel Choice element, ensuring that When elements are allocated as expected
     */
    case choice@Choice(whens, otherwiseOption, _, _) =>
      // Note that each child when node has access to the initial choice element's initial environment always
      // And not the previous node - which differs to the implementation of the when processor implementation
      val (newWhenTypeEnvs, newWhenChildren) =
        whens.map(when => performTypeInference(typeEnvironment, when).asInstanceOf[When])
        .foldLeft((List[TypeEnvironment](), List[When]()))({
          case ((accumulatedTypeEnvironments, accumulatedWhenExpressions), when) =>
            (when.after.get :: accumulatedTypeEnvironments, accumulatedWhenExpressions :+ when)
        })

      // Perform type information propagation on the otherwise node if it exists
      val newOtherwiseOption =
        otherwiseOption
          .map(choice => performTypeInference(typeEnvironment, choice).asInstanceOf[Otherwise])

      val newTypeEnvironment =
        // If the otherwise element is defined, then logic dictates that we must have gone down
        // at least one of the given paths, therefore the original type environment should not
        // be unioned! :)
        newOtherwiseOption.map(otherwise => mergeTypeEnvironment(otherwise.after.get, newWhenTypeEnvs))
        // If no otherwise element exists, then merge the initial type environment when all when elements
        .getOrElse(mergeTypeEnvironment(typeEnvironment, newWhenTypeEnvs))

      choice.copy(
        whens = newWhenChildren,
        otherwiseOption = newOtherwiseOption,
        typeInformation = Inferred(typeEnvironment, newTypeEnvironment)
      )

    /**
     * Ensure Bean type information propagates as expected within the bean environment
     */
    case bean@Bean(_, method, _, _) =>
      // Extract the return type of the referenced method within the bean environment
      val inferredBody = mR(method.flatMap(m => Option(m.getValue)))
      val newTypeInformation = typeEnvironment.copy(body = Set(inferredBody))

      bean.copy(typeInformation = Inferred(typeEnvironment, newTypeInformation))

    /**
     * Unions the type information associated with the otherwise element, in a pipeline
     * fashion, similar to both When and Route processors
     */
    case otherwise@Otherwise(children, _, _) =>
      val (newTypeEnv, newChildren) = pipelineChildren(typeEnvironment, children)

      otherwise.copy(
        children = newChildren,
        typeInformation = Inferred(typeEnvironment, newTypeEnv)
      )

    /**
     * Ensure that When elements have their children's type information accounted for
     * as expected
     */
    case when@When(expression, children, _, _) =>
      val (newTypeEnv, newChildren) = pipelineChildren(typeEnvironment, children)

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
     * A log message will not mutate the exchange properties
     */
    case log: Log =>
      log.copy(typeInformation = Inferred(typeEnvironment, typeEnvironment))

    /**
     * By default the from definition does not contain any possible type inference information
     * This could be updated within a Camel PR perhaps
     */
    case from: From =>
      from.copy(typeInformation = Inferred(typeEnvironment, typeEnvironment))

    /**
     * WireTaps are read only, and the original content should not be affected
     */
    case wireTap: WireTap =>
      wireTap.copy(typeInformation = Inferred(typeEnvironment, typeEnvironment))

    /**
     * The type information of expressions applied to setHeader can update the type environment
     */
    case setHeader@SetHeader(headerNameOption, expression, reference, _) =>
      val inferredTypeEnvironment = {
        val expressionTypeInformation = inferExpressionTypeInformation(typeEnvironment, expression)

        // We should keep track of this header information, as long as it is valid.
        val newHeaders = headerNameOption match {
          case Some(headerName) =>
            // Note, the limitation in a single inferred header type... DataModel limitations! :)
            val inferredHeaderType = expressionTypeInformation.headOption.getOrElse(DEFAULT_INFERRED_TYPE)
            typeEnvironment.headers + (headerName -> (inferredHeaderType, reference))
          case None => typeEnvironment.headers
        }
        val newTypeEnvironment = typeEnvironment
          .copy(headers = newHeaders)
        newTypeEnvironment
      }
      setHeader.copy(typeInformation = Inferred(typeEnvironment, inferredTypeEnvironment))

    /**
     * Camel RemoveHeader element, remove a header if it it exists within the type environment,
     * and the option is well defined
     */
    case removeHeader@RemoveHeader(headerNameOption, reference, _) =>
      val filteredHeaders = {
        headerNameOption match {
          case None => typeEnvironment.headers
          case Some(headerName) =>
            typeEnvironment.headers.filterKeys(_ != headerName)
        }
      }
      val inferredTypeEnvironment = typeEnvironment.copy(headers = filteredHeaders)
      removeHeader.copy(typeInformation = Inferred(typeEnvironment, inferredTypeEnvironment))

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
  private def mergeTypeEnvironment(current: TypeEnvironment, envs: List[TypeEnvironment]): TypeEnvironment = {
    envs.foldLeft(current)(_ + _)
  }

  /**
   * Performs the pipeline mechanism for a given list of children.
   * @param typeEnvironment The initial type environment
   * @param children The children of the given pipeline
   * @return The new, final, type environment, and the new list of processors which contain
   *         the additional type information.
   */
  private def pipelineChildren(typeEnvironment: TypeEnvironment, children: List[Processor]): (TypeEnvironment, List[Processor]) = {
    // Apply map by folding left with the new type environment
    val stateTuple@(newTypeEnvironment, newChildren) =  children.foldLeft((typeEnvironment, List[Processor]()))({
      case (((typeEnv), previous), next) =>
        val mappedProcessor = performTypeInference(typeEnv, next)
        val newEnv = mappedProcessor.after.get
        (newEnv, previous :+ mappedProcessor)
    })
    stateTuple
  }

  /**
   * Performs type inference on the given expression instance
   * @param expression The expression to infer type information on
   * @return The inferred type information of the given expression
   */
  def inferExpressionTypeInformation(typeEnvironment: TypeEnvironment, expression: Expression): Set[String] = {
    val inferredExpressionTypeInformation = expression match {
      /**
       * Constants may only return Strings
       */
      case constant: Constant => Some(Set(CommonClassNames.JAVA_LANG_STRING))

      /**
       * A simple expression with a supplied resultType will be used over the inferred
       * output of the expression value
       */
      case Simple(value, Some(fqcn), reference) =>
        // Extract the required project
        val projectOption = reference.getProject
        val resolvedFqcn = projectOption.flatMap(project => omega(fqcn, project)).map(_.getQualifiedName)
        resolvedFqcn.map(fqcn => Set(fqcn))

      /**
       * When no resultType is set we should infer it
       */
      case Simple(value, None, reference) =>
        val psiElementOption = reference match {
          case NoReference | DomReference(_) => None
          case ExpressionReference(element) =>
            Some(element)
        }

        psiElementOption match {
          case None => None
          case Some(element) =>
            val psiFile = element.getContainingFile
            val textOffset = element.getValue.getTextRange.getStartOffset

            // Attempt to infer the type of the camel expression
            val camelPsiFile = InjectedLanguageUtil.findInjectedPsiNoCommit(psiFile, textOffset).asInstanceOf[CamelPsiFile]
            val resolvedFqcn = simpleTypeChecker.typeCheckCamel(typeEnvironment, camelPsiFile)

            resolvedFqcn
        }

      case _ => None
    }

    inferredExpressionTypeInformation.getOrElse(Set(DEFAULT_INFERRED_TYPE))
  }
}