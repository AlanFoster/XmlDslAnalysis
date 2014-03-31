package foo.tooling.graphing.strategies.icons

import javax.swing.{ImageIcon, Icon}

/**
 * Represents the trait which to load icons from from the default classloader.
 */
trait DefaultIconLoader extends IconLoader {
  /**
   * Loads the required icon
   * @param path The path for the icon
   * @return The loaded icon
   */
  def load(path: String): Icon = {
    val resource = getClass.getResource(path)
    new ImageIcon(resource)
  }
}