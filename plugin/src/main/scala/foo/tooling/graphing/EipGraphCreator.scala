package foo.tooling.graphing

import StaticGraphTypes.EipDAG
import foo.dom.Model._
import foo.tooling.graphing.ADT.EmptyDAG
import foo.intermediaterepresentation.model.expressions.Expression
import foo.intermediaterepresentation.model.processors._
import foo.intermediaterepresentation.model.processors.Route
import scala.language.implicitConversions

/**
 * EipGraphCreator Object which contains EipDag manipulation methods.
 */
object EipGraphCreator {
  import scala.language.implicitConversions
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


/**
 * EIP Graph Creator class.
 * This class is associated with converting the Intermediate representation into a
 * DAG of EipProcessors, which can then be used later by a graphing API.
 */
class EipGraphCreator {
  import EipGraphCreator._

  /**
   * Converts the given intermediate representation of camel into a the 'visual' representation.
   *
   * @param routeWithSemantics The intermediate representation used for Camel
   * @return The converted EipDAG, which contains the appropriate edges between nodes so that they
   *         can be visually iinterpretedas an EIP diagram.
   */
  def createEipGraph(routeWithSemantics: Route): EipDAG = {
      val createdDAG: EipDAG = EmptyDAG[EipProcessor, String]()
      createEipGraph(
        List(),
        routeWithSemantics.children,
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
   * @param previous The previously visited Processors.
   *                 This should be None when called with the root node
   * @param processors The remaining list of processors to consume.
   * @param graph The currently created EipDag
   * @return A new EipDag, note the original data structure will not be mutated
   */
  private def createEipGraph(previous: List[EipProcessor],
                     processors: List[Processor],
                     graph: EipDAG): EipDAG = {
    // When there are no processors, we have completed our effort.
    if(processors == Nil) graph
    else {
      // Split the head processor + tail
      val (head, tail) = (processors.head, processors.tail)

      /**
       * Match on the head processor. The scala compiler should warn us if we have forgotten
       * to match all of the Processor trait implementations
       */
      head match {
        /**
         * Provides a conversion for the From IR Model
         */
        case processor@From(uri, _, _) =>
          val eipProcessor = EipProcessor(uri.getOrElse(DefaultAttributes.uri), processor)
          createEipGraph(List(eipProcessor), tail, linkGraph(previous, eipProcessor, graph))

        /**
         * Provides a conversion for the SetHeader IR Model
         */
        case processor@SetHeader(headerNameOption, Expression(expressionValue), _, _) =>
          val headerText = headerNameOption.map(_ + " -> " + expressionValue).getOrElse(DefaultAttributes.EmptyHeaderName)
          val eipProcessor = EipProcessor(headerText, processor)

          createEipGraph(List(eipProcessor), tail, linkGraph(previous, eipProcessor, graph))

        /**
         * Match a removeHeader element
         */
        case processor@RemoveHeader(headerNameOption, _, _) =>
          val headerName = headerNameOption.getOrElse(DefaultAttributes.EmptyHeaderName)
          val eipProcessor = EipProcessor(headerName, processor)
          createEipGraph(List(eipProcessor), tail, linkGraph(previous, eipProcessor, graph))

        /**
         * Provides a conversion for the Log IR Model
         */
        case processor@Log(text, _, _) =>
          val eipProcessor = EipProcessor(text.getOrElse(DefaultAttributes.NotValid), processor)
          createEipGraph(List(eipProcessor), tail, linkGraph(previous, eipProcessor, graph))

        /**
         * Provides a conversion for the SetBody IR Model
         */
        case processor@SetBody(Expression(expressionText), _, _) =>
          val eipProcessor = EipProcessor(expressionText, processor)
          createEipGraph(List(eipProcessor), tail, linkGraph(previous, eipProcessor, graph))

        /**
         * Provides a conversion for the To IR Model
         */
        case processor@To(uri, _, _) =>
          val eipProcessor = EipProcessor(uri.getOrElse(DefaultAttributes.uri), processor)
          createEipGraph(List(eipProcessor), tail, linkGraph(previous, eipProcessor, graph))

        /**
         * Provides a conversion for the Bean IR Model
         */
        case processor@Bean(beanReferenceOption, beanMethodOption, _, _) =>
          val beanTextOption = for {
            beanReference <- beanReferenceOption
            beanMethod <- beanMethodOption
          } yield s"${beanReference.getStringValue}.${beanMethod.getStringValue}()"

          val beanText = beanTextOption.getOrElse(DefaultAttributes.NotValid)
          val eipProcessor = EipProcessor(beanText, processor)
          createEipGraph(List(eipProcessor), tail, linkGraph(previous, eipProcessor, graph))

        /**
         * Provides a conversion for the Choice IR Model
         */
        case choice@Choice(_, otherwise, _, _) =>
          val choiceEipProcessor = EipProcessor("", choice)
          val linkedGraph = linkGraph(previous, choiceEipProcessor, graph)

          val (completedWhenGraph, previousDefinitions) = choice.getChildren.foldLeft((linkedGraph, List[EipProcessor]()))({
            /**
             * Match When elements
             */
            case ((eipGraph, lastProcessorDefinition), when@When(Expression(expressionText), children, _, _)) =>
              // Create the initial expression element from the when expression
              val whenEipProcessor = EipProcessor(expressionText, when)
              val linkedGraph = linkGraph(List(choiceEipProcessor), whenEipProcessor, eipGraph)

              // Apply the graph function recursively to produce all children nodes within the when expression
              val whenSubGraph = createEipGraph(List(whenEipProcessor), children, linkedGraph)

              val leafNodes =  getLeafNodes(whenSubGraph, whenEipProcessor)
              (whenSubGraph, leafNodes ::: lastProcessorDefinition)

            /**
             * Match otherwise element
             */
            case ((eipGraph, lastProcessorDefinition), otherwise@Otherwise(children, _, _)) =>
              val otherwiseEipProcessor = EipProcessor("", otherwise)
              val linkedGraph = eipGraph.linkComponents(List(choiceEipProcessor), otherwiseEipProcessor)

              // If we have no children, then the graph is complete, but we must be linked to the next processor
              if(children.isEmpty) (linkedGraph, otherwiseEipProcessor :: lastProcessorDefinition)
              else {
                // Produce the subgraph from the processor's children
                val otherwiseSubgraph = createEipGraph(List(otherwiseEipProcessor), children, linkedGraph)
                val lastChild = otherwiseSubgraph.vertices.find(_.processor == children.last).get

                // Only the last child in the pipeline is required to be linked to the next processor
                (otherwiseSubgraph, lastChild :: lastProcessorDefinition)
              }
          })

          // Additionally link the choice node to the next processor if there was no otherwise element
          if(otherwise.isEmpty) createEipGraph(choiceEipProcessor :: previousDefinitions, tail, completedWhenGraph)
          else createEipGraph(previousDefinitions, tail, completedWhenGraph)


        // TODO Only required because of matching bug in Scala ?
        // Fall through case, hitting a node we don't understand
        // We simply interpret it as a to component, so that we can still display
        // the information without crashing or such
        case unmatched =>
          val component = EipProcessor("Not Handled", unmatched)
          createEipGraph(List(component), tail, linkGraph(previous, component, graph))
      }
    }
  }

  /**
   * get all leaf nodes which are contained within the subgraph, ie all descendants of the parent when node
   * @param graph The current graph
   * @param parent The parent node
   * @return A list of list
   */
    def getLeafNodes(graph: EipDAG, parent: EipProcessor): List[EipProcessor] = {
      // Tail recursive implementation of this algorithm
      def getLeafNodesTailRec(graph: EipDAG, parent:EipProcessor, leafNodes: List[EipProcessor]): List[EipProcessor] = {
        val children = graph.edges.toList.filter(_.source == parent)
        if(children.isEmpty) parent :: leafNodes
        else children.flatMap(child => getLeafNodesTailRec(graph, child.target, leafNodes))
      }
      getLeafNodesTailRec(graph, parent, List[EipProcessor]())
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

