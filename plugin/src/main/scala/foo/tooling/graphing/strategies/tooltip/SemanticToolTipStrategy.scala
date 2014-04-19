package foo.tooling.graphing.strategies.tooltip

import foo.tooling.graphing.EipProcessor
import foo.intermediaterepresentation.model.types.CamelStaticTypes.ACSLFqcn
import org.apache.commons.lang.StringEscapeUtils

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
  override def createTooltip(component: EipProcessor): String = {
    val componentName = component.processor.prettyName
    // Concatenate the type information and EipComponent's specific text  value
    s"""<html>
            |<u>${componentName}</u> ${sanitize(component.text)}<br />
            |Input Body Types: ${formatBodies(component.processor.bodies)}<br />
            |Output Body Types: ${formatBodies(component.processor.outBodies)}<br />
            |Input Headers: ${formatHeaders(component.processor.headers)}<br />
            |Output Headers: ${formatHeaders(component.processor.outHeaders)}<br />
            |</html>""".stripMargin
  }

  /**
   * Sanitizes text, performing entity encoding etc
   */
  private def sanitize(text:String) = {
    StringEscapeUtils.escapeXml(text)
  }

  def formatBodies(option: Option[Set[ACSLFqcn]]) =
    option.map(_.toList.map(sanitize).sortBy(identity).mkString("{", ", ", "}")).getOrElse("{}")

  def formatHeaders[U](option: Option[Map[ACSLFqcn, (ACSLFqcn, _)]]) =
    option.map(_.map({
      case (key, (fqcn, _)) =>
        s"${sanitize(key)}: ${sanitize(fqcn)}"
    }).toList.sortBy(identity).mkString("{", ", ", "}")).getOrElse("{}")
}
