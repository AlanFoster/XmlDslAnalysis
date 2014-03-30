package foo.tooling.graphing.ADT

/**
 * An implementation of Graph, with certain limitations.
 * This implementation assumes that the graph will be a Directed Acyclic Graph,
 * that is, no circular links exist within the Graph
 *
 * @param vertices The list of vertices
 * @param edges The list of edges
 * @tparam V The vertex generic type
 * @tparam E The edge generic type
 */
case class DAG[V, E](vertices: List[V], edges: List[Edge[V, E]]) extends Graph[V, E] {
  /**
   * Adds an vertex to the given Graph
   * @param vertex The vertex
   * @return a new immutable Graph[V, E] with the given vertex
   */
  def addVertex(vertex: V): Graph[V, E] =
    DAG(vertex :: vertices, edges)

  /**
   * Adds an edge between the two given vertices. It is assumed that these
   * vertices exist within the Graph already
   * @param edge The edge information of type E
   * @param vertex1 The first vertex
   * @param vertex2 The second vertex
   * @return a new immutable Graph[V, E] with the given edge
   */
  def addEdge(edge: E, vertex1: V, vertex2: V): Graph[V, E] =
    DAG(vertices, Edge(edge, vertex1, vertex2) :: edges)

  /**
   * Generated a detail String representation of the given Graph.
   *
   * @return A string representation of the given graph
   */
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
  /**
   * Adds an vertex to the given Graph
   * @param vertex The vertex
   * @return a new immutable Graph[V, E] with the given vertex
   */
  def addVertex(vertex: V): Graph[V, E] =
    DAG[V, E](List(vertex), List[Edge[V, E]]())

  /**
   * Adds an edge between the two given vertices. It is assumed that these
   * vertices exist within the Graph already
   * @param edge The edge information of type E
   * @param vertex1 The first vertex
   * @param vertex2 The second vertex
   * @return a new immutable Graph[V, E] with the given edge
   */
  def addEdge(edge: E, vertex1: V, vertex2: V): Graph[V, E] =
    DAG[V, E](List(vertex1, vertex2), List(Edge(edge, vertex1, vertex2)))

  /**
   * Provides access to the given list of vertices
   * @return The empty list of vertices
   */
  def vertices: List[V] = Nil

  /**
   * Provides access to the given list of edges
   * @return The empty list of edges
   */
  def edges: List[Edge[V, E]] = Nil

  /**
   * Generated a detail String representation of the given Graph.
   *
   * @return A string representation of the given graph
   */
  def print = "({}, {})"
}
