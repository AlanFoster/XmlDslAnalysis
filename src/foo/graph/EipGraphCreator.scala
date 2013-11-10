package foo.graph

import foo.Model._
import foo.graph.ADT.Graph
import foo.graph.StaticGraphTypes.EipDAG
import scala.collection.JavaConverters._
import foo.graph.ADT.EmptyDAG

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
      routes.head,
      routes.head.getComponents.asScala.toList,
      createdDAG
    )
  }

  def createEipGraph(parent: Any, children: List[ProcessorDefinition], graph: EipDAG): EipDAG = {
    graph
      .addVertex(EipComponent("1", "from"))
      .addVertex(EipComponent("1","to"))
      .addVertex(EipComponent("1","to"))
      .addVertex(EipComponent("1","to"))
  }

/*  def getEipType(c:ProcessorDefinition) = c match {
    case from: FromProcessorDefinition => "from"
    case to: ToProcessorDefinition => "to"
    case inOut: InOutProcessorDefinition => "to"
    case setBody: SetBodyProcessorDefinition => "translator"
    case _ => "to"
  }
  */
  
  /*

  def createVertex(parent: Component, children: List[Component], graph: EipDAG) {
    graph.addVertex(parent)
    for { child <- children } {
      createVertex(child, List(), graph)
      pipeLineChildren(children, graph)(_.toString)
    }
    (parent, children) match {
      case (from, x :: _) => {
        graph.addEdge(from.toString, from, x)
      }
      case _ => ()
    }
  }
*/


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
  def pipeLineChildren[V, E](children: List[V], graph: Graph[V, E])(f: V => E): Boolean = children match {
    case Nil => true
    case x :: Nil => true
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


