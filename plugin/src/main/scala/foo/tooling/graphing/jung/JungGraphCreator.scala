package foo.tooling.graphing.jung

import foo.tooling.graphing.{StaticGraphTypes, GraphCreator}
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.JComponent
import foo.tooling.loaders.IntellijIconLoader

class JungGraphCreator extends GraphCreator {
  override val prettyName: String = "Eip Graph (JUNG)"

  override def createComponent(project: Project, file: VirtualFile, eipGraph: StaticGraphTypes.EipDAG): JComponent = {
    val visualEipGraph = (new VisualEipGraph(eipGraph) with IntellijIconLoader).createScrollableViewer
    visualEipGraph
  }
}
