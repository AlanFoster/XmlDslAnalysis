package foo.language.elements

import com.intellij.psi.tree.IElementType
import foo.language.Core.CamelLanguage

/**
 * Represents the base ElementType for the apache camel language.
 * All element related classes should extend this, as it is within this class
 * which should offer useful utility methods in the future
 */
class CamelBaseElementType(val debugName: String) extends IElementType(debugName, CamelLanguage)
object CamelBaseElementType {
  def getName(element: IElementType): String = element.asInstanceOf[CamelBaseElementType].debugName
}