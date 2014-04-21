package foo.language.typeChecking

import foo.intermediaterepresentation.model.types.TypeEnvironment
import com.intellij.psi.PsiElement

/**
 * Represents the ability to perform type inference on a given Simple Psi Tree
 */
trait SimpleTypeChecker {
  /**
   * Performs type checking on the given file.
   * @param typeEnvironment The current type environment associated with this expression
   * @param camelPsiFile The topmost CamelPsiFile (IE the root node of the Psi tree)
   * @return The inferred type information associated with this expression
   */
  def typeCheckCamel(typeEnvironment: TypeEnvironment, camelPsiFile: PsiElement): Option[Set[String]]
}
