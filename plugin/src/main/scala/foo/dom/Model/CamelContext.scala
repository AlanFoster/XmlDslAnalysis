package foo.dom.Model

import com.intellij.util.xml._


trait CamelContext extends DomElement {

  @SubTagList("route")
  def getRoutes: java.util.List[Route]

  @Attribute("trace")
  def getTrace: GenericAttributeValue[String]
}
