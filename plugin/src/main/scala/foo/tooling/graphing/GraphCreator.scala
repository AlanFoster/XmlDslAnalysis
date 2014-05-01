package foo.tooling.graphing

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import foo.tooling.graphing.StaticGraphTypes._
import javax.swing.JComponent

/**
 * Represents a trait which has the ability to create a representation of the given
 * EipDag information.
 */
trait GraphCreator {
  /**
   * The 'pretty name' associated with this GraphCreator, IE a human readable string.
   */
  val prettyName: String

  /**
   * Creates a new instance of a graph component for the given files
   * @param project The associated project
   * @param file The virtual file associated with the given EIP Graph
   * @param eipGraph A graph containing the available semantic information
   * @return A newly created instance of a graph component
   */
  def createComponent(project: Project, file: VirtualFile, eipGraph: EipDAG): JComponent
}
