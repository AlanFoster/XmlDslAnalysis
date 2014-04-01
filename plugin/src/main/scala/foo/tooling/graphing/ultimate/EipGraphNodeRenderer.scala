package foo.tooling.graphing.ultimate

import com.intellij.openapi.graph.builder.GraphBuilder
import javax.swing._
import com.intellij.openapi.graph.view.{NodeCellRenderer, Graph2DView, NodeRealizer}
import foo.tooling.graphing.ADT.Edge
import foo.tooling.graphing.EipProcessor
import foo.tooling.graphing.strategies.icons.{EipIconLoader, IntellijIconLoader}

/**
 * A concrete implementation of an EipProcessor renderer, which has the ability to
 * draw a given node in a graph.
 *
 * This implementation is used in conjunction with a 'GenericNodeRealizer' - which appears
 * to be an implementation of the strategy pattern.
 *
 * @param builder The graph builder currently being used to create a graph
 */
class EipGraphNodeRenderer(builder: GraphBuilder[EipProcessor, Edge[EipProcessor, String]], iconLoader: EipIconLoader)
  extends NodeCellRenderer {
  /**
   * Serves as a cache for previously created NodeRealizers.
   * The Boolean is associated with the fact that it is selected or not.
   * The value is the already created JComponent
   */
  var cache = collection.mutable.Map[(NodeRealizer, Boolean), JComponent]()

  /**
   * Creates a new JComponent associated with this node.
   *
   * @param graph The Graph2DView
   * @param realizer The given NodeRealizer that the JComponent is associated with.
   *                 Each node will have its own unique NodeRealizer, which allows it for it
   *                 to be rendered.
   * @param obj Not sure... Appears to always be null...
   *            yFiles suggests it is an additional user object
   * @param isSelected A flag which indicates if this node is selected or not.
   *                   IE True if this node is selected, false otherwise
   * @return A previously cached JComponent for this given realizer configuration, or a new JComponent instance,
   *         which is then subsequently cached
   */
  def getNodeCellRendererComponent(graph: Graph2DView,
                                   realizer: NodeRealizer,
                                   obj: AnyRef,
                                   isSelected: Boolean): JComponent = {
    cache.getOrElseUpdate((realizer, isSelected), {
      val node = builder.getNodeObject(realizer.getNode)
      val label = {
        // delegate Node creation to the icon loader strategy
        val icon = iconLoader.loadIcon(node, isSelected)
        val label = new JLabel(icon)
        label.setSize(icon.getIconHeight, icon.getIconHeight)
        realizer.setSize(icon.getIconWidth, icon.getIconHeight)
        label
      }
      label
    })
  }
}