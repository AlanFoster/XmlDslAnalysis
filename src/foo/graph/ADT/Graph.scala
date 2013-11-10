package foo.graph.ADT

/**
 * Represents a Graph data structure.
 * This specific Graph trait does not have any constraints regarding
 * direction. However, it is likely that this trait will be used to
 * represent a purely Directed Acyclic Graph - ie a graph with no
 * edges which will link back to the same edges.
 *
 * @tparam V The vertex generic type
 * @tparam E The edge generic type
 */
trait Graph[V, E] {
  def addVertex(vertex: V): Graph[V, E]
  def addEdge(vertex: E, vertex1: V, vertex2: V): Graph[V, E]
  def print: String
  def vertices: List[V]
  def edges: List[Edge[V, E]]
}