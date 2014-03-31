package foo.tooling.graphing

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import foo.tooling.graphing.StaticGraphTypes._
import javax.swing.JComponent

trait GraphCreator {
  val prettyName: String
  def createComponent(project: Project, file: VirtualFile, eipGraph: EipDAG): JComponent
}
