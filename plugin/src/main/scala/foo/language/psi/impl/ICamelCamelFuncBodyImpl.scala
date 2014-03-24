package foo.language.psi.impl

import foo.language.psi.{IACamelVariableAccess, ICamelCamelFuncBody}
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
;

/**
 * Implementation of the base trait ICamelCamelFuncBody which is used to provide access
 * to the ReferenceProvidersRegistry
 */
class ICamelCamelFuncBodyImpl(node: ASTNode) extends ASTWrapperPsiElement(node) with ICamelCamelFuncBody {
  override def getReferences: Array[PsiReference] = {
    val references = ReferenceProvidersRegistry.getReferencesFromProviders(this)
    references
  }
}