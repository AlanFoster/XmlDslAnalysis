package foo.language.annotators

import com.intellij.lang.annotation.{AnnotationHolder, Annotator}
import com.intellij.psi.{PsiPolyVariantReference, PsiElement}
import foo.language.Core.CamelFileType
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Font
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.module.{ModuleUtilCore, ModuleUtil}
import com.intellij.openapi.editor.{EditorFactory, Editor}
import com.intellij.openapi.fileEditor.FileEditorManager

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

    // Find all elements which should be 'hard' references
    // IE elements which should be highlighted as invalid if not resolved
    val missingHardReferences =
      element.getReferences
        // Filter hard references which resolve to null
        .filter(reference => !reference.isSoft && reference.resolve == null)
        // We should handle the fact that although elements can resolve to null
        // This may be because they are psi poly variant references, and simply
        // can not resolve to a *single* PsiElement - we should not highlight
        // these as errors
        .filter {
          case reference: PsiPolyVariantReference => reference.multiResolve(false).isEmpty
          case _ => true
        }

    // ForEach missing hard reference, create an error annotation within its text range
    missingHardReferences.foreach {
      reference => {
        val rangeInDocument = addTextRanges(element.getTextRange, reference.getRangeInElement)
        holder
          .createErrorAnnotation(rangeInDocument, "Unresolved Reference")
            .setTextAttributes(TextAttributesKey.createTextAttributesKey("UNRESOLVED_REFERENCE",
                new TextAttributes(java.awt.Color.RED, null, null, null, Font.BOLD))
          )
      }
    }
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
