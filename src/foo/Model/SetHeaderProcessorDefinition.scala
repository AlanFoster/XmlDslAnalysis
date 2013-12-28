package foo.Model

import com.intellij.util.xml.{NameValue, GenericAttributeValue, Attribute, DomElement}
import org.jetbrains.annotations.NotNull
import foo.Icons
import com.intellij.ide.presentation.Presentation

/**
 * Set Header processor definition.
 * Note the @Presentation will provide icon information during variant lookup
 */
@Presentation(icon = Icons.CamelString)
trait SetHeaderProcessorDefinition extends DomElement with ExpressionDefinition {
  /**
   * getHeader Name. Note this method hs an additional NameValue attribute which will
   * show this attribute's value under variant completion. Likewise when a reference
   * to this element is renamed, this value will be changed.
   *
   * @return The Generic attribute value of type string
   */
  @NotNull
  @Attribute("headerName")
  @NameValue(unique = false, referencable = true)
  def getHeaderName: GenericAttributeValue[String]
}
