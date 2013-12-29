package foo.dom.Model

import com.intellij.util.xml.{GenericAttributeValue, DomElement, Attribute}
import org.jetbrains.annotations._

trait ProcessorDefinition extends DomElement {
  @NotNull
  @Attribute("id")
  def getId: GenericAttributeValue[String]

  // TODO Not all classes have this information :)
  @NotNull
  @Attribute("uri")
  def getUri: GenericAttributeValue[String]
}
