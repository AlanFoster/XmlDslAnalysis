package foo.language.references

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement

/**
 * Identiteis whether or not an element is allowed to be renamed in place or not.
 */
class CamelRefactoringSupportProvider extends RefactoringSupportProvider {
  /**
   * {@inheritdoc}
   */
  override def isInplaceRenameAvailable(element: PsiElement, context: PsiElement): Boolean = {
    true
  }
}
