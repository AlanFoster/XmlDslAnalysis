package foo.language.typeChecking

import foo.language.Core.CamelPsiFile

/**
 * Represents the ability to perform type inference on a given Simple Psi Tree
 */
trait SimpleTypeChecker {
  def typeCheckCamel(camelPsiFile: CamelPsiFile): Option[String]
}
