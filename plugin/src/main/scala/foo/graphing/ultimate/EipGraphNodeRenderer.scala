package foo.graphing.ultimate

import com.intellij.openapi.graph.builder.renderer.BasicGraphNodeRenderer
import foo.eip.graph.EipProcessor
import foo.eip.graph.ADT.Edge
import com.intellij.openapi.util.ModificationTracker
import com.intellij.openapi.graph.builder.GraphBuilder
import javax.swing.{JComponent, Icon}
import foo.eip.graph.loaders.IntellijIconLoader
import com.intellij.openapi.graph.view.NodeRealizer

/**
 * A concrete implementation of an EipProcessor renderer,
 * which has the ability to draw a given node in a graph
 * @param builder The graph builder currently being used to create a graph
 */
class EipGraphNodeRenderer(builder: GraphBuilder[EipProcessor, Edge[EipProcessor, String]])
  extends BasicGraphNodeRenderer[EipProcessor, Edge[EipProcessor, String]](
    builder,
    ModificationTracker.NEVER_CHANGED
  ) {

  override def getIcon(node: EipProcessor): Icon = {
    val icon = (new Object with IntellijIconLoader).loadUnpickedIcon(node.eipType.toString.toLowerCase)
    icon
  }

  override def getLabelPanel(nodeRealizer: NodeRealizer): JComponent = {
    nodeRealizer.setLabelText("")
    super.getLabelPanel(nodeRealizer)
  }
}
