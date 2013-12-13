package foo.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import foo.language.psi.ICamelJavaFQCN
import com.intellij.lang.ASTNode
import com.intellij.psi._
import foo.language.references.CamelFQCNReference
import scala.annotation.tailrec
import com.intellij.openapi.util.TextRange

class CamelJavaFQCN(node: ASTNode) extends ASTWrapperPsiElement(node) with ICamelJavaFQCN  {
  override def getReferences: Array[PsiReference] = {
    val text = getText
    val splitSections = ElementSplitter.split(text)

    val references = {
      for { splitSection <- splitSections }
        yield new CamelFQCNReference(this, new TextRange(splitSection._2, splitSection._3))
    }

    references.toArray
  }


  override def replace(newElement: PsiElement): PsiElement = {
    super.replace(newElement)
  }

  def setName(name: String): PsiElement = ???
  def getNameIdentifier: PsiElement = this
}
