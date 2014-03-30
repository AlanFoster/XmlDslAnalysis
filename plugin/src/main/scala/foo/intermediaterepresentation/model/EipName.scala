package foo.intermediaterepresentation.model

/**
 * Represents the enumeration of EipNames. These are associated with
 * nodes within the expression tree (ie the processors).
 */
object EipName extends Enumeration {
  type EipName = Value
  val Route, To, From, choice, When, Translator, Otherwise = Value
}