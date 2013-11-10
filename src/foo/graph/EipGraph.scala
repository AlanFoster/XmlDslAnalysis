package foo.graph

import foo.Model._
import edu.uci.ics.jung.graph.{Graph, DirectedSparseMultigraph}
import edu.uci.ics.jung.visualization.{GraphZoomScrollPane, VisualizationViewer}
import java.awt.{Color, Shape}
import edu.uci.ics.jung.algorithms.layout.TreeLayout
import edu.uci.ics.jung.graph.util.EdgeType
import org.apache.commons.collections15.Transformer
import javax.swing._
import java.awt.geom.Rectangle2D
import edu.uci.ics.jung.visualization.decorators.EdgeShape
import edu.uci.ics.jung.visualization.control.{ModalGraphMouse, DefaultModalGraphMouse}
import java.awt.event.{KeyEvent, KeyListener}

import foo.FunctionalUtil._
import foo.graph.loaders.{DefaultIconLoader, IconLoader}
import scala.collection.JavaConverters._

/*
case class Route(id: String, pointer: Pointer)
case class Pointer(component: Component, children: List[Pointer] = List())
case class Component(id: String, eipType: String, uri: String)
*/

abstract class EipGraph(blueprint: Blueprint) extends IconLoader {
  /**
   * Create a type alias for VisualizationViewer to improve code readability
   */
  type Viewer = VisualizationViewer[Component, String]

  type DAG = Graph[Component, String]

  def pipeLineChildren[V, E](children: List[V], graph: Graph[V, E])(f: V => E): Boolean = children match {
    case Nil => true
    case x :: Nil => true
    case x :: x2 :: xs => {
      graph.addEdge(f(x), x, x2, EdgeType.DIRECTED)
      pipeLineChildren(children.tail, graph)(f)
    }
  }

  def createVertex(parent: Component, children: List[Component], graph: DAG) {
    graph.addVertex(parent)
    for { child <- children } {
      createVertex(child, List(), graph)
      pipeLineChildren(children, graph)(_.toString)
    }
    (parent, children) match {
      case (from: FromComponent, x :: _) => {
        graph.addEdge(from.toString, from, x, EdgeType.DIRECTED)
      }
      case _ => ()
    }
  }

  def createViewer = {
    val graph:DAG= new DirectedSparseMultigraph[Component, String]

    val routes = blueprint.getCamelContext.getRoutes.asScala
    val parent = routes.head.getFrom
    val components = routes.head.getComponents.asScala.toList

    createVertex(parent, components, graph)

/*

    def multiCastChildren(parent: Pointer, children: List[Pointer]): Boolean = children match {
      case Nil => true
      case x :: xs => {
        graph.addEdge(x.hashCode().toString, parent.component, x.component, EdgeType.DIRECTED)
        multiCastChildren(parent, xs)
      }
    }
*/

    val minimumSpanningForest = GraphGlue.newMinimumSpanningForest(graph)
    val viewer = new VisualizationViewer(new TreeLayout(minimumSpanningForest.getForest, 100, 100))
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

  def getEipType(c:Component) = c match {
    case from: FromComponent => "from"
    case to: ToComponent => "to"
    case inOut: InOutComponent => "to"
    case setBody: SetBodyComponent => "translator"
    case _ => "to"
  }

  /**
   * Binds the EIP renderer to the given Viewer
   * @param viewer The viewer that will render EIP icons
   * @return The Viewer
   */
  private def bindEipRenderer(viewer:Viewer): Viewer = {
    def getIcon(component: Component): Icon = {
      val isPicked = viewer.getPickedVertexState.getPicked.contains(component)
      val componentType = getEipType(component)
      if (isPicked) loadPickedIcon(componentType) // component.eipType
      else loadUnpickedIcon(componentType)
    }

    viewer.getRenderContext.setVertexIconTransformer(new Transformer[Component, Icon] {
      override def transform(component: Component): Icon = getIcon(component)
    })

    viewer.getRenderContext.setVertexShapeTransformer(new Transformer[Component, Shape] {
      def transform(component: Component): Shape = {
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
      _.setVertexToolTipTransformer(new Transformer[Component, String] {
        def transform(component: Component): String = component.getId + " " + component.getUri
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
    val graphMouse = new DefaultModalGraphMouse[Component, String]()
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

object Starter {
  def main(args: Array[String]) {
    val component = (new EipGraph(null) with DefaultIconLoader).createScrollableViewer

    val jframe = new JFrame()
    jframe.setSize(500, 700)
    jframe.getContentPane.add(component)
    jframe.setVisible(true)
    jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  }
}
