package foo.eip.graph

import foo.Model._

import foo.eip.graph.StaticGraphTypes.EipDAG
import scala.collection.JavaConverters._
import foo.eip.graph.ADT.EmptyDAG

/**
 * EIP Graph Creator class.
 * This class will convert the given intellij DOM class into a more abstract
 * DAG (Directed Acyclic Graph) which contains more semantic information
 * relevant to EIPs than the lower level abstraction of DOM.
 *
 * This decision will allow for other DSLs such as Java to be supported.
 *
 * Note an EipGraph contains /no/ information in relation to the creation of a visual
 * graph, and is purely used as a data structures which contains the relevant type information
 * etc
 */
class EipGraphCreator {
  import EipGraphCreator._

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
      val component = EipComponent(createId(from), "from", from.getUri.getStringValue, CamelType("java.lang.Object"), from)
      createEipGraph(List(component), tail, linkGraph(previous, component, graph))
    }

    case (wireTap: WireTapDefinition) :: tail => {
      val component = EipComponent(createId(wireTap), "wireTap", wireTap.getUri.getStringValue, unionTypes(previous), wireTap)
      createEipGraph(List(component), tail, linkGraph(previous, component, graph))
    }

    case (removeHeader: RemoveHeaderProcessorDefinition) :: tail => {
      // TODO Remove a header from the semantic graph... somehow
      val typeInformation = unionTypes(previous, CamelType(Set(), Map()))
      val component = EipComponent(createId(removeHeader), "translator", removeHeader.getHeaderName.getStringValue, typeInformation, removeHeader)
      createEipGraph(List(component), tail, linkGraph(previous, component, graph))
    }

    case (setHeader: SetHeaderProcessorDefinition) :: tail  => {
      // Add a new header definition to the semantic model of the graph
      val headerName = setHeader.getHeaderName.getStringValue
      val typeInformation = unionTypes(previous, CamelType(Set(), Map(headerName -> setHeader)))
      val component = EipComponent(createId(setHeader), "translator", setHeader.getExpression.getValue, typeInformation, setHeader)
      createEipGraph(List(component), tail, linkGraph(previous, component, graph))
    }

    case (setBody: SetBodyProcessorDefinition) :: tail => {
      val component = EipComponent(createId(setBody), "translator", setBody.getExpression.getValue, unionTypes(previous), setBody)
      createEipGraph(List(component), tail, linkGraph(previous, component, graph))
    }

    case (to: ToProcessorDefinition) :: tail => {
      val component = EipComponent(createId(to), "to", to.getUri.getStringValue, unionTypes(previous), to)
      createEipGraph(List(component), tail, linkGraph(previous, component, graph))
    }

    case (bean: BeanDefinition) :: tail => {
      val component = EipComponent(createId(bean), "to", bean.getRef.getStringValue, unionTypes(previous),  bean)
      createEipGraph(List(component), tail, linkGraph(previous, component, graph))
    }

    case (choice: ChoiceProcessorDefinition) :: tail => {
      val choiceComponent = EipComponent(createId(choice), "choice", "choice", unionTypes(previous), choice)
      val linkedGraph = linkGraph(previous, choiceComponent, graph)

      // TODO When node should have its own vertex, with a text box with its predicate
      val (completedWhenGraph, previousDefinitions) = choice.getWhens.asScala.foldLeft((linkedGraph, List[EipComponent]()))({
        case ((eipGraph, lastProcessorDefinition), when) => {
          // Create the initial expression element from the when expression
          val component = EipComponent(createId(when), "when", when.getExpression.getValue, unionTypes(previous), when)
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
      val component = EipComponent("", "to", "Error", unionTypes(previous), unmatched)
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


  def unionTypes(previous: List[EipComponent],
                 current: CamelType = CamelType()) = {
     previous
       .map(_.semantics)
       .foldLeft(current)((acc, next) => CamelType(acc.possibleBodyTypes ++ next.possibleBodyTypes, acc.headers ++ next.headers))
  }

}

/**
 * EipGraphCreator Object which contains EipDag manipulation methods.
 */
object EipGraphCreator {
  /**
   * Provides an implicit definition for conversion to a new 'richer' EipDag model
   * which contains various useful methods for manipulating an Eip Dag, more so
   * than a generic Graph
   * @param eipDag The EipDag reference
   * @return A rich EipDag
   */
  implicit def richEipGraph(eipDag: EipDAG) = new {
    /**
     * Adds the given EipComponent, and link the new EipComponent to all previously occurring nodes.
     * This will be a list of nodes, for instance in the scenario of after a choice statement
     *
     * @param previousList the previous elements within the graph to link to this node
     * @param next The EipComponent to link to
     * @return A new graph with the given composed edges added
     */
    def linkComponents(previousList: List[EipComponent],
                      next: EipComponent): EipDAG =
      linkGraph(previousList, next, eipDag)

    /**
     * Adds the given EipComponent, and link the new EipComponent to all previously occurring nodes.
     * This will be a list of nodes, for instance in the scenario of after a choice statement
     *
     * @param previous the previous element within the graph to link to this node
     * @param next The EipComponent to link to
     * @return A new graph with the given composed edges added
     */
    def linkComponents(previous: EipComponent,
                       next: EipComponent): EipDAG =
      linkGraph(List(previous), next, eipDag)
  }

  /**
   * Each edge within the DAG must have a unique ID
   *
   * @param x The first node between the given edge
   * @param y The second node between the given edge
   * @return A unique ID for the given edge relation
   */
  def UniqueString(graph: EipDAG)(x: EipComponent, y: EipComponent) = {
    val edgeSize = graph.edges.size
    x.id + "_" + y.id + edgeSize
  }

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
              graph: EipDAG)(namingFunction: EipDAG => (EipComponent, EipComponent) => String) =
    graph.addEdge(namingFunction(graph)(previous, next), previous, next)


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
}