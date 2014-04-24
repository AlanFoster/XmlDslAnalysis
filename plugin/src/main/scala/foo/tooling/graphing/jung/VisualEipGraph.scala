package foo.tooling.graphing.jung

import edu.uci.ics.jung.visualization._
import java.awt._
import org.apache.commons.collections15.Transformer
import javax.swing._
import java.awt.geom.{AffineTransform, Rectangle2D}
import edu.uci.ics.jung.visualization.control._
import java.awt.event._
import foo.FunctionalUtil._
import foo.tooling.graphing.{EipProcessor, StaticGraphTypes}
import StaticGraphTypes.EipDAG
import foo.tooling.serializers.CompleteEipDagSerializer
import edu.uci.ics.jung.algorithms.layout.{Layout, StaticLayout, TreeLayout}
import javax.swing.border.LineBorder
import foo.tooling.graphing.strategies.icons.{EipIconLoader, IconLoader}
import foo.tooling.graphing.strategies.tooltip.ToolTipStrategy
import foo.tooling.graphing.strategies.node.EipVertexFactory
import edu.uci.ics.jung.visualization.renderers.Renderer.Vertex
import edu.uci.ics.jung.visualization.renderers.BasicVertexRenderer
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator

/**
 * Class used to represent a visual representation of the the given EipDAG.
 *
 * Note this class requires a concrete implementation of an IconLoader, which
 * abstracts the differences in Java and IntelliJ's classloader for acquiring
 * access to icons. This improves testability, and removes the dependency on intellij.
 * This icon loader  can be provided with Scala's Mixins.
 *
 * @param eipDag In order to be agnostic of the underlying technology/DSL used for
 *               creating the given Camel routes, the EipDAG abstraction is instead used
 */
class VisualEipGraph(eipDag: EipDAG, eipVertexFactory: EipVertexFactory, tooltipStrategy: ToolTipStrategy) {
  /**
   * Create a type alias for VisualizationViewer to improve code readability
   */
  type Viewer = VisualizationViewer[EipProcessor, String]

  /**
   * Creates a new Viewer
   * @return A new Visual viewer for the given EipDAG
   */
  def createViewer(model: VisualizationModel[EipProcessor, String]): Viewer = {
    val viewer = new VisualizationViewer(model, new Dimension(3000, 3000))

    // Create our bound java component ie, the Swing graph
    // Note the use of functional composition in order to compose features
    val boundJavaComponent = (
      setBackground _
        andThen setLineRender
        andThen bindGraphMouse
        andThen bindEipRenderer
        andThen setComponentToolTip
        andThen addGraphToXmlDebugOption
      )(viewer)

    boundJavaComponent
  }


  def createSatelliteViewer(parent: Viewer, model: VisualizationModel[EipProcessor, String]): Viewer = {
    val viewer = new SatelliteVisualizationViewer[EipProcessor, String](parent, new Dimension(200, 200))

    // Create our bound java component ie, the Swing graph
    // Note the use of functional composition in order to compose features
    // Note this list differs from the main parent control's added features
    val boundJavaComponent = (
      setBackground _
        andThen setLineRender
        andThen bindEipRenderer
        andThen setComponentToolTip
      )(viewer)

    // Scale the component to its available size
    viewer.scaleToLayout(new CrossoverScalingControl)

    boundJavaComponent
  }


  /**
   * @return A scrollable Viewer
   */
  def createScrollableViewer: JComponent = {
    val visualiser = new EipGraphVisualisation(eipDag)
    val graph = visualiser.asJungGraph(eipDag)

    val minimumSpanningForest = GraphGlue.newMinimumSpanningForest(graph)

    val treeLayout = new TreeLayout(minimumSpanningForest.getForest, 125, 125)
    val staticLayout = new StaticLayout(graph, treeLayout)

    val visualisationModel = new DefaultVisualizationModel(staticLayout)

    val mainViewer = createViewer(visualisationModel)
    val mainZoomPane = new GraphZoomScrollPane(mainViewer)

    val satelliteViewer = createSatelliteViewer(mainViewer, visualisationModel)
    satelliteViewer.setBorder(LineBorder.createBlackLineBorder())

    mainViewer.setLayout(new BorderLayout())

    // Satellite View
    val rightHandSideBoxLayer = new JLayeredPane
    rightHandSideBoxLayer.setLayout(new FlowLayout(FlowLayout.RIGHT))
    rightHandSideBoxLayer.add(satelliteViewer, BorderLayout.LINE_END)

    mainViewer.add(rightHandSideBoxLayer, BorderLayout.SOUTH)

    mainZoomPane
  }


