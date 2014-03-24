package foo.eip.model

import com.intellij.psi.{PsiElement, PsiMethod}
import foo.dom.Model.{BlueprintBean, Blueprint, ProcessorDefinition}
import com.intellij.util.xml.GenericAttributeValue
import foo.eip.converter.DomAbstractModelConverter
import foo.eip.typeInference.DataFlowTypeInference
import com.intellij.psi.xml.{XmlTagValue, XmlTag}
import foo.eip.model.EipName.EipName

// Helpers to be placed in a better file
object AbstractModelManager {
  def getCurrentNode(domFile: Blueprint, currentTag: XmlTag): Option[Processor] = {

    def interceptor(processor: Processor)(fallback: () => TypeEnvironment): TypeEnvironment = processor match {
      case Processor(DomReference(reference), _) if reference.getXmlTag == currentTag =>
        TypeEnvironment()
      case _ => fallback()
    }

    val route = new DomAbstractModelConverter().convert(domFile)
    val routeWithSemantics = new DataFlowTypeInference().performTypeInference(route, interceptor)

    val currentNode = routeWithSemantics.collectFirst({
      case Processor(DomReference(reference), _) =>
        reference.getXmlTag == currentTag
    })
    currentNode
  }

  /**
   * Gets the current inferred header information and their
   * @param domFile
   * @param currentTag
   * @return
   */
  def getInferredHeaders(domFile: Blueprint, currentTag: XmlTag): Option[Map[String, ProcessorDefinition]] =  {
    // Perform route semantics
    val currentNode = getCurrentNode(domFile, currentTag)
    val headers: Option[Map[String, (String, Reference)]] = currentNode.flatMap(_.headers)

    // Additionally map the headers to their real reference types
    val availableHeaders = headers
        // Extract the associated element which modified this variable
        .map(map => map.collect({
        case (key, (inferredType, DomReference(processorDefinition))) => (key, processorDefinition)
      }))

    availableHeaders
  }
}

// Abstract Models

/*******************************************************************
  * Semantic information
  *******************************************************************/
trait TypeInformation
object NotInferred extends TypeInformation {
  override def toString: String = "NotInferred"
}
case class Inferred(before: TypeEnvironment, after: TypeEnvironment) extends TypeInformation

case class TypeEnvironment(body: Set[String], headers:Map[String, (String, Reference)]) extends TypeInformation {
  /**
   * Unions two type environments together
   *
   * @param other The other type to union with
   * @return A new TypeEnvironment instance with the union of type information performed
   */
  def +(other:TypeEnvironment): TypeEnvironment = {
    TypeEnvironment(
      body ++ other.body,
      headers ++ other.headers
    )
  }
}

object TypeEnvironment {
  def apply():TypeEnvironment = TypeEnvironment(Set(), Map())
}

/*******************************************************************
  * Expressions
  *******************************************************************/

trait Expression {
  val value: String
}

object Expression {
  def unapply(expression: Expression): Option[(String)] = {
    Some(expression.value)
  }
}

case class Constant(value: String) extends Expression
// TODO Maybe result type should be more statically typed instead of a string
case class Simple(value: String, resultType: Option[String], reference: Reference) extends Expression
// An expression language not currently handled by the plugin
case class UnknownExpression() extends Expression{
  override val value: String = "Unknown Expression"
}

/*******************************************************************
  * Adapter Psi reference
  *******************************************************************/

trait Reference
object NoReference extends Reference
case class ExpressionReference(element: XmlTag) extends Reference
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

  def eipType: EipName

  /**
   * Extracts the headers associated with the current processor
   * @return An Option, as the semantic information may not have been inferred
   *         currently.
   */
  def headers: Option[Map[String, (String, Reference)]] = typeInformation match {
      case Inferred(TypeEnvironment(_, headerMap), _) =>
        Some(headerMap)
      case _ => None
  }

  /**
   * Extracts the out headers associated with the current processor
   * @return An Option, as the semantic information may not have been inferred
   *         currently.
   */
  def outHeaders: Option[Map[String, (String, Reference)]] = typeInformation match {
    case Inferred(_, TypeEnvironment(_, headerMap)) =>
      Some(headerMap)
    case _ => None
  }

  /**
   * Extracts the current body set associated with the current processor
   * @return An option, as the semantic information may not have been inferred
   *         currently.
   */
  def bodies: Option[Set[String]] = typeInformation match {
    case Inferred(TypeEnvironment(bodySet, _), _) =>
      Some(bodySet)
    case _ => None
  }

  /**
   * Extracts the out body set associated with the current processor
   * @return An option, as the semantic information may not have been inferred
   *         currently.
   */
  def outBodies: Option[Set[String]] = typeInformation match {
    case Inferred(_, TypeEnvironment(bodySet, _)) =>
      Some(bodySet)
    case _ => None
  }

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

object EipName extends Enumeration {
  type EipName = Value
  val To, From, choice, When, Translator, Otherwise = Value
}

case class Route(children: List[Processor], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  override def eipType: EipName = ???
}
case class From(uri: String, reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor {
  override def eipType: EipName = EipName.From
}
case class To(uri: String, reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  override def eipType: EipName = EipName.To
}
case class SetBody(expression: Expression, reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  override def eipType: EipName = EipName.Translator
}
case class SetHeader(headerName: String, expression: Expression, reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  override def eipType: EipName = EipName.Translator
}
case class Choice(whens: List[When], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  override def eipType: EipName = EipName.choice
}
case class When(expression: Expression, children: List[Processor], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  override def eipType: EipName = EipName.When
}
case class Otherwise(children: List[Processor], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  override def eipType: EipName = EipName.Otherwise
}
case class Bean(ref: Option[GenericAttributeValue[BlueprintBean]], method: Option[GenericAttributeValue[PsiMethod]], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor {
  override def eipType: EipName = EipName.To
}

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