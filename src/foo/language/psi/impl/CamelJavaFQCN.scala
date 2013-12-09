package foo.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import foo.language.psi.ICamelJavaFQCN
import com.intellij.lang.ASTNode
import com.intellij.psi._
import foo.language.references.CamelFQCNReference
import scala.annotation.tailrec

class CamelJavaFQCN(node: ASTNode) extends ASTWrapperPsiElement(node) with ICamelJavaFQCN  {
  override def getReferences: Array[PsiReference] = {
/*    val firstChild = getFirstChild
    //@tailrec
    def provideReferences(child: PsiElement, previous: Option[PsiElement]):List[PsiReference] = (child, previous) match {
      case (identifier, None) =>
        new CamelFQCNReference(identifier) :: provideReferences(identifier.getNextSibling, Some(identifier))
      case _ => List[PsiReference]()
    }
    provideReferences(firstChild, None).toArray*/
    Array(new CamelFQCNReference(this))
  }

  def setName(name: String): PsiElement = ???
  def getNameIdentifier: PsiElement = this
}
