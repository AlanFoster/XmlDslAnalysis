package foo.language.completion

import com.intellij.codeInsight.completion._
import com.intellij.patterns._
import com.intellij.util.ProcessingContext
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import foo.language.elements.CamelBaseElementType
import foo.language.generated.CamelTypes
import com.intellij.patterns.StandardPatterns.or

/**
 * Provides basic code completion for common camel language variables
 * In terms of IntelliJ's API - Contribution Completion refers to intellisense
 * that does not have an explicit Reference - IE useful for keywords etc
 */
class CamelVariableCompletionContributor extends CompletionContributor {

  import Patterns._

  /**
   * Represents a code completion tuple
   * @param lookupString The provided lookup string
   * @param tail The tail to show, default by empty.
   *             It is currently assumed that the completion is a function completion if non-empty
   */
  case class Completion(lookupString: String, tail: String = "") {
    val isFunction: Boolean = !tail.isEmpty
  }

  /**
   * Provide access for in and out variable access
   */
  addCompletion(
    or(IN_VARIABLE, OUT_VARIABLE),
    List(
      Completion("body"),
      Completion("header"),
      Completion("headers")
    )
  )

  /**
   * Register completion requirements for the exception element
   */
    addCompletion(
      EXCEPTION_VARIABLE,
      List(
        Completion("message"),
        Completion("stacktrace")
      )
    )

  /**
   * Provide contribution for when the function body is empty.
   * IE The default variables/functions that are accessible
   */
  addCompletion(
    EMPTY_VARIABLE,
    List(
      Completion("camelId"),
      Completion("camelContext"),
      Completion("exchangeId"),
      Completion("id"),
      Completion("in"),
      Completion("out"),
      Completion("body"),
      Completion("bodyAs", "(type)"),
      Completion("mandatoryBodyAs", "(type)"),
      Completion("headerAs", "(key,type)"),
      Completion("sys"),
      Completion("sysenv"),
      Completion("exception"),
      Completion("routeId"),
      Completion("threadName"),
      Completion("header"),
      Completion("headers")

      // Grammar does not support the following - currently
      /*     Completion("date:command:pattern"),
           Completion("bean:bean"),
           Completion("properties:locations:key"),
           Completion("ref:xxx"),
           Completion("type:name.field")*/
    )
  )

  /**
   * Provide operator contribution
   */
  addCompletion(
    OPERATOR,
    List(
      Completion(CamelBaseElementType.getName(CamelTypes.AND_AND)),
      Completion(CamelBaseElementType.getName(CamelTypes.OR_OR)),
      Completion(CamelBaseElementType.getName(CamelTypes.EQ_EQ)),
      Completion(CamelBaseElementType.getName(CamelTypes.GT)),
      Completion(CamelBaseElementType.getName(CamelTypes.GT_EQ)),
      Completion(CamelBaseElementType.getName(CamelTypes.LT)),
      Completion(CamelBaseElementType.getName(CamelTypes.LT_EQ))
    )
  )

  /**
   * Helper function to add the providers code completions for the given elementPattern
   *
   * @param elementPattern The element pattern to use
   * @param completions The possible completions
   */
  def addCompletion(elementPattern: ElementPattern[_ <: PsiElement], completions: List[Completion]) {
    extend(CompletionType.BASIC,
      elementPattern,
      new CompletionProvider[CompletionParameters]() {
        /**
         * Provides the completions available under the given context
         * @param parameters
         * @param context
         * @param result
         */
        def addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
          // Convert all available completions to a Lookup Builder that IJ understands
          completions
            .map(completion => {
                val lookupBuilder =
                  LookupElementBuilder
                    .create(completion.lookupString)
                    .appendTailText(completion.tail, false)
                    .withInsertHandler(
                      if (completion.isFunction) new FunctionInsertHandler()
                      else null
                    )
                lookupBuilder
              })
            // Register each lookup element
            .foreach(result.addElement)
        }
      }
    )
  }
}

