package foo.intermediaterepresentation.model.types

import foo.intermediaterepresentation.model.references.Reference
import CamelStaticTypes._

object CamelStaticTypes {
  type ACSLKey = String
  type ACSLFqcn = String
}


/**
 * Represents the base trait of TypeInformation, ie inferred information about
 * a given processor
 */
sealed trait TypeInformation

/**
 * Represents the TypeEnvironment which can be asosciated with a given processor.
 *
 * @param body The currently inferred body information
 * @param headers The current inferred header information
 */
final case class TypeEnvironment(body: Set[ACSLFqcn], headers:Map[ACSLKey, (ACSLFqcn, Reference)]) extends TypeInformation {
  /**
   * Unions two type environments together.
   * This could be named ⊕, as Scala allows it. But it has been chosen not to do so.
   *
   * @param other The other type to union with
   * @return A new TypeEnvironment instance with the union of type information performed
   */
  def +(other:TypeEnvironment): TypeEnvironment = {
    TypeEnvironment(
      body ++ other.body,
      headers ++ other.headers
    )
  }
}

object TypeEnvironment {
  def apply():TypeEnvironment = TypeEnvironment(Set(), Map())
}

/**
 * Represents the single instance of TypeInformation that is not yet
 * resolved.
 *
 * This could be used under the scenario of having creating an IR, but not
 * yet placing any semantic information into the tree yet.
 */
object NotInferred extends TypeInformation {
  override def toString: String = "NotInferred"
}


/**
 * Represents the scenario in which type information has been inferred for a given node.
 * Note that the before/after information may not have been successfully inferred yet.
 */
case class Inferred(before: TypeEnvironment, after: TypeEnvironment) extends TypeInformation