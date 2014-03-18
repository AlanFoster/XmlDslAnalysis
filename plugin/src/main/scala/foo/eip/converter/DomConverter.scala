package foo.eip.converter

import scala.collection.JavaConverters._
import com.intellij.util.xml.DomElement
import foo.dom.Model._
import foo.eip.model._
import foo.eip.graph.model.CamelTypeSemantics
import com.intellij.psi.CommonClassNames
import foo.eip.model.From
import foo.eip.model.Route
import foo.eip.graph.model.CamelTypeSemantics
import foo.eip.model.Expression
import foo.eip.model.To
import foo.eip.model.Choice
import foo.eip.model.Constant
import foo.eip.model.Simple
import foo.eip.model.When
import foo.eip.model.SetHeader
import foo.eip.model.From
import foo.eip.model.Route
import foo.eip.graph.model.CamelTypeSemantics
import foo.eip.model.UnknownExpression
import foo.eip.model.To

/**
 * Concrete implementation of the Converter trait which will convert a Dom
 * structure into an abstract representation
 */
class DomConverter extends Converter[List[DomElement]] {
  type DomExpression = foo.dom.Model.Expression

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
    val processors = children.map(convert)
    Route(processors)
  }

  /**
   * Provides a mapping of a domElements into an abstract model that can be used instead
   * @param domElement The DomElement within the original DOM
   * @return A new abstract model.
   *         If there is no mapping available from the domElement to an abstract model
   *         this class will not error, and will instead try to convert the element into
   *         a To processor
   */
  def convert(domElement: DomElement): Processor = domElement match {
    // Handle the <from uri="..." /> DomElement
    case from: FromProcessorDefinition =>
      From(from.getUri.getStringValue)

    // Handle the <to uri="..."/> DomElement
    case to: ToProcessorDefinition =>
      To(to.getUri.getStringValue)

    // Handle the <setHeader headerName="..."><expression>...</expression></setHeader> element
    case setHeader: SetHeaderProcessorDefinition =>
      val headerName = setHeader.getHeaderName.getStringValue
      val expression = convertExpression(setHeader.getExpression)

      SetHeader(headerName, expression)

    case choice: ChoiceProcessorDefinition =>
     val domChildren = choice.getWhenClauses.asScala
     val abstractChildren = domChildren.map(convertWhenClause).toList
     Choice(abstractChildren)

    // Ensure we fall through in case there is a node we do not understand
    case _ =>
      To("error:unexpected")
  }

  def convertWhenClause(whenDefinition: WhenDefinition): When = whenDefinition match {
    // Handle the When case - note we need to recursively apply the function
    // to our children also
    case when: WhenDefinition =>
      val expression = convertExpression(when.getExpression)
      val children = when.getComponents.asScala.map(convert).toList

      When(expression, children)

    // case otherwise:
  }


  def convertExpression(expression: DomExpression): Expression = expression match {
    case constant: ConstantExpression => CamelTypeSemantics(Set(CommonClassNames.JAVA_LANG_STRING), Map())
      Constant(constant.getValue)
    case simple: SimpleExpression =>
      Simple(simple.getValue, Option(simple.getResultType.getStringValue))
    // By default we should supply no known type information for unknown type expressions
    case _ =>
      UnknownExpression()
  }

}
