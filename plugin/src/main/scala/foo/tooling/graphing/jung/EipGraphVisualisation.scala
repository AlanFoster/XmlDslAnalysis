package foo.tooling.graphing.jung

import edu.uci.ics.jung.visualization.VisualizationViewer
import foo.tooling.graphing.StaticGraphTypes
import StaticGraphTypes.EipDAG
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import edu.uci.ics.jung.graph.util.EdgeType
import edu.uci.ics.jung.algorithms.layout.{StaticLayout, TreeLayout}
import foo.tooling.graphing.ADT.Graph
import java.awt.Dimension
import foo.dom.Model.ProcessorDefinition

/**
 * Provides conversion methods for converting generic Graph[V,E] implementations
 * directly into a JUNG Directed Sparse Multigraph implementation
 */
class EipGraphVisualisation(eipGraph: EipDAG) {

  /**
   * Create a type alias for VisualizationViewer to improve code readability
   */
  type Viewer = VisualizationViewer[ProcessorDefinition, String]

  /**
  * Boiler plate
  */
  type MutableGraph[V, E] = edu.uci.ics.jung.graph.DirectedSparseMultigraph[V, E]

  /**
   * Converts the given graph
   * @param oldGraph The immutable graph structure to convert
   * @tparam V The generic type of the vertices
   * @tparam E The generic type of the Edges
   * @return A new, mutable, JUNG Directed Sparse Multigraph implementation
   */
  def asJungGraph[V, E](oldGraph: Graph[V, E]): MutableGraph[V, E] = {
    val newGraph:MutableGraph[V, E] = new DirectedSparseMultigraph[V, E]

    oldGraph.vertices.foreach(newGraph.addVertex)
    oldGraph.edges.foreach(e => newGraph.addEdge(e.edge, e.source, e.target, EdgeType.DIRECTED))

    newGraph
  }

  /**
   * Converts the given MutableGraph into a VisualViewer, which can then be
   * embedded within a Swing component
   * @param graph The Mutable graph.
   * @tparam V The generic type of the vertices
   * @tparam E The generic type of the Edges
   * @return A new VisualViewer which represents the given Graph, with a minimum vertex distance of 100.
   *         Note this implementation does not provide any 'additional' features.
   *         It purely converts the MutableGraph[V, E] into a VisualizationViewer[V, E]
   */
  def asVisualGraph[V, E](graph: MutableGraph[V, E]): VisualizationViewer[V, E] = {
    val minimumSpanningForest = GraphGlue.newMinimumSpanningForest(graph)
    val treeLayout = new TreeLayout(minimumSpanningForest.getForest, 100, 100)
    val staticLayout = new StaticLayout(graph, treeLayout)
    val viewer = new VisualizationViewer(staticLayout, new Dimension(500, 500))
    viewer
  }
}
