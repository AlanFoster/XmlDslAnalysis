package foo.tooling.graphing.jung

import foo.tooling.graphing.{StaticGraphTypes, GraphCreator}
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.JComponent
import foo.tooling.graphing.strategies.icons.{EipIconLoader, IconLoader, IntellijIconLoader}
import foo.tooling.graphing.strategies.tooltip.ToolTipStrategy
import foo.tooling.graphing.strategies.node.EipVertexFactory

/**
 * A concrete implementation of the GraphCreator trait which interacts
 * with the JUNG graphing library.
 *
 * {@link foo.tooling.graphing.GraphCreator}
 *
 * @param eipVertexFactory Provide access to an EIP Icon loader strategy implementation
 * @param tooltipStrategy Provide access to a tooltip strategy implementation
 */
class JungGraphCreator(eipVertexFactory: EipVertexFactory, val tooltipStrategy: ToolTipStrategy) extends GraphCreator {
  /**
   * {@inheritdoc}
   */
  override val prettyName: String = "Eip Graph (JUNG)"

  /**
   * {@inheritdoc}
   */
  override def createComponent(project: Project, file: VirtualFile, eipGraph: StaticGraphTypes.EipDAG): JComponent = {
    val visualEipGraph = new VisualEipGraph(eipGraph, eipVertexFactory, tooltipStrategy).createScrollableViewer
    visualEipGraph
  }
}
