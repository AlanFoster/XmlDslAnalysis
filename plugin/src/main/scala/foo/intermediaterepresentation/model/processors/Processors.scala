package foo.intermediaterepresentation.model.processors

import com.intellij.psi.PsiMethod
import foo.dom.Model.BlueprintBean
import com.intellij.util.xml.GenericAttributeValue
import foo.intermediaterepresentation.model.types.{TypeEnvironment, Inferred, NotInferred, TypeInformation}
import foo.intermediaterepresentation.model.references.Reference
import foo.intermediaterepresentation.model.expressions.Expression
import foo.intermediaterepresentation.model.EipName
import foo.intermediaterepresentation.model.EipName.EipName

/**
 * Represents the base trait of a processor.
 * Note this trait is within the same file as its extending classes in order
 * to ensure compile time errors when partial functions don't match all
 * possible cases.
 */
sealed trait Processor extends Mappable[Processor] {
  val typeInformation: TypeInformation
  val reference: Reference
  val eipType: EipName

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

/**
 * Processor Object implementation provides an unapply method for pattern matching
 * on the trait instance.
 */
object Processor {
  def unapply(processor: Processor): Option[(Reference, TypeInformation)] = {
    Some(processor.reference, processor.typeInformation)
  }
}


/*************************************************************************
 * Defines the currently defined set of processors within the intermediate
 * representation of the apache camel language
 *************************************************************************/

case class Route(children: List[Processor], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipName = EipName.Route
}
case class From(uri: String, reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor {
  val eipType: EipName = EipName.From
}
case class To(uri: String, reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipName = EipName.To
}
case class SetBody(expression: Expression, reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipName = EipName.Translator
}
case class SetHeader(headerName: String, expression: Expression, reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipName = EipName.Translator
}
case class Choice(whens: List[When], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipName = EipName.choice
}
case class When(expression: Expression, children: List[Processor], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipName = EipName.When
}
case class Otherwise(children: List[Processor], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipName = EipName.Otherwise
}
case class Bean(ref: Option[GenericAttributeValue[BlueprintBean]], method: Option[GenericAttributeValue[PsiMethod]], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor {
  val eipType: EipName = EipName.To
}