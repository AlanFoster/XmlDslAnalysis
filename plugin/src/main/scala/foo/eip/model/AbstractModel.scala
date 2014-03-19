package foo.eip.model

import com.intellij.psi.PsiElement
import foo.dom.Model.ProcessorDefinition

// Abstract Models

/*******************************************************************
  * Semantic information
  *******************************************************************/
trait TypeInformation
object NotInferred extends TypeInformation {
  override def toString: String = "NotInferred"
}
case class Inferred(before: TypeEnvironment, after: TypeEnvironment) extends TypeInformation

case class TypeEnvironment(body: Set[String], headers:Map[String, (String, Reference)]) extends TypeInformation

/*******************************************************************
  * Expressions
  *******************************************************************/

trait Expression

case class Constant(value: String) extends Expression
// TODO Maybe result type should be more statically typed instead of a string
case class Simple(value: String, resultType: Option[String]) extends Expression
// An expression language not currently handled by the plugin
case class UnknownExpression() extends Expression

/*******************************************************************
  * Adapter Psi reference
  *******************************************************************/

trait Reference
object NoReference extends Reference
case class DomReference(element: ProcessorDefinition) extends Reference {
  override def toString: String = {
    // Strip off anything that is not needed, ie in `FromProcessorDefinition$$EnhancerByCGLIB$$28a504a3`
    val classReferenceName = element.getClass.getSimpleName.takeWhile(_ != '$')
    s"DomReference(${classReferenceName})"
  }
}

/*******************************************************************
 * Processors
 *******************************************************************/

trait Mappable[T] {
  //def map[U](f: T => U): U
  def collectFirst(func: PartialFunction[T, Boolean]): Option[T]
}

trait Processor extends Mappable[Processor] {
  val typeInformation: TypeInformation
  val reference: Reference

  //override def map[U](f: (T) => U): U = ???
  override def collectFirst(func: PartialFunction[Processor, Boolean]): Option[Processor] =
    if(func.isDefinedAt(this) && func(this)) Some(this)
    else {
      this match {
        case Route(children, _, _ ) =>
          findChild(children)(func)
        case Choice(children, _, _) =>
          findChild(children)(func)
        case When(_, children, _, _) =>
          findChild(children)(func)
        case _ =>
          None
      }
    }

  private def findChild(children: List[Processor])
                           (func: PartialFunction[Processor, Boolean]): Option[Processor] = {
    children.map(_.collectFirst(func)).collectFirst({ case Some(x) => x })
  }
}

object Processor {
  def unapply(processor: Processor): Option[(Reference, TypeInformation)] = {
    Some(processor.reference, processor.typeInformation)
  }
}

case class Route(children: List[Processor], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor
case class From(uri: String, reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor
case class To(uri: String, reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor
case class BeanReference(ref: String, method: String, reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor
case class SetBody(expression: Expression, reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor
case class SetHeader(headerName: String, expression: Expression, reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor
case class Choice(whens: List[When], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor
case class When(expression: Expression, children: List[Processor], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor
case class Otherwise(children: List[Processor], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor
case class Bean(ref: Option[String], method:  Option[String], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor

object MainTest {
  def main(args: Array[String]) {

    val from = From("direct:start", NoReference)

    val when1 = When(
      Constant("true"),
      List(To("direct:foo", NoReference)),
      NoReference
    )

    val when2 = When(
      Constant("false"),
      List(To("direct:bar", NoReference)),
      NoReference
    )

    val choice = Choice(List(when1, when2),
      NoReference)

    val afterChoice = To("uri:end",
      NoReference)

    val route = Route(List(
      from,
      choice,
      afterChoice
    ),
    NoReference)

    val semantics = Inferred(TypeEnvironment(Set(), Map()), TypeEnvironment(Set(), Map()))
    val typedProcessor = From("direct:start", NoReference, semantics)

    println(typedProcessor)
  }
}