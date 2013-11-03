package foo.editor

import javax.swing.{ImageIcon, Icon}
import com.intellij.openapi.util.IconLoader

trait IconLoader {
  def load(s: String): Icon

  def loadPickedIcon(s: String): Icon = load(s"/eip/picked/${s}.gif")
  def loadUnpickedIcon(s: String): Icon = load(s"/eip/unpicked/${s}.gif")
}

trait IntellijIconLoader extends IconLoader {
  def load(s: String): Icon = IconLoader.getIcon(s)
}

trait DefaultIconLoader extends IconLoader {
  def load(s: String): Icon = {
    val resource = s.getClass.getResource(s)
    new ImageIcon(resource)
  }
}