package foo.eip.graph.ADT

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

/**
 * Represents an Empty DAG.
 *
 * @tparam V The vertex generic type
 * @tparam E The edge generic type
 */
case class EmptyDAG[V, E]() extends Graph[V, E] {
  def addVertex(vertex: V): Graph[V, E] =
    DAG[V, E](List(vertex), List[Edge[V, E]]())

  def addEdge(edge: E, vertex1: V, vertex2: V): Graph[V, E] =
    DAG[V, E](List(vertex1, vertex2), List(Edge(edge, vertex1, vertex2)))

  def vertices: List[V] = Nil
  def edges: List[Edge[V, E]] = Nil

  def print = "({}, {})"
}
