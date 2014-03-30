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
import com.intellij.openapi.graph.builder.util.GraphViewUtil

/**
 * The graph presentation model deals with edges/nodes drawing etc.
 * For instance being able to provide custom realizors for nodes.
 */
class CamelGraphPresentationModel(graph: Graph, project: Project)
  extends BasicGraphPresentationModel[EipProcessor, Edge[EipProcessor, String]](graph) {

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
   // GraphManager.getGraphManager.createGenericNodeRealizer()
    //val implementations = GenericNodeRealizer.Statics.getFactory.getAvailableConfigurations

    // Get the default map assocaited with Realization components, and modify it with our pluggable instances
    val factory = GenericNodeRealizer.Statics.getFactory
    val defaultConfigurationMap = factory.createDefaultConfigurationMap().asInstanceOf[java.util.Map[Any, Any]]
    defaultConfigurationMap.put(
      classOf[GenericNodeRealizer.Painter],
      GraphManager.getGraphManager.createNodeCellRendererPainter(eipGraphNodeRenderer, NodeCellRendererPainter.USER_DATA_MAP)
    )

/*
    val impl = new AbstractCustomHotSpotPainter {
      override def hotSpotHit(noderealizer: NodeRealizer, d: Double, d1: Double): Byte = ???
      override def paintHotSpots(noderealizer: NodeRealizer, graphics2d: Graphics2D): Unit = ???
    }
*/

/*    defaultConfigurationMap.put(
      classOf[GenericNodeRealizer.HotSpotPainter],
      //GraphManager.getGraphManager.createNodeCellRendererPainter(eipGraphNodeRenderer, NodeCellRendererPainter.USER_DATA_MAP)
      GraphManager.getGraphManager.createColorRenderer()
    )*/

    factory.addConfiguration("EipPluggableConfiguration", defaultConfigurationMap)


  // Create an instance of a generic realizer, and provide the configuration name
   val realizor =  GraphManager.getGraphManager.createGenericNodeRealizer("EipPluggableConfiguration")

    realizor

    // GraphViewUtil.createNodeRealizer("EipProcessorRender", eipGraphNodeRenderer)
    // http://docs.yworks.com/yfiles/doc/api/y/view/GenericNodeRealizer.html
    // You seem to create a generic node realizer, then override what you want



/*    val path = "file:\\C:\\Users\\a\\Documents\\GitHub\\XmlDslAnalysis\\plugin\\src\\main\\resources\\eip\\unpicked\\from.gif"*/
    //val icon = (new Object with IntellijIconLoader).loadUnpickedIcon(eipProcessor.eipType.toString.toLowerCase)
/*    val nodeRealizer = GraphManager.getGraphManager.createImageNodeRealizer()

    //nodeRealizer.setImage(new ImageIcon(path).getImage)
    nodeRealizer.setImageURL(new URL(path))
    nodeRealizer.setAlphaImageUsed(true)
    nodeRealizer.setToImageSize()*/
/*
    nodeRealizer*/
  }

  override def editEdge(e: Edge[EipProcessor, String]): Boolean = {
    false
  }
  override def editNode(n: EipProcessor): Boolean = false

  override def getEdgeRealizer(e: Edge[EipProcessor, String]): EdgeRealizer = {
    val edgeRealizer = GraphManager.getGraphManager.createPolyLineEdgeRealizer()
    edgeRealizer.setSmoothedBends(true)
    edgeRealizer.setLineType(LineType.LINE_1)
    edgeRealizer.setLineColor(Color.DARK_GRAY)
    edgeRealizer.setSourceArrow(Arrow.NONE)
    edgeRealizer.setTargetArrow(Arrow.STANDARD)

    edgeRealizer
  }

  override def getNodeTooltip(n: EipProcessor): String = n.text
}
