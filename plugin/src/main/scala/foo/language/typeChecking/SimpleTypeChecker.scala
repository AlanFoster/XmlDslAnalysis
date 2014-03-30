package foo.language.typeChecking

import foo.language.Core.CamelPsiFile
import foo.intermediaterepresentation.model.types.TypeEnvironment

/**
 * Represents the ability to perform type inference on a given Simple Psi Tree
 */
trait SimpleTypeChecker {
  def typeCheckCamel(typeEnvironment: TypeEnvironment, camelPsiFile: CamelPsiFile): Option[Set[String]]
}
