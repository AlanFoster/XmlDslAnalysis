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
   * Attempts to return the associated BlueprintDomDescription with the given virtual file
   * @param project The project
   * @param virtualFile The current VirtualFile
   * @return None if the given VirtualFile is not the expected DOM file, otherwise the Some(DomFile)
   */
  def getBlueprintDomFile(project: Project, virtualFile: VirtualFile): Option[Blueprint] =
    if(virtualFile.getFileType != StdFileTypes.XML) None
    else {
      // Extract the given XML file from the VirtualFile
      val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
      val xmlFile = psiFile.asInstanceOf[XmlFile]
      getDomFile(project, xmlFile, classOf[Blueprint])
    }

  /**
   * Attempts to return the associated BlueprintDomDescription with the given xml file file
   * @param project The project
   * @param xmlFile The given xmlFile
   * @return None if the given VirtualFile is not the expected DOM file, otherwise the Some(DomFile)
   */
  def getBlueprintDomFile(project: Project, xmlFile: XmlFile): Option[Blueprint] =
    getDomFile(project, xmlFile, classOf[Blueprint])

  /**
   * Attempts to return the associated DomDescription with the virtual file.
   *
   * @param project The project
   * @param xmlFile The target xml file
   * @tparam T The root element T
   * @return An Option[T] which may contain the required U
   */
  def getDomFile[T <: DomElement](project: Project, xmlFile: XmlFile, clazz: Class[T]): Option[T] =
      Option(DomManager.getDomManager(project).getFileElement(xmlFile, clazz))
        .map(_.getRootElement)
}
