package foo.language.annotators

import com.intellij.psi.{PsiPolyVariantReference, PsiReference}
import com.intellij.openapi.util.TextRange

/**
 * Provides a common set of functions associated with annotators
 */
object AnnotatorHelper {

  /**
   * Predicate function which decides whether or not a psi reference has been
   * successfully resolved as expected
   * @param psiReference The PsiReference to check against
   * @return True if the PsiReference has been successfully resolved,
   *         otherwise false.
   */
  def isResolved(psiReference: PsiReference):Boolean = {
    // Find all elements which should be 'hard' references
    // IE elements which should be highlighted as invalid if not resolved
    val isHardReferenceResolved = psiReference.isSoft || psiReference.resolve != null

    // We should handle the fact that although elements can resolve to null
    // This may be because they are psi poly variant references, and simply
    // can not resolve to a *single* PsiElement - we should not highlight
    // these as errors
    val isMultiResolved = psiReference match {
      case reference: PsiPolyVariantReference => reference.multiResolve(false).nonEmpty
      case _ => false
    }

    // !resolved => multiresolved === !!resolved || multiresolved === resolved || multiresolved
    isHardReferenceResolved || isMultiResolved
  }
}
