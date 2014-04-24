package foo.tooling.graphing.ultimate

import com.intellij.openapi.graph.builder.GraphBuilder
import javax.swing._
import com.intellij.openapi.graph.view.{NodeCellRenderer, Graph2DView, NodeRealizer}
import foo.tooling.graphing.EipProcessor
import foo.tooling.graphing.strategies.icons.EipIconLoader
import java.awt._
import foo.FunctionalUtil._
import foo.tooling.graphing.ADT.Edge
import scala.List
import foo.tooling.graphing.strategies.node.EipVertexFactory

/**
 * A concrete implementation of an EipProcessor renderer, which has the ability to
 * draw a given node in a graph.
 *
 * This implementation is used in conjunction with a 'GenericNodeRealizer' - which appears
 * to be an implementation of the strategy pattern.
 *
 * @param builder The graph builder currently being used to create a graph
 */
class EipGraphNodeRenderer(builder: GraphBuilder[EipProcessor, Edge[EipProcessor, String]], eipVertexFactory: EipVertexFactory)
  extends NodeCellRenderer {

  def getNodeCellRendererComponent(graph: Graph2DView,
                                   realizer: NodeRealizer,
                                   obj: AnyRef,
                                   isSelected: Boolean): JComponent = {
      val node = builder.getNodeObject(realizer.getNode)

      val component = eipVertexFactory.create(node, isSelected)
      val preferredSize = component.getPreferredSize

      realizer.setSize(preferredSize.getWidth, preferredSize.getHeight)

      component
  }


}
