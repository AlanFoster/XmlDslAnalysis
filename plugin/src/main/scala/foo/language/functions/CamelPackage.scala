package foo.language.functions

import foo.language.generated.CamelTypes
import foo.intermediaterepresentation.model.types.CamelStaticTypes._

/**
 * A case class implementation of a CamelArgument, which represents a FQCN which
 * should be used as a camel argument within the context of a camel function
 *
 * @param argName The argument name associated with this Argument
 */
case class CamelPackage(override val argName: String) extends CamelArgument {
  /**
   * {@inheritdoc}
   */
  def prettyType: String = "Class"

  /**
   * {@inheritdoc}
   */
  def requiredElementType = CamelTypes.FQCN

  /**
   * {@inheritdoc}
   */
  override def requiredFQCN: ACSLFqcn = ""
}