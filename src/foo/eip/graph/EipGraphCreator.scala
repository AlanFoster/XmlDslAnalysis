package foo.eip.graph

import foo.Model._
import foo.eip.graph.ADT.Graph
import foo.eip.graph.StaticGraphTypes.EipDAG
import scala.collection.JavaConverters._
import foo.eip.graph.ADT.EmptyDAG
import java.util.UUID

/**
 * EIP Graph Creator class.
 * This class will convert the given intellij DOM class into a more abstract
 * DAG (Directed Acyclic Graph) which contains more semantic information
 * relevent to EIPs than the lower level abstraction of DOM.
 *
 * This decision will allow for other DSLs such as Java to be supported.
 *
 * Note an EipGraph contains /no/ information in relation to the creation of a visual
 * graph, and is purely used as a data structures which contains the relevant type information
 * etc
 */
class EipGraphCreator {

  /**
   * Converts the given blueprint DOM file into an EipDag
   * @param root The root DOM element
   * @return The converted EipDAG
   */
  def createEipGraph(root:Blueprint): EipDAG = {
    // Extract the given camel routes and create an empty EipDAG
    val routes = root.getCamelContext.getRoutes.asScala
    val createdDAG: EipDAG = EmptyDAG[EipComponent, String]()

    // There is no need to traverse the given routes if there are no processor definitions
    if (routes.isEmpty || !routes.head.getFrom.exists) createdDAG
    else createEipGraph(
      None,
      routes.head.getFrom :: routes.head.getComponents.asScala.toList,
      createdDAG
    )
  }

  /**
   * Each edge within the DAG must have a unique ID
   *
   * @param x The first node between the given edge
   * @param y The second node between the given edge
   * @return A unique ID for the given edge relation
   */
  def UniqueString(x: AnyRef, y: AnyRef) = UUID.randomUUID().toString

  /**
   * Creates the edges between the given nodes.
   * This implementation will handle the scenarios of no previous node existing
   * @param previous The previously visited node
   * @param next The newly visited node
   * @param graph The current EipGraph.
   * @param namingFunction The naming function, which when given two nodes will create a unique
   *                       ID associating the two nodes.
   * @return A newly created EipDag - Note this operation does not mutate the original
   *         data structure
   */
  def addEdge(previous: Option[EipComponent],
            next: EipComponent,
            graph: EipDAG)(namingFunction: (EipComponent, EipComponent) => String) = (previous, next) match {
    // Handle the scenario in which we are currently at the root node
    case (None, _) => graph
    // Handle the scenario in which we have visited a root node already
    case (Some(component1), component2) => graph.addEdge(namingFunction(component1, component2), component1, component2)
    case _ => graph
  }


  def linkGraph(previous: Option[EipComponent],
                  next: EipComponent,
                  graph: EipDAG) = {
    val newGraph = graph.addVertex(next)
    val linkedGraph = addEdge(previous, next, newGraph)(UniqueString)
    linkedGraph
  }


  /**
   * Creates a new EipDAG for the given list of processor definitions.
   * More concretely a list of processor definitions will initially
   * represent the given route, which will then be recursively visited.
   * A pointer to the previously recursed node will be used to successfully
   * create edges between the nodes in the DAG.
   *
   * @param previous The previously visited ProcessorDefinition.
   *                 This should be None when called with the root node
   * @param processors The remaining list of processor definitions.
   * @param graph The current EipDag
   * @return A new EipDag, note the original data structure will not be mutated
   */
  def createEipGraph(previous: Option[EipComponent],
                     processors: List[ProcessorDefinition],
                     graph: EipDAG): EipDAG = processors match {
    // When there are no processors, we have completed our effort.
    case Nil => graph

    case (from: FromProcessorDefinition) :: tail => {
      val next = EipComponent(from.getId.getStringValue, "from", from.getUri.getStringValue)
      createEipGraph(Some(next), tail, linkGraph(previous, next, graph))
    }

    case (wireTap: WireTapDefinition) :: tail => {
      val component = EipComponent(wireTap.getId.getStringValue, "awireTap", wireTap.getUri.getStringValue)
      createEipGraph(Some(component), tail, linkGraph(previous, component, graph))
    }

    case (setBody: SetBodyProcessorDefinition) :: tail => {
      val component = EipComponent(setBody.getId.getStringValue, "translator", "Expression ")
      createEipGraph(Some(component), tail, linkGraph(previous, component, graph))
    }

    case (to: ToProcessorDefinition) :: tail => {
      val component = EipComponent(to.getId.getStringValue, "to", to.getUri.getStringValue)
      createEipGraph(Some(component), tail, linkGraph(previous, component, graph))
    }

    case (bean: BeanDefinition) :: tail => {
      val component = EipComponent(bean.getId.getStringValue, "to", bean.getRef.getStringValue)
      createEipGraph(Some(component), tail, linkGraph(previous, component, graph))
    }

    case (choice: ChoiceProcessorDefinition) :: tail => {
      val choiceComponent = EipComponent(choice.getId.getStringValue, "choice", "choice")
      val newGraph = graph.addVertex(choiceComponent)
      val linkedGraph = addEdge(previous, choiceComponent, newGraph)(UniqueString)

      // TODO When node should have its own vertex, with a text box with its predicate
      val choiceGraph = choice.getWhens.asScala.foldLeft(linkedGraph)((graph, when) => {
        val component = EipComponent(when.getId.getStringValue, "when", "Expression ")
        val newGraph = graph.addVertex(component)
        val linkedGraph = addEdge(Some(choiceComponent), component, newGraph)(UniqueString)
        createEipGraph(Some(component), when.getComponents.asScala.toList, linkedGraph)
      })

      // TODO need to link all generated nodes to graph, IE Some(choiceComponent) isn't valid, it's Some(List[Choices]) possibly
      createEipGraph(Some(choiceComponent), tail, linkGraph(previous, choiceComponent, choiceGraph))
    }

    // Fall through case, hitting a node we don't understand
    // We simply interpret it as a to component, so that we can still display
    // the information without crashing or such
    case _ :: tail => {
      val component = EipComponent("", "to", "Error")
      createEipGraph(Some(component), tail, linkGraph(previous, component, graph))
    }
  }
}


