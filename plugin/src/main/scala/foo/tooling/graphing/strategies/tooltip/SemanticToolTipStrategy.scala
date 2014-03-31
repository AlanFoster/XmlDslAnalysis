package foo.tooling.graphing.strategies.tooltip

import foo.tooling.graphing.EipProcessor

/**
 * Represents a concrete implementation which can provide a human readable definition of a given
 * EipProcessor instance
 */
class SemanticToolTipStrategy extends ToolTipStrategy {
  /**
   * Returns the human readable string associated with this processor
   * @param component The given EIP Processor component
   * @return A human readable string
   */
  override def createTooltip(component: EipProcessor): String =
  // Concatenate the type information and EipComponent's specific text  value
    s"""<html>
            |${component.text}<br />
            |Input Body Types: ${component.processor.bodies.map(_.toList.sortBy(identity).mkString("{", ", ", "}")).getOrElse("{}")}<br />
            |Output Body Types: ${component.processor.outBodies.map(_.toList.sortBy(identity).mkString("{", ", ", "}")).getOrElse("{}")}<br />
            |Input Headers: ${component.processor.headers.map(_.keys.toList.sortBy(identity).mkString("{", ", ", "}")).getOrElse("{}")}<br />
            |Output Headers: ${component.processor.outHeaders.map(_.keys.toList.sortBy(identity).mkString("{", ", ", "}")).getOrElse("{}")}<br />
            |</html>""".stripMargin
}
