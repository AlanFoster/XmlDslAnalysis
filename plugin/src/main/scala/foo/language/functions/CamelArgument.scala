package foo.language.functions

import com.intellij.psi.tree.IElementType
import foo.intermediaterepresentation.model.types.CamelStaticTypes._

/**
 * Represents a CamelArgument which can be used within the context of a camel function
 */
trait CamelArgument {
  /**
   * The name associated with the camel argument
   * @return The name associated with teh camel argument
   */
  def argName: String

  /**
   * The type definition associated with this argument
   * @return The required element type information
   */
  def requiredElementType: IElementType

  /**
   * Optionally the required FQCN type
   * @return The required FQCN Type
   */
  def requiredFQCN: ACSLFqcn

  /**
   * Computes the prettified version of this argument's type information
   * @return A human-readible version of this argument type information
   */
  def prettyType: String

  /**
   * Computes the overall argument name and type information
   * for this argument name
   * @return The human readible type information for this argument
   */
  def prettyPrint: String = s"${argName}: ${prettyType}"
}
