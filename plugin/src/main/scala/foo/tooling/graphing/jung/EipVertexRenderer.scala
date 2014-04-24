package foo.tooling.graphing.jung

import edu.uci.ics.jung.visualization.renderers.BasicVertexRenderer
import edu.uci.ics.jung.visualization.{Layer, RenderContext}
import edu.uci.ics.jung.algorithms.layout.Layout
import foo.tooling.graphing.EipProcessor
import java.awt.geom.AffineTransform
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator
import javax.swing.{JComponent, CellRendererPane}

/**
 * Concrete implementation of an Eip Vertex Renderer for JUNG
 */
class EipVertexRenderer(vertexLoader: EipProcessor => JComponent) extends BasicVertexRenderer[EipProcessor, String] {

  /**
   * @inheritdoc
   */
  override def paintVertex(rc: RenderContext[EipProcessor, String],
                           layout: Layout[EipProcessor, String],
                           v: EipProcessor): Unit = {
    // Perform the transformation + layout positioning
    val center = layout.transform(v)
    val transformedCenter = rc.getMultiLayerTransformer.transform(Layer.LAYOUT, center)
    val (x, y) = (transformedCenter.getX.toInt, transformedCenter.getY.toInt)

    val renderShape = rc.getVertexShapeTransformer.transform(v)

    val affineTransform = AffineTransform.getTranslateInstance(x, y)
    val transformedShape = affineTransform.createTransformedShape(renderShape)

    // Culling - Only draw the node, if the vertex is hit
    if (vertexHit(rc, transformedShape)) {
      val graphicsContext = rc.getGraphicsContext
      val renderingPlane = rc.getRendererPane

      drawNode(v, graphicsContext, renderingPlane, x, y)
    }
  }

  /**
   * Draws the given node, relative to the given X, Y coordinates.
   * It is assumed that these X,Y coordinates have been transformed appropriately.
   */
  def drawNode(v: EipProcessor, graphicsContext: GraphicsDecorator,
               renderingPlane: CellRendererPane,
               x: Int, y: Int) {
    val nodeComponent = vertexLoader(v)
    val (width, height) = {
      val preferredSize = nodeComponent.getPreferredSize
      (preferredSize.getWidth.toInt, preferredSize.getHeight.toInt)
    }

    graphicsContext.draw(nodeComponent, renderingPlane,
      x - (width / 2), y - (height / 2),
      width, height,
      true)
  }
}