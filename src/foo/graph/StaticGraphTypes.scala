package foo.graph

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 10/11/13
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
object StaticGraphTypes {
  /**
   * Create a new type alias for an Eip component graph, to
   * remove code smell from type signatures
   */
  type EipDAG = foo.graph.ADT.Graph[EipComponent, String]

}
