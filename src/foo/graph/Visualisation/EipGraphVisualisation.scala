package foo.graph.Visualisation

import edu.uci.ics.jung.visualization.VisualizationViewer
import foo.Model.ProcessorDefinition
import foo.graph.{GraphGlue, EipComponent}
import foo.graph.ADT.Graph
import foo.graph.StaticGraphTypes.EipDAG
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import edu.uci.ics.jung.graph.util.EdgeType
import edu.uci.ics.jung.algorithms.layout.TreeLayout

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 10/11/13
 * Time: 16:25
 * To change this template use File | Settings | File Templates.
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
