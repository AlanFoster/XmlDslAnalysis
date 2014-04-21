package foo.language.annotators

import com.intellij.lang.annotation.{AnnotationHolder, Annotator}
import com.intellij.psi.PsiElement
import foo.language.generated.CamelTypes
import foo.language.Highlighting.CamelTextAttributeKeys
import com.intellij.openapi.util.TextRange

/**
 * Highlights Strings which are not well formed and contain an ending speech mark as expected
 */
class IncompleteStringAnnotator extends Annotator {
  /**
   * {@inheritdoc}
   */
  override def annotate(element: PsiElement, holder: AnnotationHolder): Unit = {
    val isString = element.getNode.getElementType == CamelTypes.STRING
    if(!isString) return

    val text = element.getText

    if(!terminatedCorrectly(text)) {
      val startOffset = element.getTextRange.getStartOffset
      val range = new TextRange(startOffset + text.length - 1, startOffset + text.length)
      holder.createErrorAnnotation(range, "Unterminated String Literal")
    }
  }

  /**
   *
   * @param text The string to check against
   * @return True if the first letter equals the last letter, otherwise false
   */
  private def terminatedCorrectly(text: String): Boolean = {
    val (firstChar, lastChar) = (text.head, text.last)

    val firstEqualsLast = firstChar == lastChar
    val isValid = text.length > 1 && firstEqualsLast

    isValid
  }
}
