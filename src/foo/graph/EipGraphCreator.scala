package foo.graph

import foo.Model._
import foo.graph.ADT.Graph
import foo.graph.StaticGraphTypes.EipDAG
import scala.collection.JavaConverters._
import foo.graph.ADT.EmptyDAG
import com.intellij.util.xml.DomElement
import foo.graph.ADT.EmptyDAG
import java.util.UUID

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 10/11/13
 * Time: 15:52
 * To change this template use File | Settings | File Templates.
 */
class EipGraphCreator {

  def createEipGraph(root:Blueprint): EipDAG = {
    val routes = root.getCamelContext.getRoutes.asScala
    val createdDAG: EipDAG = EmptyDAG[EipComponent, String]()

    if (routes.isEmpty) createdDAG
    else createEipGraph(
      None,
      routes.head.getFrom :: routes.head.getComponents.asScala.toList,
      createdDAG
    )
  }

  def UniqueString(x: AnyRef, y: AnyRef) = UUID.randomUUID().toString

  def link(previous: Option[EipComponent],
            next: EipComponent,
            graph: EipDAG)(f: (EipComponent, EipComponent) => String) = (previous, next) match {
    case (None, _) => graph
    case (Some(component1), component2) => graph.addEdge(f(component1, component2), component1, component2)
    case _ => graph
  }

  def createEipGraph(previous: Option[EipComponent],
                     processors: List[ProcessorDefinition],
                     graph: EipDAG): EipDAG = processors match {
    case Nil => graph

    case (from: FromProcessorDefinition) :: tail => {
      val component = EipComponent(from.getId.getStringValue, "from", from.getUri.getStringValue)
      val newGraph = graph.addVertex(component)
      val linkedGraph = link(previous, component, newGraph)(UniqueString)
      createEipGraph(Some(component), tail, linkedGraph)
    }

    case (wireTap: WireTapDefinition) :: tail => {
      val component = EipComponent(wireTap.getId.getStringValue, "awireTap", wireTap.getUri.getStringValue)
      val newGraph = graph.addVertex(component)
      val linkedGraph = link(previous, component, newGraph)(UniqueString)
      createEipGraph(Some(component), tail, linkedGraph)
    }

    case (setBody: SetBodyProcessorDefinition) :: tail => {
      val component = EipComponent(setBody.getId.getStringValue, "translator", setBody.getExpression.getValue)
      val newGraph = graph.addVertex(component)
      val linkedGraph = link(previous, component, newGraph)(UniqueString)
      createEipGraph(Some(component), tail, linkedGraph)
    }

    case (to: ToProcessorDefinition) :: tail => {
      val component = EipComponent(to.getId.getStringValue, "to", to.getUri.getStringValue)
      val newGraph = graph.addVertex(component)
      val linkedGraph = link(previous, component, newGraph)(UniqueString)
      createEipGraph(Some(component), tail, linkedGraph)
    }

    case (choice: ChoiceProcessorDefinition) :: tail => {
      val choiceComponent = EipComponent(choice.getId.getStringValue, "choice", "choice")
      val newGraph = graph.addVertex(choiceComponent)
      val linkedGraph = link(previous, choiceComponent, newGraph)(UniqueString)

      // TODO When node should have its own vertex, with a text box with its predicate

      choice.getWhens.asScala.foldLeft(linkedGraph)((graph, when) => {
        val component = EipComponent(when.getId.getStringValue, "when", when.getExpression.getValue)
        val newGraph = graph.addVertex(component)
        val linkedGraph = link(Some(choiceComponent), component, newGraph)(UniqueString)
        createEipGraph(Some(component), when.getComponents.asScala.toList, linkedGraph)
      })
    }

    // Fall through case, hitting a node we don't understand
    // Intepret it as a to component
    case _ :: tail => {
      val component = EipComponent("", "to", "Error")
      val newGraph = graph.addVertex(component)
      val linkedGraph = link(previous, component, newGraph)(UniqueString)
      createEipGraph(Some(component), tail, linkedGraph)
    }
  }

  /**
   * Tail recursive implementation of a pipeline children function.
   * IE, each child will have an edge to next child
   * @param children
   * @param graph
   * @param f
   * @tparam V
   * @tparam E
   * @return
   */
  def pipeLineChildren[V, E](children: List[V], graph: Graph[V, E])(f: V => E): Graph[V, E] = children match {
    case Nil => graph
    case x :: Nil => graph
    case x :: x2 :: xs => {
      val newGraph = graph.addEdge(f(x), x, x2)
      pipeLineChildren(children.tail, newGraph)(f)
    }
  }


  /*

      def multiCastChildren(parent: Pointer, children: List[Pointer]): Boolean = children match {
        case Nil => true
        case x :: xs => {
          graph.addEdge(x.hashCode().toString, parent.component, x.component, EdgeType.DIRECTED)
          multiCastChildren(parent, xs)
        }
      }
  */
}


