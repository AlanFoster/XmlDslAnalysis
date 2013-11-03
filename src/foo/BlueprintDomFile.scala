package foo

import com.intellij.util.xml.DomFileDescription
import foo.Model.Blueprint
import javax.swing.Icon

/**
 * Defines the BlueprintDom file description for the Blueprint root node.
 */
class BlueprintDomFile extends DomFileDescription[Blueprint](classOf[Blueprint], "blueprint") {
  /**
   * @param flags
   * @return The blueprint icon if this file description is met
   */
   override def getFileIcon(flags: Int): Icon = Icons.Camel
}
