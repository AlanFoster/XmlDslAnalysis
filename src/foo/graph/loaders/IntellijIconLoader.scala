package foo.graph.loaders

import javax.swing.Icon
import com.intellij.openapi.util.IconLoader

/**
 * Represents the trait which will load icons correctly from IntelliJ's classpath
 */
trait IntellijIconLoader extends IconLoader {
  /**
   * Loads the required icon
   * @param path The path for the icon
   * @return The loaded icon
   */
  def load(path: String): Icon = IconLoader.getIcon(path)
}

