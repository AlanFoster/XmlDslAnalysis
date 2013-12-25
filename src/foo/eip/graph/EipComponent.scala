package foo.eip.graph

import foo.Model.ProcessorDefinition

/**
 * Represents a basic class which can be represented within the context of an
 * EIP diagram
 * @param id The unique vakue of this component
 * @param eipType The EIP type, string for possible expansion - however the loss of type information is obvious
 * @param text The text associated with this component, for instance an expression
 */
class EipComponent(val id: String, val eipType: String, val text: String, val psiReference: ProcessorDefinition)

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
  def apply(id: String, eipType: String, text: String, psiReference: ProcessorDefinition) = new EipComponent(id, eipType, text, psiReference)
}