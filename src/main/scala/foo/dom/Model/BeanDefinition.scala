package foo.dom.Model

import com.intellij.util.xml.{GenericAttributeValue, Attribute, DomElement}
import org.jetbrains.annotations.NotNull

trait BeanDefinition extends DomElement with ProcessorDefinition {
  @NotNull
  @Attribute("ref")
  def getRef: GenericAttributeValue[String]

  @NotNull
  @Attribute("method")
  def getMethod: GenericAttributeValue[String]
}
