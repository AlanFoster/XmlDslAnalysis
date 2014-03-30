package foo.intermediaterepresentation.model.expressions

/**
 * Created by a on 30/03/14.
 */
object Expression {
  def unapply(expression: Expression): Option[(String)] = {
    Some(expression.value)
  }
}

/*******************************************************************
  * Expressions
  *******************************************************************/

trait Expression {
  val value: String
}