package foo.tooling.graphing.strategies.tooltip

import foo.tooling.graphing.EipProcessor

/**
 * Represents a trait which can provide a human readable definition of a given
 * EipProcessor instance
 */
trait ToolTipStrategy {
  /**
   * Returns the human readable string associated with this processor
   * @param processor The given EIP Processor
   * @return A human readable string
   */
  def createTooltip(processor: EipProcessor): String
}
