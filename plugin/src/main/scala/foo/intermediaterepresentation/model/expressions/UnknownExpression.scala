package foo.intermediaterepresentation.model.expressions

/**
 * Created by a on 30/03/14.
 */
// An expression language not currently handled by the plugin
case class UnknownExpression() extends Expression{
  override val value: String = "Unknown Expression"
}
