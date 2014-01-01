package foo.language.completion

import com.intellij.codeInsight.completion._
import com.intellij.patterns.{ElementPattern, PlatformPatterns}
import com.intellij.util.ProcessingContext
import com.intellij.codeInsight.lookup.{LookupElement, LookupElementBuilder}
import com.intellij.psi.PsiElement
import foo.language.generated.psi.{CamelFunctionArgs, CamelCamelExpression, CamelCamelFunction, CamelCamelFuncBody}
import foo.language.elements.CamelBaseElementType
import foo.language.generated.CamelTypes

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
  val VARIABLE = psiElement().inside(classOf[CamelCamelFuncBody])
          .andNot(psiElement().inside(classOf[CamelFunctionArgs]))


  val OPERATOR = psiElement().withParent(classOf[CamelCamelExpression])
    // .withElementType(CamelTypes.CAMEL_EXPRESSION))

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
     Completion("camelId"),
     Completion("camelContext.OGNL"),
     Completion("exchangeId"),
     Completion("id"),
     Completion("body"),
     Completion("in.body"),
     Completion("body.OGNL"),
     Completion("in.body.OGNL"),
     Completion("bodyAs", "(type)"),
     Completion("mandatoryBodyAs", "(type)"),
     Completion("out.body"),
     Completion("header.foo"),
     Completion("header[foo]"),
     Completion("headers.foo"),
     Completion("headers[foo]"),
     Completion("in.header.foo"),
     Completion("in.header[foo]"),
     Completion("in.headers.foo"),
     Completion("in.headers[foo]"),
     Completion("header.foo[bar]"),
     Completion("in.header.foo[bar]"),
     Completion("in.headers.foo[bar]"),
     Completion("header.foo.OGNL"),
     Completion("in.header.foo.OGNL"),
     Completion("in.headers.foo.OGNL"),
     Completion("out.header.foo"),
     Completion("out.header[foo]"),
     Completion("out.headers.foo"),
     Completion("out.headers[foo]"),
     Completion("headerAs", "(key,type)"),
     Completion("headers"),
     Completion("in.headers"),
     Completion("property.foo"),
     Completion("property[foo]"),
     Completion("property.foo.OGNL"),
     Completion("sys.foo"),
     Completion("sysenv.foo"),
     Completion("exception"),
     Completion("exception.OGNL"),
     Completion("exception.message"),
     Completion("exception.stacktrace"),
     Completion("date:command:pattern"),
     Completion("bean:bean"),
     Completion("properties:locations:key"),
     Completion("routeId"),
     Completion("threadName"),
     Completion("ref:xxx"),
     Completion("type:name.field")
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

/*  override def fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) = {
    val context = new ProcessingContext()
    val accepts = psiElement().withParent(classOf[CamelCamelExpression]).accepts(parameters.getPosition, context)
    super.fillCompletionVariants(parameters, result)
  }*/

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

        def addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
          completions.foreach(completion => {
              val lookupBuilder =
                LookupElementBuilder
                  .create(completion.lookupString)
                  .appendTailText(completion.tail, false)
                  .withInsertHandler(
                    if(completion.isFunction) new FunctionInsertHandler()
                    else null
                )

             result.addElement(lookupBuilder)
          })
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


