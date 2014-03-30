package foo.graphing.ultimate

import com.intellij.openapi.graph.builder.renderer.{BasicNodeCellRenderer, BasicGraphNodeRenderer}
import foo.eip.graph.EipProcessor
import foo.eip.graph.ADT.Edge
import com.intellij.openapi.util.ModificationTracker
import com.intellij.openapi.graph.builder.GraphBuilder
import javax.swing._
import foo.eip.graph.loaders.IntellijIconLoader
import com.intellij.openapi.graph.view.{NodeCellRenderer, Graph2DView, NodeRealizer}
import java.awt.{GridBagConstraints, Component, BorderLayout, Color}
import com.intellij.ui.{JBColor, LightColors}
import foo.eip.graph.ADT.Edge
import com.intellij.ui.components.panels.NonOpaquePanel

/**
 * A concrete implementation of an EipProcessor renderer,
 * which has the ability to draw a given node in a graph
 * @param der The graph builder currently being used to create a graph
 */
class EipGraphNodeRenderer(builder: GraphBuilder[EipProcessor, Edge[EipProcessor, String]])
  extends NodeCellRenderer {
//  extends BasicGraphNodeRenderer[EipProcessor, Edge[EipProcessor, String]](
    // builder,
 //   ModificationTracker.NEVER_CHANGED
 // ) {

/*  override def getIcon(node: EipProcessor): Icon = {
    val icon = (new Object with IntellijIconLoader).loadUnpickedIcon(node.eipType.toString.toLowerCase)
    icon
  }*/
/*
  override def getLabelPanel(nodeRealizer: NodeRealizer): JComponent = {
    nodeRealizer.setLabelText("")
    super.getLabelPanel(nodeRealizer)
  }*/
/*  override def getIconLabel(node: EipProcessor): JComponent = {

  }*/
/*  override def tuneNode(nodeRealizer: NodeRealizer, wrapper: JPanel): Unit = {
    wrapper.removeAll()
    //wrapper.add(new JButton("Foo: " + builder.getNodeObject(nodeRealizer.getNode).text))
  val button = new JButton()
  button.setText("             ")
  button.setBackground(Color.RED)
    wrapper.add(button)
  }

  override def getBackground(node: EipProcessor): Color = {
    LightColors.RED
  }

  override def getSelectionColor: Color = {
    new JBColor(new Color(0xff00cc), new Color(0xff00cc))
  }*/


/*  override def getRendererComponent(graph2dview: Graph2DView, noderealizer: NodeRealizer, obj: scala.Any, isSelected: Boolean): JComponent = {
    new JButton("I am currently selected : " + isSelected)
  }*/

  /**
   * Serves as a cache for previously created NodeRealizers.
   * The Boolean is associated with the fact that it is selected or not.
   * The value is the already created JComponent
   */
  var cache = collection.mutable.Map[(NodeRealizer, Boolean), JComponent]()

  def getNodeCellRendererComponent(graph: Graph2DView,
                                   realizer: NodeRealizer,
                                   obj: AnyRef,
                                   isSelected: Boolean): JComponent = {
    cache.getOrElseUpdate((realizer, isSelected), {
      val node = builder.getNodeObject(realizer.getNode)
      val label = {
        val eipType = node.eipType.toString.toLowerCase
        val icon =
          if(isSelected) (new Object with IntellijIconLoader).loadPickedIcon(eipType)
          else (new Object with IntellijIconLoader).loadUnpickedIcon(eipType)
        val label = new JLabel(icon)
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5))
        label.setSize(icon.getIconHeight, icon.getIconHeight)
        realizer.setSize(icon.getIconWidth, icon.getIconHeight)
        label
      }
      label
    })
  }
}
