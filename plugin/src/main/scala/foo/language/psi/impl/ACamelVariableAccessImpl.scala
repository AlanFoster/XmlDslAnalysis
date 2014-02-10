package foo.language.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.PsiReference

class ACamelVariableAccessImpl(node: ASTNode) extends ASTWrapperPsiElement(node) {
  override def getReferences: Array[PsiReference] = {
    com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry.getReferencesFromProviders(this)
  }
}
