package foo.language.completion

import com.intellij.codeInsight.completion._
import com.intellij.util.ProcessingContext
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns._
import foo.language.generated.CamelTypes
import foo.language.generated.psi.{CamelCamelExpression, CamelFunctionArgs, CamelCamelFuncBody}
import foo.language.elements.CamelBaseElementType
import com.intellij.patterns.StandardPatterns._
import com.intellij.psi.PsiElement
import Patterns._

/**
 * Provides contribution for headers within the relevant places of the camel language
 */
class HeaderContribution extends CompletionContributor {
  val HEADER = VariableAccessorTest.afterVariableObject(List("header"), true)
  val IN_HEADER = VariableAccessorTest.afterVariableObject(List("in", "header"), true)
  val OUT_HEADER = VariableAccessorTest.afterVariableObject(List("out", "header"), true)

  val HEADERS = VariableAccessorTest.afterVariableObject(List("headers"), true)
  val IN_HEADERS = VariableAccessorTest.afterVariableObject(List("in", "headers"), true)
  val OUT_HEADERS = VariableAccessorTest.afterVariableObject(List("out", "headers"), true)

 /* val HEADER_AS =
    psiElement(CamelTypes.STRING)*/

  /**
   * Union all possible header patterns for contribution
   */
   val unionPattern =
    or(
      HEADER,
      IN_HEADER,
      OUT_HEADER,

      HEADERS,
      OUT_HEADERS,
      IN_HEADERS

      //HEADER_AS
    )

  extend(CompletionType.BASIC,
    unionPattern,
    new CompletionProvider[CompletionParameters]() {
      /**
       * Provides the completions available under the given context
       * @param parameters
       * @param context
       * @param result
       */
      def addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
        // Convert all available completions to a Lookup Builder that IJ understands
        ('a' to 'l').map(_.toString)
          .map(LookupElementBuilder.create)
          // Register each lookup element
          .foreach(result.addElement)
      }
    }
  )

}
