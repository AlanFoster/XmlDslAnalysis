package foo.editor

import foo.Model.Blueprint
import edu.uci.ics.jung.graph.{Graph, DirectedSparseMultigraph}
import edu.uci.ics.jung.visualization.{BasicVisualizationServer, VisualizationViewer}
import java.awt.{Color, Shape, Dimension}
import edu.uci.ics.jung.algorithms.layout.TreeLayout
import edu.uci.ics.jung.graph.util.EdgeType
import org.apache.commons.collections15.Transformer
import javax.swing._
import java.awt.geom.Rectangle2D
import edu.uci.ics.jung.visualization.decorators.EdgeShape
import edu.uci.ics.jung.visualization.control.{ModalGraphMouse, DefaultModalGraphMouse}
import com.intellij.openapi.util.IconLoader
import foo.editor.Route
import foo.editor.Component
import foo.editor.Pointer
import scala.swing.BoxPanel
import java.awt.event.{KeyEvent, KeyListener}

import foo.FunctionalUtil._

case class Route(id: String, pointer: Pointer)
case class Pointer(component: Component, children: List[Pointer] = List())
case class Component(id: String, eipType: String, uri: String)

class EipGraph(domModel: Blueprint, loadPickedIcon: String => Icon, loadUnpickedIcon: String => Icon) {
  /**
   * Create a type alias for VisualizationViewer to improve code readibility
   */
  type Viewer = VisualizationViewer[Component, String]

  def createComponent = {
    val graph:Graph[Component, String] = new DirectedSparseMultigraph[Component, String]

      val route = Route("route", Pointer(null, List(
        Pointer(Component("1", "from", "uri")),
        Pointer(Component("2", "to", "uri")),
        Pointer(Component("3", "to", "uri")),
        Pointer(Component("4", "to", "uri")),
        Pointer(Component("5", "to", "uri")),
        Pointer(Component("6", "to", "uri"))
      )))

    def pipeLineChildren(children: List[Pointer]): Boolean = children match {
      case Nil => true
      case x :: Nil => true
      case x :: x2 :: xs => {
        graph.addEdge(x.hashCode().toString, x.component, x2.component, EdgeType.DIRECTED)
        pipeLineChildren(children.tail)
      }
    }

    def multiCastChildren(parent: Pointer, children: List[Pointer]): Boolean = children match {
      case Nil => true
      case x :: xs => {
        graph.addEdge(x.hashCode().toString, parent.component, x.component, EdgeType.DIRECTED)
        multiCastChildren(parent, xs)
      }
    }

    def createTree(p: Pointer) {
      if (p.component != null) {
        graph.addVertex(p.component)
      }

      pipeLineChildren(p.children)
      p.children.foreach(createTree)
    }

    createTree(route.pointer)

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
   * Binds the EIP renderer to the given Viewer
   * @param viewer The viewer that will render EIP icons
   * @return The Viewer
   */
  private def bindEipRenderer(viewer:Viewer): Viewer = {
    def getIcon(component: Component): Icon = {
      val isPicked = viewer.getPickedVertexState.getPicked.contains(component)
      if (isPicked) loadPickedIcon(component.eipType)
      else loadUnpickedIcon(component.eipType)
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
        def transform(component: Component): String = component.id + " " + component.uri
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
    def load: String => Icon = s => {
      val resource = s.getClass.getResource(s)
      new ImageIcon(resource)
    }

    val component = new EipGraph(
      null,
      loadPickedIcon = s => load(s"/eip/picked/${s}.gif"),
      loadUnpickedIcon = s => load(s"/eip/unpicked/${s}.gif")
    ).createComponent

    val jframe = new JFrame()
    jframe.setSize(500, 700)
    jframe.getContentPane.add(component)
    jframe.setVisible(true)
    jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  }
}
