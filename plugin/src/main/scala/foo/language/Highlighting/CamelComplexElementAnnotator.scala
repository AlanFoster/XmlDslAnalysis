package foo.language.Highlighting

import com.intellij.lang.annotation.{AnnotationHolder, Annotator}
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import foo.language.generated.psi.CamelFunctionName
import com.intellij.openapi.editor.colors.TextAttributesKey

/**
 * Performs additional highlighting on elements which can not perform
 * their own highlighting through 'simple' tokens alone.
 */
class CamelComplexElementAnnotator extends Annotator {
  override def annotate(element: PsiElement, holder: AnnotationHolder): Unit = {
    // Perform highlighting on Camel function names such as `headerAs`(...)
    applyHighlight(holder, CamelTextAttributeKeys.CAMEL_FUNC)(() => {
      Option(PsiTreeUtil.getParentOfType(element, classOf[CamelFunctionName], false))
    })
  }

  /**
   * Applies a given highlight if a the function applied, through currrying, provides a non-None
   * PsiElement
   *
   * @param holder The error holder
   * @param attributesKey The attributes key to apply
   * @param extractor The function which can potentially return the matching PsiElement for this
   *                  highlight
   */
  private def applyHighlight(holder:AnnotationHolder, attributesKey: TextAttributesKey)
                            (extractor: () => Option[PsiElement]) {
    val optionElement = extractor()
    optionElement match {
      case None =>
      case Some(element) =>
        holder
          .createInfoAnnotation(element, null)
          .setTextAttributes(CamelTextAttributeKeys.CAMEL_FUNC)
    }
  }
}
