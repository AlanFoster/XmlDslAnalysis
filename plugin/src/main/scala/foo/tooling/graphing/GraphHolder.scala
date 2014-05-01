package foo.tooling.graphing

import com.intellij.openapi.extensions.Extensions

/**
 * Contains all graph rendering engines which can be used by this plugin
 */
trait GraphHolder {
  /**
   * Access all graphs
   * @return The list of registered graphs that can be used by this plugin
   */
  def graphs: List[VisualEipGraphFactory]
}

/**
 * Concrete implementation of a graph holder that uses those graphs registered via plugin.xml
 */
class ExtensionPointGraphHolder() extends GraphHolder {
  /**
   * Access all graphs
   * @return The list of registered graphs that can be used by this plugin
   */
  def graphs: List[VisualEipGraphFactory] = {
    val extensionPoint = Extensions.getRootArea.getExtensionPoint("foo.initial.eipGraphingLibrary")
    extensionPoint.getExtensions.toList
  }
}
