package foo.language.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.PsiReference
import foo.language.psi.IACamelVariableAccess
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry

class ACamelVariableAccessImpl(node: ASTNode) extends ASTWrapperPsiElement(node) with IACamelVariableAccess  {
  override def getReferences: Array[PsiReference] = {
    ReferenceProvidersRegistry.getReferencesFromProviders(this)
  }
/*
  override def getName: String = toString
  override def getNameIdentifier: PsiElement = this
  override def setName(name: String): PsiElement = this*/
}
