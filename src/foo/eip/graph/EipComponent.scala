package foo.eip.graph

import foo.dom.Model.ProcessorDefinition

/**
 * Represents a basic class which can be represented within the context of an
 * EIP diagram
 * @param id The unique vakue of this component
 * @param eipType The EIP type, string for possible expansion - however the loss of type information is obvious
 * @param text The text associated with this component, for instance an expression
 */
class EipComponent(val id: String, val eipType: String, val text: String, val semantics: CamelType, val psiReference: ProcessorDefinition)

/**
 * The companion object to help create a new instance of EipComponent.
 * This allows for the use of 'EipComponent(args*)' without the use of a 'new' prefix
 */
object EipComponent {
  /**
   * Creates a new instance of the EipComponent class
   * @param id The unique vakue of this component
   * @param eipType The EIP type, string for possible external expansion
   * @param text The text associated with this component, for instance an expression
   */
  def apply(id: String, eipType: String, text: String, semantics: CamelType, psiReference: ProcessorDefinition) = new EipComponent(id, eipType, text, semantics, psiReference)
}

// Initial type semantic information
class CamelType(val possibleBodyTypes: Set[String], val headers: Map[String, ProcessorDefinition])

object CamelType {
  def apply(bodyType: String) = new CamelType(Set(bodyType), Map())
  def apply() = new CamelType(Set(), Map())
  def apply(bodyTypes: Set[String], headers: Map[String, ProcessorDefinition]) = new CamelType(bodyTypes, headers)
}