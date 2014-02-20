package foo.language.formatting

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import com.intellij.psi.tree.TokenSet
import foo.language.generated.CamelTypes

/**
 * Represents the CamelQuoteHandler which propagates the string TokenSet information
 * to the SimpleTokenSetQuoteHandler.
 *
 * When registered this will provide the automatic closing of string literals, and if
 * a string literal is being closed, and the closing literal already exists, it will be replaced
 * rather than adding another literal character again.
 */
class CamelQuoteHandler extends SimpleTokenSetQuoteHandler(CamelQuoteHandler.StringTokenSet)

/**
 * Defines the quote tokens within the apache camel simple language
 */
object CamelQuoteHandler {
  val StringTokenSet = TokenSet.create(CamelTypes.STRING)
}