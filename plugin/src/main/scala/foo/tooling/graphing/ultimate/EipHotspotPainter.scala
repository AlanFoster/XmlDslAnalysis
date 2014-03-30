package foo.tooling.graphing.ultimate

import com.intellij.openapi.graph.view.{NodeRealizer, GenericNodeRealizer}
import java.awt.Graphics2D
import a.j.md.m_
import a.j.{md, ld}
import com.intellij.openapi.graph.impl.GraphBase

/**
 * A hotspot painter draws the 'handles' on a given node.
 * IE When a node is selected, the hotspot handles may be drawn.
 */
class EipHotspotPainter extends GenericNodeRealizer.HotSpotPainter {
  /**
   * By default this implementation is NOOP
   * @param noderealizer
   * @param graphics2d
   */
  override def paintHotSpots(noderealizer: NodeRealizer, graphics2d: Graphics2D): Unit = {
    // noop
  }
}

/**
 * Object helper methods
 */
object EipHotspotPainter {
  /**
   * Creates a new IJ/YFiles implementation of a HotspotPainter
   */
  def createWrapper(wrappedInstance: GenericNodeRealizer.HotSpotPainter) = {
    new m_ {
      override def a(ld1: ld, graphics2d: Graphics2D): Unit = {
        wrappedInstance.paintHotSpots(null, graphics2d)
      }
    }
  }
}