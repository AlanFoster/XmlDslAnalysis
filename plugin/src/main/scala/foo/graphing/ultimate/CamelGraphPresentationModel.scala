package foo.graphing.ultimate

import com.intellij.openapi.graph.builder.components.BasicGraphPresentationModel
import com.intellij.openapi.graph.base.Graph
import foo.eip.graph.EipProcessor
import com.intellij.openapi.graph.view._
import com.intellij.openapi.graph.settings.GraphSettingsProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.graph.GraphManager
import java.awt.Color
import foo.eip.graph.ADT.Edge

/**
 * The graph presentation model deals with edges/nodes drawing etc.
 * For instance being able to provide custom realizors for nodes.
 */
class CamelGraphPresentationModel(graph: Graph, project: Project)
  extends BasicGraphPresentationModel[EipProcessor, Edge[EipProcessor, String]](graph) {

  /**
   * Provides access to the default node realizer configuration for EIP nodes.
   * The first time this value is called, the configuration will be registered
   * for use within generic node's configurations under the returned name.
   */
  private lazy val PLUGGABLE_NODEREALIZER_CONFIGURATION = {
    val registeredName = "EipPluggableConfiguration"
    registerDefaultNodeRealizerConfiguration(registeredName)
    registeredName
  }

  /**
   * Provides customized settings for the view, editMode instances.
   * This method currently sets the default layout, as the getDefaultLayer method isn't
   * called in this class for some reason..
   *
   * @param view
   * @param editMode
   */
  override def customizeSettings(view: Graph2DView, editMode: EditMode): Unit = {
    super.customizeSettings(view, editMode)

    // Disable various view/editing modes etc
    view.setGridVisible(false)
    view.setAntialiasedPainting(false)
    view.setFitContentOnResize(false)

    editMode.allowResizeNodes(false)
    editMode.allowEdgeCreation(false)
    editMode.allowBendCreation(false)

    // Bind our default layout - ie bubble/hierarchical etc
    val layouter = getLayouter
    GraphSettingsProvider.getInstance(project).getSettings(graph)
      .setCurrentLayouter(layouter)
  }

  /**
   * The layout mechanism associated with this graph instance
   */
  lazy val getLayouter = {
    val layouter = GraphManager.getGraphManager.createHierarchicGroupLayouter()
    layouter.setMinimalNodeDistance(80)
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
    // Create an instance of a generic realizer, and provide the configuration name
    val realizor = GraphManager.getGraphManager.createGenericNodeRealizer(PLUGGABLE_NODEREALIZER_CONFIGURATION)
    realizor
  }

  /**
   * Registers the default configuration for a node. This configuration will then be available to a newly
   * created generic node, under the given registeredName
   *
   * @param registeredName The name of the configuration map to register under.
   */
  private def registerDefaultNodeRealizerConfiguration(registeredName: String) {
    // Get the default map associated with Realization components, and modify it with our pluggable instances
    val factory = GenericNodeRealizer.Statics.getFactory
    val defaultConfigurationMap = factory.createDefaultConfigurationMap().asInstanceOf[java.util.Map[Any, Any]]

    // Provide a custom implementation for painting nodes
    defaultConfigurationMap.put(
      classOf[GenericNodeRealizer.Painter],
      GraphManager.getGraphManager.createNodeCellRendererPainter(eipGraphNodeRenderer, NodeCellRendererPainter.USER_DATA_MAP)
    )

    // Allow a custom implementation for drawing hotspots, ie handle bars when selected
    defaultConfigurationMap.put(
      classOf[GenericNodeRealizer.HotSpotPainter],
      EipHotspotPainter.createWrapper(new EipHotspotPainter)
    )

    // Register the configuration under the required name, such that new generic instances can use the configuration
    factory.addConfiguration(registeredName, defaultConfigurationMap)
  }

  // Not sure when these are triggered...
  override def editEdge(e: Edge[EipProcessor, String]): Boolean = false
  override def editNode(n: EipProcessor): Boolean = false

  /**
   * An edge realizer allows for an edge to be drawn visually within a visual component.
   *
   * @param edge The given edge of the IJ graph
   * @return The edge realizer associated with this edge
   */
  override def getEdgeRealizer(edge: Edge[EipProcessor, String]): EdgeRealizer = {
    val edgeRealizer = GraphManager.getGraphManager.createPolyLineEdgeRealizer()
    edgeRealizer.setSmoothedBends(true)
    edgeRealizer.setLineType(LineType.LINE_1)
    edgeRealizer.setLineColor(Color.DARK_GRAY)
    edgeRealizer.setSourceArrow(Arrow.NONE)
    edgeRealizer.setTargetArrow(Arrow.STANDARD)

    edgeRealizer
  }

  /**
   * Provides the tooltip associated with a given node, ie when hovering the mouse above the node
   * @param node The Eip Node
   * @return The text to display when the user has hovered over this node
   */
  override def getNodeTooltip(node: EipProcessor): String = node.text
}
