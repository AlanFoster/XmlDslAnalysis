package foo.language

import com.intellij.codeInsight.completion._
import com.intellij.patterns.{ElementPattern, PsiElementPattern, PlatformPatterns}
import foo.language.Core.CamelLanguage
import com.intellij.util.ProcessingContext
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import foo.language.psi.{CamelCamelFunction, CamelCamelVariable}
import foo.language.elements.CamelBaseElementType
import com.intellij.psi.tree.TokenSet

/**
 * Provides basic code completion for common camel language variables
 * In terms of IntelliJ's API - Contribution Completion refers to intellisense
 * that does not have an explicit Reference - IE useful for keywords etc
 */
class CamelVariableCompletionContributor extends CompletionContributor {
  import PlatformPatterns._

  val VARIABLE = psiElement().inside(classOf[CamelCamelVariable])

  val OPERATOR = psiElement().afterLeaf(psiElement.withElementType(CamelTypes.CAMEL_EXPRESSION))


  /*
  TokenSet.create(
    CamelTypes.AND_AND,
    CamelTypes.OR_OR,
    CamelTypes.EQ_EQ,
    CamelTypes.GT,
    CamelTypes.GT_EQ,
    CamelTypes.LT,
    CamelTypes.LT_EQ
  )
   */

  addCompletion(
    VARIABLE,
    List(
      "body",
      "in",
      "in.body",
      "headers",
      "in.headers"
    )
  )

  // TODO We potentially have this list already within the camel syntax highlighting
  addCompletion(
    psiElement(),
    List(
      CamelBaseElementType.getName(CamelTypes.AND_AND),
      CamelBaseElementType.getName(CamelTypes.OR_OR),
      CamelBaseElementType.getName(CamelTypes.EQ_EQ),
      CamelBaseElementType.getName(CamelTypes.GT),
      CamelBaseElementType.getName(CamelTypes.GT_EQ),
      CamelBaseElementType.getName(CamelTypes.LT),
      CamelBaseElementType.getName(CamelTypes.LT_EQ)
    )
  )

  /**
   * Helper function to add the providers code completions for the given elementPattern
   *
   * @param elementPattern The element pattern to use
   * @param completions The possible completions
   */
  def addCompletion(elementPattern: ElementPattern[_ <: PsiElement], completions: List[String]) {
    extend(CompletionType.BASIC,
      elementPattern,
      new CompletionProvider[CompletionParameters]() {
        def addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
          completions.foreach(label => result.addElement(LookupElementBuilder.create(label)))
        }
      }
    )
  }
}