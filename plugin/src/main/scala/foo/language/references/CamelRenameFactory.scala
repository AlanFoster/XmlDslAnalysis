package foo.language.references

import com.intellij.psi.{PsiFileFactory, PsiElement}
import foo.language.Core.CamelLanguage

/**
 * Helper methods to create common elements when the user is renaming elements
 */
object CamelRenameFactory {
  def getIdentifierRename(existing: PsiElement, name: String): PsiElement = {
    val project = existing.getProject
    val tempFileName = "__" + existing.getContainingFile.getName
    val text = "${" + name + "}"
    val newFile = PsiFileFactory.getInstance(project).createFileFromText(tempFileName, CamelLanguage, text)

    val replacement = newFile.findElementAt(2)

    replacement
  }
}
