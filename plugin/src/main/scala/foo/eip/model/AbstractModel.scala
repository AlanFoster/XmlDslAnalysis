package foo.eip.model

// Abstract Models


trait TypeInformation
object NotInferred extends TypeInformation {
  override def toString: String = "NotInferred"
}
case class Inferred(before: TypeEnvironment, after: TypeEnvironment) extends TypeInformation

case class TypeEnvironment(body: Set[String], headers:Map[String, String]) extends TypeInformation

trait Expression

case class Constant(value: String) extends Expression
// TODO Maybe result type should be more statically typed instead of a string
case class Simple(value: String, resultType: Option[String]) extends Expression
// An expression language not currently handled by the plugin
case class UnknownExpression() extends Expression

trait Processor {
  val typeInformation: TypeInformation
}

case class Route(children: List[Processor], typeInformation: TypeInformation = NotInferred) extends Processor
case class From(uri: String, typeInformation: TypeInformation = NotInferred) extends Processor
case class To(uri: String, typeInformation: TypeInformation = NotInferred) extends Processor
case class BeanReference(reference: String, method: String, typeInformation: TypeInformation = NotInferred) extends Processor
case class SetBody(expression: Expression, typeInformation: TypeInformation = NotInferred) extends Processor
case class SetHeader(headerName: String, expression: Expression, typeInformation: TypeInformation = NotInferred) extends Processor
case class Choice(whens: List[When], typeInformation: TypeInformation = NotInferred) extends Processor
case class When(expression: Expression, children: List[Processor], typeInformation: TypeInformation = NotInferred) extends Processor
case class Otherwise(children: List[Processor], typeInformation: TypeInformation = NotInferred) extends Processor
case class Bean(ref: Option[String], method:  Option[String], typeInformation: TypeInformation = NotInferred) extends Processor

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

    val semantics = Inferred(TypeEnvironment(Set(), Map()), TypeEnvironment(Set(), Map()))
    val typedProcessor = From("direct:start", semantics)

    println(typedProcessor)
  }
}