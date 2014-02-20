package foo.dom.Model

import com.intellij.util.xml.{SubTagList, SubTag, DomElement}
import org.jetbrains.annotations.NotNull

/**
 * Blueprint root element.
 * Note the actual logic for registering the dom model is part of {@link BlueprintDom}
 * and registered as usual under the plugin.xml extensions
 */
trait Blueprint extends DomElement {
  @SubTag("camelContext")
  def getCamelContext: CamelContext

  @NotNull
  @SubTagList(value = "bean")
  def getBlueprintBeans: java.util.List[BlueprintBean]
}
