package foo.language.references

import com.intellij.psi.{PsiFileFactory, PsiElement}
import foo.language.Core.{CamelPsiFile, CamelLanguage}
import com.intellij.openapi.util.TextRange
import com.intellij.psi.util.PsiTreeUtil
import foo.language.generated.psi.CamelCamelFuncBody

/**
 * Helper methods to create common elements when the user is renaming elements
 */
object CamelRenameFactory {
  def getIdentifierRename(existing: PsiElement, name: String): PsiElement = {
    val text = wrapSimpleFunction(name)

    val newFile = createTempFile(existing, text)
    val replacement = newFile.findElementAt(2)

    replacement
  }

  def getMethodRename(existing: PsiElement, rangeInElement: TextRange, newName : String): PsiElement = {
    val replacementText = rangeInElement.replace(existing.getText, newName)
    val simpleText = wrapSimpleFunction(replacementText)
    val newFile = createTempFile(existing, simpleText)

    val replacement = PsiTreeUtil.findElementOfClassAtOffset(newFile, 2, classOf[CamelCamelFuncBody], false)
    replacement
  }

  private def createTempFile(element: PsiElement, text: String): CamelPsiFile = {
    val project = element.getProject
    val tempFileName = "__" + element.getContainingFile.getName
    val newFile =
      PsiFileFactory.getInstance(project)
        .createFileFromText(tempFileName, CamelLanguage, text)
        .asInstanceOf[CamelPsiFile]

    newFile
  }

  private def wrapSimpleFunction(expression: String) = "${" + expression + "}"

}
