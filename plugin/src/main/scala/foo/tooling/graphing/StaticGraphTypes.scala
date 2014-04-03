package foo.tooling.graphing

/**
 * Helper class which provides type aliasing for an EipGraph.
 */
object StaticGraphTypes {
  /**
   * Create a new type alias for an Eip component graph, to
   * remove code smell from type signatures.
   *
   * Note an EipGraph contains /no/ information in relation to the creation of a visual
   * graph, and is purely used as a data structures which contains the relevant type information
   * etc
   */
  type EipDAG = foo.tooling.graphing.ADT.Graph[EipProcessor, String]
}
