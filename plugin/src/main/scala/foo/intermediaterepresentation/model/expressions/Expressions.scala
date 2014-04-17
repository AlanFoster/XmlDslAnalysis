package foo.intermediaterepresentation.model.expressions

import foo.intermediaterepresentation.model.references.Reference

/**
 * Represents the base trait of an expression within Camel
 */
sealed trait Expression {
  val value: String
}

object Expression {
  def unapply(expression: Expression): Option[(String)] = {
    Some(expression.value)
  }
}

/*******************************************************************
  * Expressions - The implementations
  *******************************************************************/

// TODO Maybe result type should be more statically typed instead of a string
final case class Simple(value: String, resultType: Option[String], reference: Reference) extends Expression
final case class Constant(value: String) extends Expression
final case class UnknownExpression() extends Expression{
  override val value: String = "Unknown Expression"
}
