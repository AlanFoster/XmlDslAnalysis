package foo.language.completion

import com.intellij.codeInsight.completion.{InsertionContext, InsertHandler}
import com.intellij.codeInsight.lookup.LookupElement

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


