package foo.eip.model

import foo.eip.graph.ADT.EmptyDAG

// Abstract Models

trait Expression

case class Constant(value: String) extends Expression
// TODO Maybe result type should be more statically typed instead of a string
case class Simple(value: String, resultType: Option[String]) extends Expression
// An expression language not currently handled by the plugin
case class UnknownExpression() extends Expression

trait Processor

case class Route(children: List[Processor]) extends Processor
case class From(uri: String) extends Processor
case class To(uri: String) extends Processor
case class BeanReference(reference: String, method: String) extends Processor
case class SetBody(expression: Expression) extends Processor
case class SetHeader(headerName: String, expression: Expression) extends Processor
case class Choice(whens: List[When]) extends Processor
case class When(expression: Expression, children: List[Processor]) extends Processor
case class Otherwise(children: List[Processor]) extends Processor
case class Bean(ref: Option[String], method:  Option[String]) extends Processor

object MainTest {
  def main(args: Array[String]) {

    val from = From("direct:start")

    val when1 = When(
      Constant("true"),
      List(To("direct:foo"))
    )

    val when2 = When(
      Constant("false"),
      List(To("direct:bar"))
    )

    val choice = Choice(List(when1, when2))

    val afterChoice = To("uri:end")

    val route = Route(List(
      from,
      choice,
      afterChoice
    ))

    println(route)
  }
}