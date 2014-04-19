package foo.dom.Model

import org.jetbrains.annotations.NotNull
import com.intellij.util.xml.{DomElement, GenericAttributeValue, Attribute}

trait LogProcessorDefinition extends DomElement with ProcessorDefinition {
  @NotNull
  @Attribute("message")
  def getMessage: GenericAttributeValue[String]
}
