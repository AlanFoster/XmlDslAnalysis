package foo.graphing.ultimate

import com.intellij.openapi.graph.builder.components.BasicGraphPresentationModel
import com.intellij.openapi.graph.base.Graph
import foo.eip.graph.EipProcessor
import foo.eip.graph.ADT.Edge
import com.intellij.openapi.graph.view.{EditMode, Graph2DView, NodeRealizer}
import com.intellij.openapi.graph.builder.util.GraphViewUtil
import com.intellij.openapi.graph.settings.GraphSettingsProvider
import com.intellij.openapi.project.{Project, ProjectManager}
import com.intellij.openapi.graph.GraphManager
import com.intellij.openapi.graph.layout.hierarchic.HierarchicLayouter
import com.intellij.openapi.graph.layout.{Layouter, OrientationLayouter}
import com.intellij.openapi.graph.builder.EdgeCreationPolicy

/**
 * The graph presentation model deals with edges/nodes drawing etc.
 * For instance being able to provide custom realizors for nodes.
 */
class CamelGraphPresentationModel(graph: Graph, project: Project)
  extends BasicGraphPresentationModel[EipProcessor, Edge[EipProcessor, String]](graph) {

  override def customizeSettings(view: Graph2DView, editMode: EditMode): Unit = {
    super.customizeSettings(view, editMode)

    GraphSettingsProvider.getInstance(project).getSettings(graph)
      .setCurrentLayouter(getLayouter)
  }

  def getLayouter = {
    val layouter = GraphManager.getGraphManager.createHierarchicLayouter()
    layouter.setMinimalNodeDistance(50)
    layouter.setMinimalLayerDistance(50)
    layouter.setOrientationLayouterEnabled(true)
    layouter
  }

  /**
   * Lazily instantiated eip graph node render, which will handle drawing the nodes
   * wtihin this graph
   */
  def eipGraphNodeRenderer = new EipGraphNodeRenderer(getGraphBuilder)

  /**
   * Provide a concrete implementation of a node realizer which allows for a custom
   * draw of a given EipProcessor/Node
   *
   * @param eipProcessor The given eip processor node
   * @return A custom node realizer for this node
   */
  override def getNodeRealizer(eipProcessor: EipProcessor): NodeRealizer = {
    GraphViewUtil.createNodeRealizer("EipProcessorRenderer", eipGraphNodeRenderer)
  }


}
