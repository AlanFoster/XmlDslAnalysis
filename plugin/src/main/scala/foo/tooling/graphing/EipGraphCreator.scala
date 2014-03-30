package foo.tooling.graphing

import StaticGraphTypes.EipDAG
import scala.collection.JavaConverters._
import foo.dom.Model._
import foo.tooling.graphing.ADT.EmptyDAG
import foo.intermediaterepresentation.converter.AbstractModelConverter
import foo.intermediaterepresentation.typeInference.AbstractModelTypeInference
import foo.intermediaterepresentation.model.expressions.Expression
import foo.intermediaterepresentation.model.processors._


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
   * @param modelConverter An abstract implementation of a model converter
   * @param dataFlowInference An abstract implementation of data flow inference algorithm
   * @param root The root DOM element
   * @return The converted EipDAG
   */
  def createEipGraph
      (modelConverter: AbstractModelConverter[Blueprint], dataFlowInference: AbstractModelTypeInference)
      (root:Blueprint): EipDAG = {
    // Extract the given camel routes and create an empty EipDAG
    val routes = root.getCamelContext.getRoutes.asScala
    val createdDAG: EipDAG = EmptyDAG[EipProcessor, String]()

    // There is no need to traverse the given routes if there are no processor definitions
    if (routes.isEmpty || !routes.head.getFrom.exists) createdDAG
    else {
      val route = modelConverter.convert(root)
      val routeWithSemantics = dataFlowInference.performTypeInference(route)

      createEipGraph(
        List(),
        routeWithSemantics.children,
        createdDAG
      )
    }
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
  def createEipGraph(previous: List[EipProcessor],
                     processors: List[Processor],
                     graph: EipDAG): EipDAG = processors match {
    // When there are no processors, we have completed our effort.
    case Nil => graph

    case (processor@From(uri, _, _)) :: tail =>
      val eipProcessor = EipProcessor(uri, processor)
      createEipGraph(List(eipProcessor), tail, linkGraph(previous, eipProcessor, graph))

    case (processor@SetHeader(headerName, Expression(expressionValue), _, _)) :: tail =>
      val eipProcessor = EipProcessor(headerName + " -> " + expressionValue, processor)
      createEipGraph(List(eipProcessor), tail, linkGraph(previous, eipProcessor, graph))

    case (processor@SetBody(Expression(expressionText), _, _)) :: tail =>
      val eipProcessor = EipProcessor(expressionText, processor)
      createEipGraph(List(eipProcessor), tail, linkGraph(previous, eipProcessor, graph))

    case (processor@To(uri, _, _)) :: tail =>
      val eipProcessor = EipProcessor(uri, processor)
      createEipGraph(List(eipProcessor), tail, linkGraph(previous, eipProcessor, graph))

    case (processor@Bean(beanReference, _, _, _)) :: tail =>
      val beanText = beanReference.map(_.getStringValue).getOrElse("Not Specified")
      val eipProcessor = EipProcessor(beanText, processor)
      createEipGraph(List(eipProcessor), tail, linkGraph(previous, eipProcessor, graph))

    case (choice@Choice(whens, _, _)) :: tail =>
      val choiceEipProcessor = EipProcessor("choice", choice)
      val linkedGraph = linkGraph(previous, choiceEipProcessor, graph)

      // TODO When node should have its own vertex, with a text box with its predicate
      val (completedWhenGraph, previousDefinitions) = whens.foldLeft((linkedGraph, List[EipProcessor]()))({
        case ((eipGraph, lastProcessorDefinition), when@When(Expression(expressionText), children, _, _)) => {
          // Create the initial expression element from the when expression
          val whenEipProcessor = EipProcessor(expressionText, when)
          val linkedGraph = linkGraph(List(choiceEipProcessor), whenEipProcessor, eipGraph)

          // Apply the graph function recursively to produce all children nodes within the when expression
          val whenSubGraph = createEipGraph(List(whenEipProcessor), children, linkedGraph)

          // get all leaf nodes which are contained within the subgraph
          // ie all descendants of the parent when node
          val leafNodes = {
            def getLeafNodes(parent: EipProcessor, graph: EipDAG, leafNodes: List[EipProcessor]): List[EipProcessor] = {
              val children = graph.edges.toList.filter(_.source == parent)
              if(children.isEmpty) parent :: leafNodes
              else  children.flatMap(child => getLeafNodes(child.target, graph, leafNodes))
            }
            getLeafNodes(whenEipProcessor, whenSubGraph, List())
          }

          (whenSubGraph, leafNodes ::: lastProcessorDefinition)
        }
      })

      // TODO Should only link the choice node to the next node, IF, and only if, there is no otherwise statement
      // TODO need to link all generated nodes to graph, IE Some(choiceComponent) isn't valid, it's Some(List[Choices]) possibly
      createEipGraph(choiceEipProcessor :: previousDefinitions, tail, completedWhenGraph)

    // Fall through case, hitting a node we don't understand
    // We simply interpret it as a to component, so that we can still display
    // the information without crashing or such
    case unmatched :: tail =>
      val component = EipProcessor("Not Handlded", unmatched)
      createEipGraph(List(component), tail, linkGraph(previous, component, graph))
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
    def linkComponents(previousList: List[EipProcessor],
                      next: EipProcessor): EipDAG =
      linkGraph(previousList, next, eipDag)

    /**
     * Adds the given EipComponent, and link the new EipComponent to all previously occurring nodes.
     * This will be a list of nodes, for instance in the scenario of after a choice statement
     *
     * @param previous the previous element within the graph to link to this node
     * @param next The EipComponent to link to
     * @return A new graph with the given composed edges added
     */
    def linkComponents(previous: EipProcessor,
                       next: EipProcessor): EipDAG =
      linkGraph(List(previous), next, eipDag)
  }

  /**
   * Each edge within the DAG must have a unique ID
   *
   * @param x The first node between the given edge
   * @param y The second node between the given edge
   * @return A unique ID for the given edge relation
   */
  def UniqueString(graph: EipDAG)(x: EipProcessor, y: EipProcessor) = {
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
  def addEdge(previous: EipProcessor,
              next: EipProcessor,
              graph: EipDAG)(namingFunction: EipDAG => (EipProcessor, EipProcessor) => String) =
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
  def linkGraph(previousList: List[EipProcessor],
                next: EipProcessor,
                graph: EipDAG): EipDAG = {
    val newGraph = graph.addVertex(next)
    previousList.foldLeft(newGraph)((graphAccumulator, previous) => {
      val linkedGraph = addEdge(previous, next, graphAccumulator)(UniqueString)
      linkedGraph
    })
  }
}