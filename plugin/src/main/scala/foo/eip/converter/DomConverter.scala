package foo.eip.converter

import scala.collection.JavaConverters._
import com.intellij.util.xml.DomElement
import foo.dom.Model.Blueprint
import foo.eip.model.Route

/**
 * Concrete implementation of the Converter trait which will convert a Dom
 * structure into an abstract representation
 */
class DomConverter extends Converter[List[DomElement]] {

  /**
   * Converts the given blueprint DOM file into an EipDag
   * @param root The root DOM element
   * @return The converted EipDAG
   */
  def createAbstraction(root:Blueprint): Route = {
    // Extract the given camel routes and create an empty EipDAG
    val domRoutes = root.getCamelContext.getRoutes.asScala
    // There is no need to traverse the given routes if there are no processor definitions
    if (domRoutes.isEmpty || !domRoutes.head.getFrom.exists) Route(Nil)
    else {
      // Convert the first route by default
      val firstRoute = domRoutes.head
      convert(
        firstRoute.getFrom :: firstRoute.getComponents.asScala.toList
      )
    }
  }

  /**
   * Converts the given data model into an abstract representation
   * @param children The child DomElements within this structure
   * @return The abstract route representation of the same data model
   */
  override def convert(children: List[DomElement]): Route = {
    Route(Nil)
  }
}
