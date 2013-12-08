package foo

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.fileTypes.StdFileTypes
import com.intellij.util.xml.{DomElement, DomManager}
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlFile
import foo.Model.Blueprint

/**
 * Represents functions which are useful to gain access to core DomFile
 * classes.
 */
object DomFileAccessor {
  /**
   * Attempts to return the associated BlueprintDomDescription with the given virtualfile
   * @param project
   * @param virtualFile
   * @return
   */
  def getBlueprintDomFile(project: Project, virtualFile: VirtualFile): Option[Blueprint] =
    getDomFile(project, virtualFile, classOf[Blueprint])

  /**
   * Attempts to return the associated DomDescription with the virtual file.
   *
   * @param project
   * @param virtualFile
   * @tparam T The root element T
   * @return An Option[T] which may contain the required U
   */
  def getDomFile[T <: DomElement](project: Project, virtualFile: VirtualFile, clazz: Class[T]): Option[T] =
    if(virtualFile.getFileType != StdFileTypes.XML) None
    else {
      val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
      val xmlFile = psiFile.asInstanceOf[XmlFile]


      Some(DomManager.getDomManager(project).getFileElement(xmlFile, clazz))
        .map(_.getRootElement)
    }
}
