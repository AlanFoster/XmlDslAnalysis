package foo.language.references.body

import com.intellij.psi.{PsiReference, PsiReferenceBase, PsiElement}
import com.intellij.openapi.util.TextRange
import foo.language.MethodConverter
import foo.language.references.EipReference

/**
 * Represents a CamelBodyReference, IE the element within ${body}.
 * This implementation provides no variant completion and should purely
 * resolve to the inferred body type within the EIP graph
 *
 * @param element The parent element, presumably func body
 * @param range The text range within the parent element to provide references for
 */
class CamelBodyReference(element: PsiElement, range: TextRange)
// Note this reference is a soft reference, ie if it doesn't resolve, it is *not* an error!
  extends PsiReferenceBase[PsiElement](element, range, false)
  with MethodConverter
  with EipReference {

  /**
   * No variants are possible within this reference
   */
  override def getVariants: Array[AnyRef] = {
    PsiReference.EMPTY_ARRAY.asInstanceOf[Array[AnyRef]]
  }

  /**
   * Attempts to resolve to the matching PsiClass body
   * @return The resolved PsiClass, otherwise null.
   */
  override def resolve(): PsiElement = {
    // TODO Multiple resolve
    getBodyTypes(element).headOption.getOrElse(null)
  }
}

