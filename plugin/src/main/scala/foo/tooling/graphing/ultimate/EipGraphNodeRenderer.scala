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
   * The padding between components
   */
  val PADDING = 5

  /**
   * The maximum label length before text cropping should occur
   */
  val MAX_LABEL_WIDTH = 250

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

      val container = mutate(new JPanel())(
        x => x.setLayout(new BoxLayout(x, BoxLayout.Y_AXIS)),
        _.setOpaque(false),
        _.setFocusable(false)
      )

      // Add every child component centered
      val children = createChildren(node, isSelected)
      children.foreach(child => {
        child.setAlignmentX(Component.CENTER_ALIGNMENT)
        child.setOpaque(false)
        container.add(child)
      })

      // Calculate the required size
      val (width, height) = (
        children.map(_.getPreferredSize.getWidth).max.toInt + PADDING,
        children.map(_.getPreferredSize.getHeight).sum.toInt
      )

      container.setPreferredSize(new Dimension(width, height))
      realizer.setSize(width, height)

      container
    })
  }

  /**
   * Creates the children JComponent elements associated with this EipProcessor
   * @param node The EipProcessor to visualize
   * @param isSelected A flag which indicates if this node is selected or not.
   *                   IE True if this node is selected, false otherwise
   * @return The list of individual children JComponents associated with this
   *         processor
   */
  def createChildren(node: EipProcessor, isSelected: Boolean) = {
    val iconLabel = {
      // delegate Node creation to the icon loader strategy
      val icon = iconLoader.loadIcon(node, isSelected)
      val label = new JLabel(icon)
      label.setSize(icon.getIconWidth, icon.getIconHeight)
      label
    }

    val children = List(
      new JLabel(node.processor.prettyName),
      iconLabel
    )

    val nodeText = node.text
    if(nodeText.nonEmpty) children :+ createJLabel(nodeText)
    else children
  }

  private def createJLabel(text: String, maxWidth:Int = MAX_LABEL_WIDTH): JLabel = {
    mutate(new JLabel(text)) {
      label =>
        val fontMetrics = label.getFontMetrics(label.getFont)
        val width = Math.min(fontMetrics.stringWidth(text) + PADDING, MAX_LABEL_WIDTH)
        label.setPreferredSize(new Dimension(width, 21))
    }
  }
}
