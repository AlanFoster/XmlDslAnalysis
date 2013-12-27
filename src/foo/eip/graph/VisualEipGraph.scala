package foo.eip.graph

import edu.uci.ics.jung.visualization.{GraphZoomScrollPane, VisualizationViewer}
import java.awt._
import org.apache.commons.collections15.Transformer
import javax.swing._
import java.awt.geom.Rectangle2D
import edu.uci.ics.jung.visualization.decorators.EdgeShape
import edu.uci.ics.jung.visualization.control.{AbstractPopupGraphMousePlugin, ModalGraphMouse, DefaultModalGraphMouse}
import java.awt.event._

import org.apache.commons.collections15.Factory

import foo.FunctionalUtil._
import foo.eip.graph.loaders.{DefaultIconLoader, IconLoader}
import foo.eip.graph.Visualisation.EipGraphVisualisation
import foo.eip.graph.StaticGraphTypes.EipDAG
import foo.eip.serializers.{TypeEipDagSerializer, EipDagSerializer}
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import edu.uci.ics.jung.graph.util.EdgeType
import foo.eip.graph.ADT.EmptyDAG
import scala.List

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
  def createViewer: Viewer = {
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
    val graphMouse = new DefaultModalGraphMouse[EipComponent, String]()
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

  /**
   * Adds a right click option to JUNG to create a debug output of the loaded EipDag
   * @param viewer The current viewer
   * @return The viewer which now contains the option to generate debug output
   */
  def addGraphToXmlDebugOption(viewer: Viewer): Viewer = {
    val graphMouse = viewer.getGraphMouse.asInstanceOf[DefaultModalGraphMouse[EipComponent, String]]
    graphMouse.add(new DebugGraphToXmlPlugin(eipDag))
    viewer
  }

}

/**
 * An Abstract Popup Plugin which, when the graph is right clicked, will show a debug menu
 * Which will allow the user to create an EipDag debug output.
 *
 * @param eipDag The current EIP diagram
 */
class DebugGraphToXmlPlugin(eipDag: EipDAG) extends AbstractPopupGraphMousePlugin() with MouseListener {
  /**
   * Creates the JPopupMenu at the event source location
   * @param e the MouseEvent
   */
  def handlePopup(e: MouseEvent): Unit = {
    val viewer = e.getSource.asInstanceOf[Component]
    val popUp = new JPopupMenu()
    popUp.add(new AbstractAction("Output EIP") {
      def actionPerformed(e: ActionEvent): Unit = {
        println(new TypeEipDagSerializer().serialize(eipDag))
      }
    })
    popUp.show(viewer, e.getX, e.getY)
  }
}

// Simple manual testing, note the DefaultIconLoader mixin
object Starter {
  def fakeGraph() = {
    val graph = new DirectedSparseMultigraph[String, Integer]()

    val edgeFactory = new Factory[Integer] {
      var i = 1
      def create(): Integer = {
        i = i + 1
        i
      }
    }

    val a0 = "A0"
    val b0 = "B0"
    val b1 = "B1"
    val c1 = "c1"
    val d1 = "d1"

    graph.addVertex(a0)
    graph.addVertex(b0)
    graph.addVertex(b1)
    graph.addVertex(c1)
    graph.addVertex(d1)

    graph.addEdge(edgeFactory.create(), a0, b0, EdgeType.DIRECTED)
    graph.addEdge(edgeFactory.create(), a0, b1, EdgeType.DIRECTED)
    graph.addEdge(edgeFactory.create(), b0, c1, EdgeType.DIRECTED)
    graph.addEdge(edgeFactory.create(), b1, c1, EdgeType.DIRECTED)
    graph.addEdge(edgeFactory.create(), c1, d1, EdgeType.DIRECTED)

    graph
  }

  def main(args: Array[String]) {
    import EipGraphCreator._

    val from = EipComponent("from", "from", "from", null)
    val choice = EipComponent("choice", "choice", "choice", null)

    val when1 = EipComponent("when1", "when", "when1", null)
    val foo = EipComponent("foo", "to", "foo", null)

    val when2 = EipComponent("when2", "when", "when2", null)
    val bar = EipComponent("bar", "to", "bar", null)

    val afterChoice = EipComponent("afterChoice", "to", "afterChoice", null)

    val eipDag = EmptyDAG[EipComponent, String]()
      .linkComponents(List(), from)
      .linkComponents(from, choice)
      .linkComponents(choice, when1)
      .linkComponents(when1, foo)
      .linkComponents(choice, when2)
      .linkComponents(when2, bar)
      .linkComponents(List(foo, bar), afterChoice)

    val component = (new VisualEipGraph(eipDag) with DefaultIconLoader).createScrollableViewer

   // val graph = fakeGraph()
/*
    val delegateForest = new DelegateForest(graph)
    val layout = new TreeLayout(delegateForest, 100, 100)
    val viewer = new VisualizationViewer(layout)
    val component = new GraphZoomScrollPane(viewer)
*/
/*    val minimumSpanningForest = GraphGlue.newMinimumSpanningForest(graph)
    val treeLayout = new StaticLayout(graph, new TreeLayout(minimumSpanningForest.getForest))
    val viewer = new VisualizationViewer(treeLayout)

    mutate(viewer)(_.getRenderContext.setEdgeShapeTransformer(new EdgeShape.Line))

    val graphMouse = new DefaultModalGraphMouse[EipComponent, String]()
    viewer.setGraphMouse(graphMouse)

    graphMouse.setMode(ModalGraphMouse.Mode.PICKING)

    val component = new GraphZoomScrollPane(viewer)*/

    val jframe = new JFrame()
    jframe.setSize(500, 700)
    jframe.getContentPane.add(component)
    jframe.setVisible(true)
    jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  }
}