  /**
   * Binds the EIP renderers to the given Viewer
   * @param viewer The viewer that will render EIP icons
   * @return The Viewer
   */
  private def bindEipRenderer(viewer: Viewer): Viewer = {
    def getNodeComponent(component: EipProcessor): JComponent = {
      val isSelected = viewer.getPickedVertexState.getPicked.contains (component)
      eipVertexFactory.create(component, isSelected)
    }

    viewer.getRenderer.setVertexRenderer(new EipVertexRenderer(getNodeComponent))
    viewer.getRenderContext.setVertexShapeTransformer(new Transformer[EipProcessor, Shape] {
      def transform(component: EipProcessor): Shape = {
        val nodeComponent = getNodeComponent(component)
        val preferredSize = nodeComponent.getPreferredSize

        val (width, height) = (preferredSize.getWidth, preferredSize.getHeight)
        new Rectangle2D.Double(-(width / 2), -(height / 2), width, height)
      }
    })

    viewer
  }

  /**
   * Adds a component tooltip for each vertices
   * @param viewer The viewer
   * @return the viewer
   */
  private def setComponentToolTip(viewer: Viewer): Viewer =
    mutate(viewer) {
      _.setVertexToolTipTransformer(new Transformer[EipProcessor, String] {
        def transform(component: EipProcessor): String = {
          tooltipStrategy.createTooltip(component)
        }
      })
    }

  /**
   * Sets the Line renderer of the viewer
   * @param viewer The viewer
   * @return The viewer
   */
  private def setLineRender(viewer: Viewer): Viewer = viewer
    //mutate(viewer)(_.getRenderContext.setEdgeShapeTransformer(new EdgeShape.Line))

  /**
   * Sets the background of the viewer
   * @param viewer The viewer
   * @return The viewer
   */
  private def setBackground(viewer: Viewer): Viewer =
    mutate(viewer)(_.setBackground(Color.white))

  /**
   * Binds the mouse control to the given VisualizationViewer
   * @param viewer The viewer to modify
   */
  def bindGraphMouse(viewer: Viewer): Viewer = mutate(viewer)(viewer => {
    val graphMouse = new DefaultModalGraphMouse[EipProcessor, String]()
    viewer.setGraphMouse(graphMouse)

    graphMouse.setMode(ModalGraphMouse.Mode.PICKING)

    /**
     * Toggles the graph mouse mode
     * @param isTransforming If true sets the graph mouse mode to Transforming, otherwise picking
     */
    def toggle(isTransforming: Boolean) {
      val newMode =
        if (isTransforming) ModalGraphMouse.Mode.TRANSFORMING
        else ModalGraphMouse.Mode.PICKING
      graphMouse.setMode(newMode)
    }

    // Bind a keyboard listener, to toggle to Transforming mode when space bar is pressed
    viewer.addKeyListener(new KeyListener {
      def keyPressed(e: KeyEvent) =
        if (e.getKeyCode == KeyEvent.VK_SPACE) toggle(isTransforming = true)

      def keyReleased(e: KeyEvent) =
         if (e.getKeyCode == KeyEvent.VK_SPACE) toggle(isTransforming = false)

      def keyTyped(e: KeyEvent) {}
    })
    viewer.requestFocus()
  })

  /**
   * Adds a right click option to JUNG to create a debug output of the loaded EipDag
   * @param viewer The current viewer
   * @return The viewer which now contains the option to generate debug output
   */
  def addGraphToXmlDebugOption(viewer: Viewer): Viewer = {
    val graphMouse = viewer.getGraphMouse.asInstanceOf[DefaultModalGraphMouse[EipProcessor, String]]
    graphMouse.add(new DebugGraphToXmlPlugin(eipDag))
    viewer
  }
}
