package foo.language.completion

import com.intellij.codeInsight.completion._
import com.intellij.patterns._
import com.intellij.patterns.StandardPatterns.collection
import com.intellij.util.ProcessingContext
import com.intellij.codeInsight.lookup.{LookupElement, LookupElementBuilder}
import com.intellij.psi.PsiElement
import foo.language.generated.psi.{CamelFunctionArgs, CamelCamelExpression, CamelCamelFunction, CamelCamelFuncBody}
import foo.language.elements.CamelBaseElementType
import foo.language.generated.CamelTypes
import com.intellij.openapi.module.{ModuleUtilCore, ModuleUtil}

/**
 * Provides basic code completion for common camel language variables
 * In terms of IntelliJ's API - Contribution Completion refers to intellisense
 * that does not have an explicit Reference - IE useful for keywords etc
 */
class CamelVariableCompletionContributor extends CompletionContributor {

  import PlatformPatterns._

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
   * A variable will be placed between ${..} however, <b>not</b> nested inside a
   * function call, ie ${bodyAs(...)}
   */
  def VARIABLE() = {
    val element = psiElement(CamelTypes.IDENTIFIER)
    element
      .inside(classOf[CamelCamelFuncBody])
      .andNot(element.inside(classOf[CamelFunctionArgs]))
  }

  def afterVariableObject(variableName: String) =
    VARIABLE()
      .and(psiElement().afterLeaf(psiElement(CamelTypes.DOT).afterLeaf(psiElement().withText(variableName))))

  /**
   * An 'in' variable, will be only available after `in.&gt;caret&lt;`
   */
  val IN_VARIABLE = afterVariableObject("in")
  /**
   * Matches the 'out' variable
   */
  val OUT_VARIABLE = afterVariableObject("out")

  val OPERATOR = psiElement().withParent(classOf[CamelCamelExpression])
  // .withElementType(CamelTypes.CAMEL_EXPRESSION))

  /**
   * A pattern which will successfully match in an empty camel func body,
   * other than itself
   */
  val EMPTY_VARIABLE = {
    val variable = VARIABLE()
    variable
      .withSuperParent(2,
        psiElement(classOf[CamelCamelFuncBody])
          .withChildren(
            collection(classOf[PsiElement]).size(1)
          )
      )
  }

  addCompletion(
    IN_VARIABLE,
    List(
      Completion("body"),
      Completion("header"),
      Completion("headers")
    )
  )

  addCompletion(
    OUT_VARIABLE,
    List(
      Completion("body"),
      Completion("header"),
      Completion("headers")
    )
  )

  /**
   * Register completion requirements for header access
   */
  /*  addCompletion(
      // HEADER_VARIABLE.or(HEADERS_VARIABLE)
      EMPTY_VARIABLE,
      List(
         // TODO EIP contribution!!
      )
    )*/

  /**
   * Register completion requirements for the exception element
   */
  /*  addCompletion(
      // EXCEPTION_VARIABLE
      EMPTY_VARIABLE,
      List(
        Completion("message"),
        Completion("stacktrace")
      )
    )*/

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

  // TODO We potentially have this list already within the camel syntax highlighting
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

/**
 * FunctionInsertHandler which inserts the given lookupstring into the current
 * document, and leaves the caret in the correct position - ie between left/right parens
 */
class FunctionInsertHandler extends InsertHandler[LookupElement] {
  def handleInsert(context: InsertionContext, item: LookupElement) {
    val (document, caretModel) = (context.getDocument, context.getEditor.getCaretModel)
    val offset = caretModel.getOffset

    // Insert the new function based on the previous lookup string, with zero args
    val argumentList = "()"

    document.insertString(offset, argumentList)

    // Update the caret position to be placed directly after the first brace
    caretModel.moveToOffset(offset + 1)
  }
}


