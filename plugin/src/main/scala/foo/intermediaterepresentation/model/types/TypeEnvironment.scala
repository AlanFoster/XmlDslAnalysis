package foo.intermediaterepresentation.model.types

import foo.intermediaterepresentation.model.references.Reference

/**
 * Represents the TypeEnvironment which can be asosciated with a given processor.
 *
 * @param body The currently inferred body information
 * @param headers The current inferred header information
 */
case class TypeEnvironment(body: Set[String], headers:Map[String, (String, Reference)]) extends TypeInformation {
  /**
   * Unions two type environments together
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