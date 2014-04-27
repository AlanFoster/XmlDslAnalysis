package foo.language.annotators

import com.intellij.lang.annotation.{AnnotationHolder, Annotator}
import com.intellij.psi.{PsiReference, PsiElement}
import foo.language.Core.CamelFileType
import foo.language.Highlighting.CamelTextAttributeKeys
import foo.language.annotators.AnnotatorHelper._
import com.intellij.openapi.util.TextRange

/**
 * Represents an implementation of an annotator which will highlight all
 * unresolved references.
 *
 * Note, this class will only highlight elements with PsiReferences which return
 * false for isSoft
 */
class UnresolvedReferenceAnnotator extends Annotator {
  /**
   * {@inheritdoc}
   */
  def annotate(element: PsiElement, holder: AnnotationHolder): Unit = {
    // Only consider elements which match our Camel Language
    if(!(element.getContainingFile.getFileType == CamelFileType)) return

    // Split references into a group of resolved and unresolved references
    val resolvedGroup = element.getReferences.groupBy(isResolved).withDefaultValue(Array())
    val (resolved, unresolved) = ( resolvedGroup(true), resolvedGroup(false) )

    // For Each successfully resolved reference, create an info annotation within the text range
    resolved.foreach(annotateSuccessfullyResolved(element, holder, _))

    // ForEach missing hard reference, create an error annotation within its text range
    unresolved.foreach(annotateUnresolved(element, holder, _))
  }

  /**
   * Applies an annotation to the given psi reference under the assumption that it has been
   * successfully resolved as expected
   *
   * @param element The parent element
   * @param holder The annotation holder
   * @param psiReference The successfully resolved reference
   */
  private def annotateSuccessfullyResolved(element: PsiElement, holder: AnnotationHolder, psiReference: PsiReference) {
    val rangeInDocument = addTextRanges(element.getTextRange, psiReference.getRangeInElement)
    holder
      .createInfoAnnotation(rangeInDocument, null)
      .setTextAttributes(CamelTextAttributeKeys.RESOLVED_REFERENCE)
  }

  /**
    * Applies an annotation to the given psi reference under the assumption that it has been
    * not succesfully resolved as expected
    *
   * @param element The parent element
   * @param holder The annotation holder
   * @param psiReference The unresolved reference
   */
  private def annotateUnresolved(element: PsiElement, holder: AnnotationHolder, psiReference: PsiReference) {
    val rangeInDocument = addTextRanges(element.getTextRange, psiReference.getRangeInElement)
    holder
      .createWeakWarningAnnotation(rangeInDocument, "Unresolved Reference - Potentially Invalid")
      .setTextAttributes(CamelTextAttributeKeys.UNRESOLVED_REFERENCE)
  }


  /**
   * Annotators should register their text ranges relative to the document, rather than relative
   * to the element. This function correctly shifts the reference range as expected into a new
   * TextRange
   *
   * @param one The parent element
   * @param two The reference's text range within the element
   * @return The added TextRange relative to the PsiFile.
   */
  def addTextRanges(one: TextRange, two: TextRange) =
    new TextRange(
      one.getStartOffset + two.getStartOffset,
      one.getStartOffset + two.getEndOffset
    )
}
