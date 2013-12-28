package foo.Model

import com.intellij.util.xml.{GenericAttributeValue, Attribute, DomElement}
import org.jetbrains.annotations.NotNull

trait SetHeaderProcessorDefinition extends DomElement with ExpressionDefinition {
  @NotNull
  @Attribute("headerName")
  def getHeaderName: GenericAttributeValue[String]
}
