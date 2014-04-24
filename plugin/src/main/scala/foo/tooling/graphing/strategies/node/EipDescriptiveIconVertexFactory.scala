package foo.tooling.graphing.strategies.node

import foo.tooling.graphing.strategies.icons.EipIconLoader
import foo.tooling.graphing.EipProcessor
import javax.swing.{JLabel, BoxLayout, JPanel, JComponent}
import foo.FunctionalUtil._
import java.awt.{Dimension, Component}

/**
 * Concrete EipVertexFactory which produces a visual EIP icon, and a descriptive
 * text associated with the vertex
 *
 * @param iconLoader An Eip Icon Loader implementation
 */
class EipDescriptiveIconVertexFactory(iconLoader: EipIconLoader) extends EipVertexFactory {
  /**
   * Serves as a cache for previously created NodeRealizers.
   * The Boolean is associated with the fact that it is selected or not.
   * The value is the already created JComponent
   */
  var cache = collection.mutable.Map[(EipProcessor, Boolean), JComponent]()

  /**
   * The padding between components
   */
  val PADDING = 5

  /**
   * The maximum label length before text cropping should occur
   */
  val MAX_LABEL_WIDTH = 250

  /**
   * Creates the appropriate vertex which can be rendered within an Eip Graph
   * @param processor The EipProcessor to visualize
   * @param isSelected A flag which indicates if this node is selected or not.
   *                   IE True if this node is selected, false otherwise
   * @return A visual representation of the given processor
   */
  override def create(processor: EipProcessor, isSelected: Boolean): JComponent = {
    cache.getOrElseUpdate((processor, isSelected), {
      val container = mutate(new JPanel())(
        x => x.setLayout(new BoxLayout(x, BoxLayout.Y_AXIS)),
        _.setOpaque(false),
        _.setFocusable(false)
      )

      // Add every child component centered
      val children = createChildren(processor, isSelected)
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

  /**
   * Creates a given JLabel with the appropriate text. The preferredSize
   * for this node will be calculated
   * @param text The given text
   * @param maxWidth The maximum size of this node, in pixels
   * @return The created JLabel
   */
  private def createJLabel(text: String, maxWidth:Int = MAX_LABEL_WIDTH): JLabel = {
    mutate(new JLabel(text)) {
      label =>
        val fontMetrics = label.getFontMetrics(label.getFont)
        val width = Math.min(fontMetrics.stringWidth(text) + PADDING, MAX_LABEL_WIDTH)
        label.setPreferredSize(new Dimension(width, 21))
    }
  }
}