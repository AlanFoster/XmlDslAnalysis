package foo.eip.graph.model

import foo.dom.Model.ProcessorDefinition


// Initial type semantic information
case class CamelTypeSemantics(possibleBodyTypes: Set[String], headers: Map[String, ProcessorDefinition]) {
  /**
   * Unions two CamelTypes together
   *
   * @param other The other type to union with
   * @return A new CamelType instance with the union of type information performed
   */
  def +(other:CamelTypeSemantics): CamelTypeSemantics =
    CamelTypeSemantics.apply(
      this.possibleBodyTypes ++ other.possibleBodyTypes,
      this.headers ++ other.headers
    )
}

/**
 * Represents the methods of creating new CamelType instances
 */
object CamelType {
  def apply(bodyType: String) = new CamelTypeSemantics(Set(bodyType), Map())
  def apply() = new CamelTypeSemantics(Set(), Map())
  def apply(bodyTypes: Set[String], headers: Map[String, ProcessorDefinition]) = new CamelTypeSemantics(bodyTypes, headers)
}