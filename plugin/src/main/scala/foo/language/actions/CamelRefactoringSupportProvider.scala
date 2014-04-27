package foo.language.actions

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import com.intellij.refactoring.RefactoringActionHandler

/**
 * Provides refactoring support for the ACSL.
 *
 * @see
 */
class CamelRefactoringSupportProvider extends RefactoringSupportProvider {
  /**
   * Enables or disables the associated refactoring support with the given PsiElement.
   * All validation logic will be performed in the appropriate handler
   *
   * @param context The current refactoring context element
   * @return True if refactoring is available for this current element,
   *         false otherwise/
   */
  override def isAvailable(context: PsiElement): Boolean =
    true

  /**
   * @inheritdoc
   */
  override def getIntroduceVariableHandler: RefactoringActionHandler =
    new CamelIntroduceExpressionVariable()
}
