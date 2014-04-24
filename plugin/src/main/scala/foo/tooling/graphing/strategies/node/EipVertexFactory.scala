package foo.tooling.graphing.strategies.node

import foo.tooling.graphing.EipProcessor
import javax.swing.JComponent

/**
 * A trait which represents the ability to produce a vertex which can be used within
 * an EIP graph
 */
trait EipVertexFactory {
  /**
   * Creates the appropriate vertex which can be rendered within an Eip Graph
   * @param processor The EipProcessor to visualize
   * @param isSelected A flag which indicates if this node is selected or not.
   *                   IE True if this node is selected, false otherwise
   * @return A visual representation of the given processor
   */
  def create(processor: EipProcessor, isSelected: Boolean): JComponent
}


