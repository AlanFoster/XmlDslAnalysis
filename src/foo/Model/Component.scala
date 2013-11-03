package foo.Model

import com.intellij.util.xml.{GenericAttributeValue, DomElement, Attribute}
import org.jetbrains.annotations._

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 03/11/13
 * Time: 02:03
 * To change this template use File | Settings | File Templates.
 */
trait Component extends DomElement {
  @NotNull
  @Attribute("id")
  def getId: GenericAttributeValue[String]

  @NotNull
  @Attribute("uri")
  def getUri: GenericAttributeValue[String]
}
