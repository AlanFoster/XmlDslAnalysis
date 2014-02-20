package foo.dom.Model

import com.intellij.util.xml._
import org.jetbrains.annotations.NotNull
import foo.dom.converters.BlueprintBeanConverter

trait BeanDefinition extends DomElement with ProcessorDefinition {
  @SubTag("ref")
  @Required(value = true, nonEmpty = true, identifier = false)
  @Convert(value = classOf[BlueprintBeanConverter], soft = false)
  def getRef: GenericAttributeValue[BlueprintBean]

  @NotNull
  @Attribute("method")
  def getMethod: GenericAttributeValue[String]
}
