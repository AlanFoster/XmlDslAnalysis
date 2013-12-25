package foo.eip.graph

import foo.Model._

import foo.eip.graph.StaticGraphTypes.EipDAG
import scala.collection.JavaConverters._
import foo.eip.graph.ADT.EmptyDAG

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
      List(),
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
  def UniqueString(x: EipComponent, y: EipComponent) = x.id + "_" + y.id

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
  def addEdge(previous: EipComponent,
            next: EipComponent,
            graph: EipDAG)(namingFunction: (EipComponent, EipComponent) => String) =
    graph.addEdge(namingFunction(previous, next), previous, next)


  /**
   * Adds the given EipComponent, and link the new EipComponent to all previously occurring nodes.
   * This will be a list of nodes, for instance in the scenario of after a choice statement
   *
   * @param previousList the previous elements within the graph to link to this node
   * @param next The EipComponent to link to
   * @param graph The currently composed graph
   * @return A new graph with the given composed edges added
   */
  def linkGraph(previousList: List[EipComponent],
                  next: EipComponent,
                  graph: EipDAG): EipDAG = {
    val newGraph = graph.addVertex(next)
    previousList.foldLeft(newGraph)((graphAccumulator, previous) => {
      val linkedGraph = addEdge(previous, next, graphAccumulator)(UniqueString)
      linkedGraph
    })
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
  def createEipGraph(previous: List[EipComponent],
                     processors: List[ProcessorDefinition],
                     graph: EipDAG): EipDAG = processors match {
    // When there are no processors, we have completed our effort.
    case Nil => graph

    case (from: FromProcessorDefinition) :: tail => {
      val component = EipComponent(createId(from), "from", from.getUri.getStringValue, from)
      createEipGraph(List(component), tail, linkGraph(previous, component, graph))
    }

    case (wireTap: WireTapDefinition) :: tail => {
      val component = EipComponent(createId(wireTap), "awireTap", wireTap.getUri.getStringValue, wireTap)
      createEipGraph(List(component), tail, linkGraph(previous, component, graph))
    }

    case (setBody: SetBodyProcessorDefinition) :: tail => {
      val component = EipComponent(createId(setBody), "translator", "Expression ", setBody)
      createEipGraph(List(component), tail, linkGraph(previous, component, graph))
    }

    case (to: ToProcessorDefinition) :: tail => {
      val component = EipComponent(createId(to), "to", to.getUri.getStringValue, to)
      createEipGraph(List(component), tail, linkGraph(previous, component, graph))
    }

    case (bean: BeanDefinition) :: tail => {
      val component = EipComponent(createId(bean), "to", bean.getRef.getStringValue, bean)
      createEipGraph(List(component), tail, linkGraph(previous, component, graph))
    }

    case (choice: ChoiceProcessorDefinition) :: tail => {
      val choiceComponent = EipComponent(createId(choice), "choice", "choice", choice)
      val linkedGraph = linkGraph(previous, choiceComponent, graph)

      // TODO When node should have its own vertex, with a text box with its predicate
      val (completedWhenGraph, previousDefinitions) = choice.getWhens.asScala.foldLeft((linkedGraph, List[EipComponent]()))({
        case ((eipGraph, lastProcessorDefinition), when) => {
          // Create the initial expression element from the when expression
          val component = EipComponent(createId(when), "when", "Expression ", when)
          val whenGraph = eipGraph.addVertex(component)

          // Apply the graph function recursively to produce all children nodes within the when expression
          val linkedGraph = addEdge(choiceComponent, component, whenGraph)(UniqueString)
          val whenSubGraph = createEipGraph(List(component), when.getComponents.asScala.toList, linkedGraph)

          (whenSubGraph, whenSubGraph.vertices.head :: lastProcessorDefinition)
        }
      })

      // TODO need to link all generated nodes to graph, IE Some(choiceComponent) isn't valid, it's Some(List[Choices]) possibly
      createEipGraph(previousDefinitions, tail, completedWhenGraph)
    }

    // Fall through case, hitting a node we don't understand
    // We simply interpret it as a to component, so that we can still display
    // the information without crashing or such
    case unmatched :: tail => {
      val component = EipComponent("", "to", "Error", unmatched)
      createEipGraph(List(component), tail, linkGraph(previous, component, graph))
    }
  }

  /**
   * Attempts to extract the ID from the given processor definition.
   * @param processorDefinition The dom processor definition element
   * @return The ID attribute if present, otherwise defaults to the hash code of the element
   */
  def createId(processorDefinition: ProcessorDefinition): String = {
    val idAttribute = processorDefinition.getId

    if(idAttribute.exists()) idAttribute.getStringValue
    else idAttribute.hashCode.toString
  }
}


