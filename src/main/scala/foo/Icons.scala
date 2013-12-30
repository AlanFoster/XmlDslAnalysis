package foo

import com.intellij.openapi.util.IconLoader

/**
 * Initial plugin icons
 */
object Icons {
  final val CamelString = "/Camel_16x16.png"
  def Camel = IconLoader.getIcon(CamelString)
}
