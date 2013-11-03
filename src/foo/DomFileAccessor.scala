package foo

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.fileTypes.{LanguageFileType, StdFileTypes}
import com.intellij.util.xml.{DomManager, DomFileDescription}
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlFile

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
  def getBlueprintDomFile(project: Project, virtualFile: VirtualFile) =
    getDomFile[BlueprintDomFile](project, virtualFile)

  /**
   * Attempts to return the associated DomDescription with the virtual file.
   *
   * @param project
   * @param virtualFile
   * @tparam T A dom description of type T
   * @return An Option[T] which may contain the required T
   */
  def getDomFile[T <: DomFileDescription[_]](project: Project, virtualFile: VirtualFile): Option[T] =
    if(virtualFile.getFileType !=  StdFileTypes.XML) None
    else {
      val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
      val xmlFile = psiFile.asInstanceOf[XmlFile]

      val domDescription = DomManager.getDomManager(project).getDomFileDescription(xmlFile)
      domDescription match {
        case x: T => Option(x)
        case _ => None
      }
    }
}
