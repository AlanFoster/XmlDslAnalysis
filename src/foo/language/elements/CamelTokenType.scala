package foo.language.elements

import com.intellij.psi.tree.IElementType
import foo.language.Core.CamelLanguage


/**
 * Represents the TokenType of token produced by a language. Note Each token is
 * associated with a single language, in this case Apache Camel's language.
 *
 * Note this class represents the lowest 'leaf' within a PSI Tree, ie it has
 * no immediately children. A CamelElementType however will contain such a composition
 * of elements.
 *
 * @see CamelElementType
 */
class CamelTokenType(debugName: String) extends IElementType(debugName, CamelLanguage) {
  override def toString: String = s"{CamelTokenType ${super.toString}}"
}
