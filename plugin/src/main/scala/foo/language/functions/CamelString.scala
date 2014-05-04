package foo.language.functions

import foo.language.generated.CamelTypes
import foo.intermediaterepresentation.model.types.CamelStaticTypes._
import com.intellij.psi.CommonClassNames

/**
 * A case class implementation of a CamelArgument which represents a String
 * which should be used within a camel function
 * @param argName The argument name associated with this Argument
 */
case class CamelString(override val argName: String) extends CamelArgument {
  /**
   * {@inheritdoc}
   */
  def prettyType: String = "String"
  /**
   * {@inheritdoc}
   */
  def requiredElementType = CamelTypes.STRING

  /**
   * Optionally the required FQCN type
   * @return The required FQCN Type
   */
  override def requiredFQCN: ACSLFqcn = CommonClassNames.JAVA_LANG_STRING
}
