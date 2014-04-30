package foo.intermediaterepresentation.model.expressions

import foo.intermediaterepresentation.model.references.Reference
import foo.intermediaterepresentation.model.types.CamelStaticTypes.ACSLFqcn

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

final case class Simple(value: String, resultType: Option[ACSLFqcn], reference: Reference) extends Expression
final case class Constant(value: String) extends Expression
final case class UnknownExpression() extends Expression{
  override val value: String = "Unknown Expression"
}
