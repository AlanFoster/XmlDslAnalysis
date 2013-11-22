package foo.language.elements

import com.intellij.psi.tree.IElementType
import foo.language.Core.CamelLanguage

/**
 * Represents a complex ElementType within the system. For instance,
 * this class represents a node within a graph which is not a leaf.
 *
 * @param debugName The given name which will be output for debugging purposes
 * @see CamelTokenType
 */
class CamelElementType(debugName: String) extends IElementType(debugName, CamelLanguage)