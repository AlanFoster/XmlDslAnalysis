package foo.eip.graph.ADT

/**
 * Represents a Graph data structure.
 * This specific Graph trait does not have any constraints regarding
 * direction. However, it is likely that this trait will be used to
 * represent a purely Directed Acyclic Graph - ie a graph with no
 * edges which will link back to the same edges.
 *
 * Note a Graph contains /no/ information in relation to the creation of a visual
 * graph, and is purely used as a data structures which contains Vertices + Edges
 *
 * @tparam V The vertex generic type
 * @tparam E The edge generic type
 */
trait Graph[V, E] {
  /**
   * Adds an vertex to the given Graph
   * @param vertex The vertex
   * @return a new immutable Graph[V, E] with the given vertex
   */
  def addVertex(vertex: V): Graph[V, E]

  /**
   * Adds an edge between the two given vertices. It is assumed that these
   * vertices exist within the Graph already
   * @param edge The edge information of type E
   * @param vertex1 The first vertex
   * @param vertex2 The second vertex
   * @return a new immutable Graph[V, E] with the given edge
   */
  def addEdge(edge: E, vertex1: V, vertex2: V): Graph[V, E]

  /**
   * Generated a detail String representation of the given Graph
   * @return A string representation of the given graph
   */
  def print: String

  /**
   * Provides access to the given list of vertices
   * @return The list of vertices
   */
  def vertices: List[V]

  /**
   * Provides access to the given list of edges
   * @return The list of edges
   */
  def edges: List[Edge[V, E]]
}