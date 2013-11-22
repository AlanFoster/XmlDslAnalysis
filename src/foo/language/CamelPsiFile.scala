package foo.language

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider
import foo.language.Core.{LanguageConstants, CamelFileType, CamelLanguage}
import com.intellij.openapi.fileTypes.FileType
import javax.swing.Icon
import com.intellij.psi.tree.IFileElementType

/**
 * This CamePsiFile (Program Structure Interface) extends the base PsiFileBase class.
 * Within the context of IntelliJ's parsing lifecycle; A file will be parsed first into
 * a set of ASTNodes, and each ASTNodes will be associated with an IElementType implementation.
 *
 * The root node will need to have a specific IElementType identifier, which comes in the form
 * of the IFileElementType
 */
class CamelPsiFile(viewProvider: FileViewProvider) extends PsiFileBase(viewProvider, CamelLanguage)  {
  /**
   *
   * @return The associated filetype with the CamelPsiFile
   */
  def getFileType: FileType = CamelFileType

  override def getIcon(flags: Int): Icon = LanguageConstants.icon

  override def toString: String = s"{CamelPsiFile ${super.toString}}"
}
