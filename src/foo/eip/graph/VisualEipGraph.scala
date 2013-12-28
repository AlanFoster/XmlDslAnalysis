package foo.eip.graph

import edu.uci.ics.jung.visualization.{VisualizationModel, DefaultVisualizationModel, GraphZoomScrollPane, VisualizationViewer}
import java.awt._
import org.apache.commons.collections15.Transformer
import javax.swing._
import java.awt.geom.{AffineTransform, Rectangle2D}
import edu.uci.ics.jung.visualization.decorators.EdgeShape
import edu.uci.ics.jung.visualization.control._
import java.awt.event._

import org.apache.commons.collections15.Factory

import foo.FunctionalUtil._
import foo.eip.graph.loaders.{DefaultIconLoader, IconLoader}
import foo.eip.graph.Visualisation.EipGraphVisualisation
import foo.eip.graph.StaticGraphTypes.EipDAG
import foo.eip.serializers.{TypeEipDagSerializer, EipDagSerializer}
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import edu.uci.ics.jung.graph.util.{TreeUtils, EdgeType}
import foo.eip.graph.ADT.EmptyDAG
import scala.List
import fr.inria.zvtm.engine.{View, VirtualSpace, VirtualSpaceManager}
import fr.inria.zvtm.glyphs.{VImage, VRectangle, VCircle}
import fr.inria.zvtm.engine.portals.{OverviewPortal, CameraPortal}
import com.intellij.openapi.vcs.changes.dbCommitted.VcsSqliteLayer
import edu.uci.ics.jung.io.GraphMLWriter
import edu.uci.ics.jung.samples.{GraphZoomScrollPaneDemo, SatelliteViewDemo}
import edu.uci.ics.jung.algorithms.layout.{StaticLayout, TreeLayout}
import javax.swing.border.LineBorder
import foo.eip.graph.ADT.EmptyDAG
import foo.eip.graph.ADT.EmptyDAG

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
  def createViewer(model: VisualizationModel[EipComponent, String]): Viewer = {
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


  def createSatelliteViewer(parent: Viewer, model: VisualizationModel[EipComponent, String]): Viewer = {
    val viewer = new SatelliteVisualizationViewer[EipComponent, String](parent, new Dimension(200, 200))

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

/*    val roots = TreeUtils.getRoots(minimumSpanningForest.getForest)
    for {
      root <- roots
    } {
    }*/

    val treeLayout = new TreeLayout(minimumSpanningForest.getForest, 100, 100)
    val staticLayout = new StaticLayout(graph, treeLayout)

    val visualisationModel = new DefaultVisualizationModel(staticLayout)

    val mainViewer = createViewer(visualisationModel)
    val mainZoomPane = new GraphZoomScrollPane(mainViewer)

    val satelliteViewer = createSatelliteViewer(mainViewer, visualisationModel)
    satelliteViewer.setBorder(LineBorder.createBlackLineBorder())

    mainViewer.setLayout(new BorderLayout())


    // Help Window
/*    val helpLayer = new JLayeredPane

    val helpPanel = new JPanel()
    helpPanel.setLayout(new FlowLayout())
    helpPanel.setBackground(Color.WHITE)
    helpPanel.add(new JLabel("howdy!"))

    helpLayer.setLayout(new FlowLayout(FlowLayout.CENTER))
    helpLayer.add(helpPanel, BorderLayout.CENTER)

    mainViewer.add(helpLayer, BorderLayout.CENTER)*/

    // settings button handling
/*    val button = new JButton(load("/eip/settings-32.png"))
    button.setBorder(BorderFactory.createEmptyBorder())
    button.setBackground(Color.WHITE)
    button.setContentAreaFilled(false)
    button.addActionListener (new ActionListener {
      def actionPerformed(e: ActionEvent): Unit = {
        helpLayer.setVisible(true)
      }
    })*/

/*    val settingsLayer = new JLayeredPane
    settingsLayer.setLayout(new FlowLayout(FlowLayout.RIGHT))
    settingsLayer.add(button, BorderLayout.LINE_END)
    mainViewer.add(settingsLayer, BorderLayout.NORTH)*/


    // Satellite View
    val rightHandSideBoxLayer = new JLayeredPane
    rightHandSideBoxLayer.setLayout(new FlowLayout(FlowLayout.RIGHT))
    rightHandSideBoxLayer.add(satelliteViewer, BorderLayout.LINE_END)

    mainViewer.add(rightHandSideBoxLayer, BorderLayout.SOUTH)

    mainZoomPane
  }


  /**
   * Binds the EIP renderer to the given Viewer
   * @param viewer The viewer that will render EIP icons
   * @return The Viewer
   */
  private def bindEipRenderer(viewer: Viewer): Viewer = {
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
   * Override the default implementation of mousePressed, which relies on e.isPopupTrigger.
   * This method instead uses swing utilities to ensure the mouse event is a right click.
   * @param e The mouse event
   */
  override def mousePressed(e: MouseEvent) {
    if (SwingUtilities.isRightMouseButton(e)) {
      handlePopup(e)
      e.consume
    }
  }

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


/*    SatelliteViewDemo.main(Array())*/

    val jframe = new JFrame()
    jframe.setSize(500, 700)


  //  jframe.getContentPane.add(ztvm())
    jframe.getContentPane.add(JUNG())


    jframe.setVisible(true)
    jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  }

  def ztvm() = {



    // Access the virtual space manager which represents a singleton
    val virtualSpaceManager = VirtualSpaceManager.INSTANCE

    // Create our new virtual space, and register our camera requirements
    val eipGraph = "EipGraph"
    val virtualSpace = virtualSpaceManager.addVirtualSpace(eipGraph)


/*
    val circle = new VCircle(0, 0, 0, 10, Color.GREEN)
    virtualSpace.addGlyph(circle)
*/

    val circle2 = new VCircle(900, 900, 0, 10, Color.RED)
    virtualSpace.addGlyph(circle2)


    val square = new VImage(javax.imageio.ImageIO.read(getClass.getResourceAsStream("/eip/picked/to.gif")))
    square.setDrawBorder(false)
    virtualSpace.addGlyph(square)



/*    val square = new VImage[]()(300, 300, 0, 100, 100, Color.BLUE) {
      override def draw(g: Graphics2D, vW: Int, vH: Int, i: Int, stdS: Stroke, stdT: AffineTransform, dx: Int, dy: Int): Unit = {

        val image = javax.imageio.ImageIO.read(getClass.getResourceAsStream("/eip/picked/to.gif"))

        g.drawImage(image, dx + pc[0], dy + pc[0], null)
        super.draw(g, vW, vH, i, stdS, stdT, dx, dy)
      }
    }*/
/*    virtualSpace.addGlyph(square)*/

    // Create our Cameras
    val mainCamera = virtualSpace.addCamera()
    val portalCamera = virtualSpace.addCamera()


    {
      import scala.collection.JavaConverters._

      val cameras = List(mainCamera, portalCamera).asJava

      val view = virtualSpaceManager.addFrameView(cameras, eipGraph, View.OPENGL_VIEW, 800, 600, false)
      view.setBackgroundColor(Color.WHITE)
      val portal = new OverviewPortal(0, 0, 100, 50, portalCamera, mainCamera)
      virtualSpaceManager.addPortal(portal, view)

      view.getPanel.getComponent
    }
  }



  def JUNG() = {
    import EipGraphCreator._

    val from = EipComponent("from", "from", "from", CamelType(), null)
    val choice = EipComponent("choice", "choice", "choice", CamelType(), null)

    val when1 = EipComponent("when1", "when", "when1", CamelType(), null)
    val foo = EipComponent("foo", "to", "foo", CamelType(), null)

    val when2 = EipComponent("when2", "when", "when2", CamelType(), null)
    val bar = EipComponent("bar", "to", "bar", CamelType(), null)

    val afterChoice = EipComponent("afterChoice", "to", "afterChoice", CamelType(), null)

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


    component
  }

}


