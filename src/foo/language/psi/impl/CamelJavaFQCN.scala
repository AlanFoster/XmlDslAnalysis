package foo.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import foo.language.psi.ICamelJavaFQCN
import com.intellij.lang.ASTNode
import com.intellij.psi._
import foo.language.references.CamelFQCNReference

class CamelJavaFQCN(node: ASTNode) extends ASTWrapperPsiElement(node) with ICamelJavaFQCN  {
  override def getReference: PsiReference = new CamelFQCNReference(this)

  def setName(name: String): PsiElement = ???
  def getNameIdentifier: PsiElement = this
}
