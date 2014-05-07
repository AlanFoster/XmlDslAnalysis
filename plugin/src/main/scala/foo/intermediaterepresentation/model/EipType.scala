package foo.intermediaterepresentation.model

/**
 * Represents the enumeration of EipNames. These are associated with
 * nodes within the expression tree (ie the processors).
 */
object EipType extends Enumeration {
  type EipType = Value
  val Route, To, From, Choice, When, Translator, Misc, WireTap, Otherwise = Value
  def getString(value: EipType.Value) = value.toString
}