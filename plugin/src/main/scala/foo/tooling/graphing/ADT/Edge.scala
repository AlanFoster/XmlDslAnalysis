package foo.tooling.graphing.ADT

/**
 * Represents an edge within a graph.
 *
 * @param edge The edge value
 * @param source the first vertex within the edge
 * @param target the second vertex within the edge
 * @tparam V The vertex generic type
 * @tparam E The edge generic type
 */
case class Edge[V, E](edge: E, source: V, target: V) {
  override def toString: String = source + "--(" + edge + ")-->" + target
}