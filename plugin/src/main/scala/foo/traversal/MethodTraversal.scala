package foo.traversal

import com.intellij.psi.{PsiModifier, PsiClass, PsiMethod}

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

  /**
   * Populates the list of public methods, minus the constructor, which
   * is available within this psi class
   * @param psiClass The given Psi Class
   * @return The list of PsiMethods which hold true to thie predicate
   */
  def getAllPublicMethods(psiClass: PsiClass): List[PsiMethod] = {
    MethodTraversal.getAllMethods(psiClass)
      // Public accessors only
      .filter(_.getModifierList.hasModifierProperty(PsiModifier.PUBLIC))
      // We shouldn't suggest constructors
      .filter(!_.isConstructor)
  }
}
