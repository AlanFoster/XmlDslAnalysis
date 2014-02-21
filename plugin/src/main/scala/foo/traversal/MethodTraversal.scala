package foo.traversal

import com.intellij.psi.{PsiClass, PsiMethod}

/**
 * Provides functions which are associated with method traversal within psi classes
 */
object MethodTraversal {
  /**
   * Returns the list of all known methods available within this psi class
   * Note this includes all super methods
   * @param psiClass The psi class which contains the relevant Psi information
   * @return The list of all possible variants within the given context, which
   *         have unique identifier method names.
   */
  def getAllMethods(psiClass: PsiClass): List[PsiMethod] = {
    val allMethods = psiClass.getAllMethods.toList

    // Ensure duplicates are removed
    val uniqueIdentifiers = allMethods.groupBy(_.getName).map(_._2.head).toList
    uniqueIdentifiers
  }
}
