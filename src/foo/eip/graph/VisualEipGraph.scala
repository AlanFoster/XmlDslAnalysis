package foo.eip.graph

import foo.Model._
import edu.uci.ics.jung.visualization.{GraphZoomScrollPane, VisualizationViewer}
import java.awt.{Color, Shape}
import org.apache.commons.collections15.Transformer
import javax.swing._
import java.awt.geom.Rectangle2D
import edu.uci.ics.jung.visualization.decorators.EdgeShape
import edu.uci.ics.jung.visualization.control.{ModalGraphMouse, DefaultModalGraphMouse}
import java.awt.event.{KeyEvent, KeyListener}

import foo.FunctionalUtil._
import foo.eip.graph.loaders.{DefaultIconLoader, IconLoader}
import foo.eip.graph.EipGraphCreator
import foo.eip.graph.Visualisation.EipGraphVisualisation
import foo.eip.graph.StaticGraphTypes.EipDAG

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
abstract class VisualEipGraph(eipDag: EipDAG) extends IconLoader {
  /**
   * Create a type alias for VisualizationViewer to improve code readability
   */
  type Viewer = VisualizationViewer[EipComponent, String]

  /**
   * Creates a new Viewer
   * @return A new Visual viewer for the given EipDAG
   */
  def createViewer = {
    val visualiser = new EipGraphVisualisation(eipDag)
    val viewer = visualiser.asVisualGraph(visualiser.asJungGraph(eipDag))

    // Create our bound java component ie, the Swing graph
    // Note the use of functional composition in order to compose features
    val boundJavaComponent = (
      setBackground _
        andThen setLineRender
        andThen bindGraphMouse
        andThen bindEipRenderer
        andThen setComponentToolTip
      )(viewer)

    boundJavaComponent
  }

  /**
   * @return A scrollable Viewer
   */
  def createScrollableViewer: GraphZoomScrollPane =
    new GraphZoomScrollPane(createViewer)

  /**
   * Binds the EIP renderer to the given Viewer
   * @param viewer The viewer that will render EIP icons
   * @return The Viewer
   */
  private def bindEipRenderer(viewer:Viewer): Viewer = {
    def getIcon(component: EipComponent): Icon = {
      val isPicked = viewer.getPickedVertexState.getPicked.contains(component)
      val eipType = component.eipType
      if (isPicked) loadPickedIcon(eipType)
      else loadUnpickedIcon(eipType)
    }

    viewer.getRenderContext.setVertexIconTransformer(new Transformer[EipComponent, Icon] {
      override def transform(component: EipComponent): Icon = getIcon(component)
    })

    viewer.getRenderContext.setVertexShapeTransformer(new Transformer[EipComponent, Shape] {
      def transform(component: EipComponent): Shape = {
        val icon = getIcon(component)

        val (width, height) = (icon.getIconWidth.toDouble, icon.getIconHeight.toDouble)
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
      _.setVertexToolTipTransformer(new Transformer[EipComponent, String] {
        def transform(component: EipComponent): String = component.text
      })
    }

  /**
   * Sets the Line renderer of the viewer
   * @param viewer The viewer
   * @return The viewer
   */
  private def setLineRender(viewer: Viewer): Viewer =
    mutate(viewer)(_.getRenderContext.setEdgeShapeTransformer(new EdgeShape.Line))

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
    val graphMouse = new DefaultModalGraphMouse[ProcessorDefinition, String]()
    viewer.setGraphMouse(graphMouse)

    graphMouse.setMode(ModalGraphMouse.Mode.PICKING)

    /**
     * Toggles the graph mouse mode
     * @param isTransforming If true sets the graph mouse mode to Transforming, otherwise picking
     */
    def toggle(isTransforming: Boolean) {
      val newMode =
        if (isTransforming)  ModalGraphMouse.Mode.TRANSFORMING
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
  })

}

// Simple manual testing, note the DefaultIconLoader mixin
object Starter {
  def main(args: Array[String]) {
    val component = (new VisualEipGraph(null) with DefaultIconLoader).createScrollableViewer

    val jframe = new JFrame()
    jframe.setSize(500, 700)
    jframe.getContentPane.add(component)
    jframe.setVisible(true)
    jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  }
}
