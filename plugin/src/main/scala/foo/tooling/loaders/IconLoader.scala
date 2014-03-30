package foo.tooling.loaders

import javax.swing.Icon

/**
 * Represents a trait which can load picked and unpicked icons.
 * The implementation of load should be provided by the implementing trait.
 */
trait IconLoader {
  /**
   * Loads the required icon
   * @param path The path for the icon
   * @return The loaded icon
   */
  def load(path: String): Icon

  /**
   * Loads the required picked icon, where picked means selected/highlighted
   * @param name The name of the icon
   * @return The required icon
   */
  def loadPickedIcon(name: String): Icon = load(s"/eip/picked/${name}.gif")

  /**
   * Loads the required unpicked icon, where unpicked means not selected/highlighted
   * @param name The name of the icon
   * @return The required icon
   */
  def loadUnpickedIcon(name: String): Icon = load(s"/eip/unpicked/${name}.gif")
}