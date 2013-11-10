package foo.graph


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

    def print = "({}, {})"
  }

  def main(args: Array[String]) {
    val one = new EipComponent("from")
    val two = new EipComponent("to")


    println(EmptyDAG[EipComponent, String]()
      .addVertex(one)
      .addVertex(two)
      .addEdge("", one, two)
      .print)
  }
}

case class EipComponent(eip: String)
