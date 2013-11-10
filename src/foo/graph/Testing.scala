package foo.graph

import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import foo.Model.Component
import edu.uci.ics.jung.graph.util.EdgeType
import javax.swing.{WindowConstants, JFrame}
import edu.uci.ics.jung.visualization.VisualizationViewer
import edu.uci.ics.jung.algorithms.layout.TreeLayout


object Testing {

  case class Edge[V, E](edge: E, source: V, target: V) {
    override def toString: String = source + "--(" + edge + ")-->" + target
  }

  /**
   * Represents a Graph data structure
   * @tparam V
   * @tparam E
   */
  trait Graph[V, E] {
    def addVertex(vertex: V): Graph[V, E]
    def addEdge(vertex: E, vertex1: V, vertex2: V): Graph[V, E]
    def print: String
    def vertices: List[V]
    def edges: List[Edge[V, E]]
  }

  /**
   * An implementation of Graph, with certain limitations.
   * This implementation assumes that the graph will be a Directed Acyclic Graph,
   * that is, no circular links exist within the Graph
   *
   * @param vertices
   * @param edges
   * @tparam V The vertex generic type
   * @tparam E The edge generic type
   */
  case class DAG[V, E](vertices: List[V], edges: List[Edge[V, E]]) extends Graph[V, E] {
    def this(vertex: V) = this(List(vertex), List[Edge[V, E]]())

    def addVertex(vertex: V): Graph[V, E] =
      DAG(vertex :: vertices, edges)

    def addEdge(edge: E, vertex1: V, vertex2: V): Graph[V, E] =
      DAG(vertices, Edge(edge, vertex1, vertex2) :: edges)

    def print =
      s"""
      |({${vertices.mkString(", ")}},
      |{${edges.mkString(", ")}})""".stripMargin
  }

  case class EmptyDAG[V, E]() extends Graph[V, E] {
    def addVertex(vertex: V): Graph[V, E] =
      DAG[V, E](List(vertex), List[Edge[V, E]]())

    def addEdge(edge: E, vertex1: V, vertex2: V): Graph[V, E] =
      ???

    def vertices: List[V] = Nil
    def edges: List[Edge[V, E]] = Nil

    def print = "({}, {})"
  }

  def main(args: Array[String]) {

    val one = new EipComponent("from")
    val two = new EipComponent("to")
    val three = new EipComponent("hi");

    val dag = EmptyDAG[EipComponent, String]()
      .addVertex(one)
      .addVertex(two)
      .addVertex(three)
      .addEdge("a", one, two)
      .addEdge("b", one, three)

    val newGraph = asJungGraph[EipComponent, String](dag)
    val component = asVisualGraph(newGraph)

    val jframe = new JFrame()
    jframe.setSize(500, 700)
    jframe.getContentPane.add(component)
    jframe.setVisible(true)
    jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

  }

  type MutableGraph[V, E] = edu.uci.ics.jung.graph.DirectedSparseMultigraph[V, E]
  def asJungGraph[V, E](oldGraph: Graph[V, E]): MutableGraph[V, E] = {
    val newGraph:MutableGraph[V, E] = new DirectedSparseMultigraph[V, E]

    oldGraph.vertices.foreach(newGraph.addVertex)
    oldGraph.edges.foreach(e => newGraph.addEdge(e.edge, e.source, e.target, EdgeType.DIRECTED))

    newGraph
  }

  def asVisualGraph[V, E](graph: MutableGraph[V, E]): VisualizationViewer[V, E] = {
    val minimumSpanningForest = GraphGlue.newMinimumSpanningForest(graph)
    val viewer = new VisualizationViewer(new TreeLayout(minimumSpanningForest.getForest, 100, 100))
    viewer
  }


}

case class EipComponent(eip: String)
