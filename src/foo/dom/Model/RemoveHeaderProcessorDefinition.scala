package foo.dom.Model

import com.intellij.util.xml._
import foo.dom.converters.ClearHeaderResolvingConverter

trait RemoveHeaderProcessorDefinition extends DomElement with ProcessorDefinition {
  /**
   * Provide a ResolvingConverter implementation that can provide variants, and with soft reference
   * as true. That is to say, if the converter does not resolve successfully, it should not be
   * highlighted as an error
   * @return The GenericAttributeValue
   */
  @Convert(value = classOf[ClearHeaderResolvingConverter], soft = true)
  @Attribute("headerName")
  def getHeaderName: GenericAttributeValue[String]
}
