package foo.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import foo.language.psi.ICamelJavaFQCN
import com.intellij.lang.ASTNode
import com.intellij.psi._
import foo.language.references.CamelFQCNReference
import com.intellij.openapi.util.TextRange

/**
 * Represents a Camel Java fully qualified name implementation
 * @param node The AST Node within the created tree after parsing
 */
class CamelJavaFQCN(node: ASTNode) extends ASTWrapperPsiElement(node) with ICamelJavaFQCN  {
  /**
   * Provides the references associated with this element
   * @return The array of PsiReferences
   */
  override def getReferences: Array[PsiReference] = {
    val text = getText
    val splitSections = ElementSplitter.split(text)

    // Provide a reference contribution for each possible split section within the CamelJavaFQCN text
    val references = {
      for { splitSection <- splitSections }
        yield new CamelFQCNReference(this, new TextRange(splitSection.start, splitSection.end))
    }

    references.toArray
  }


  /**
   * Sets the name of this element. This is not required, and is therefore NOOP.
   * @param name The new name
   * @return Not implemented exception
   */
  def setName(name: String): PsiElement = ???

  /**
   * Returns the identifier associated with this element
   * @return This
   */
  def getNameIdentifier: PsiElement = this
}
