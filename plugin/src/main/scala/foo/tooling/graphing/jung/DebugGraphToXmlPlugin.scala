package foo.tooling.graphing.jung

import foo.tooling.graphing.StaticGraphTypes._
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin
import java.awt.event.{ActionEvent, MouseEvent, MouseListener}
import javax.swing.{AbstractAction, JPopupMenu, SwingUtilities}
import java.awt.Component
import foo.tooling.serializers.CompleteEipDagSerializer

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
        println(new CompleteEipDagSerializer().serialize(eipDag))
      }
    })
    popUp.show(viewer, e.getX, e.getY)
  }
}
