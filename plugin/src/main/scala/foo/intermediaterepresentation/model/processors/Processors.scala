package foo.intermediaterepresentation.model.processors

import com.intellij.psi.PsiMethod
import foo.dom.Model.BlueprintBean
import com.intellij.util.xml.GenericAttributeValue
import foo.intermediaterepresentation.model.types.{TypeEnvironment, Inferred, NotInferred, TypeInformation}
import foo.intermediaterepresentation.model.references.Reference
import foo.intermediaterepresentation.model.expressions.Expression
import foo.intermediaterepresentation.model.EipType
import foo.intermediaterepresentation.model.EipType.EipType

/**
 * Represents the base trait of a processor.
 * Note this trait is within the same file as its extending classes in order
 * to ensure compile time errors when partial functions don't match all
 * possible cases.
 */
sealed trait Processor extends Mappable[Processor] {
  /**
   * The associated type information with this processor
   */
  val typeInformation: TypeInformation

  /**
   * A reference to the representation that this Processor was
   * originated from
   */
  val reference: Reference

  /**
   * The high level EipType associated with this Processor.
   * More commonly this will be equal to the Translator Eip
   */
  val eipType: EipType

  /**
   * The pretty name of this Processor
   * @return The human readable String associated with this Processor
   *          for instance, SetHeader -> Set Header
   */
  lazy val prettyName: String = {
    val simpleName = getClass.getSimpleName
    val prettyName = simpleName.map(c =>
      if(c.isUpper) " " + c else c.toString
    ).mkString.tail
    prettyName
  }

  /**
   * @return The before environment type information associated
   *         with this processor
   */
  def before: Option[TypeEnvironment] = typeInformation match {
    case Inferred(before, _) =>
      Option(before)
    case _ => None
  }

  /**
   * @return The after environment type information associated
   *         with this processor
   */
  def after: Option[TypeEnvironment] = typeInformation match {
    case Inferred(_, after) =>
      Option(after)
    case _ => None
  }

  /**
   * Extracts the headers associated with the current processor
   * @return An Option, as the semantic information may not have been inferred
   *         currently.
   */
  def headers: Option[Map[String, (String, Reference)]] =
    before.map(_.headers)

  /**
   * Extracts the out headers associated with the current processor
   * @return An Option, as the semantic information may not have been inferred
   *         currently.
   */
  def outHeaders: Option[Map[String, (String, Reference)]] =
    after.map(_.headers)

  /**
   * Extracts the current body set associated with the current processor
   * @return An option, as the semantic information may not have been inferred
   *         currently.
   */
  def bodies: Option[Set[String]] = before flatMap  {
    case TypeEnvironment(bodySet, _) =>
      Some(bodySet)
    case _ => None
  }

  /**
   * Extracts the out body set associated with the current processor
   * @return An option, as the semantic information may not have been inferred
   *         currently.
   */
  def outBodies: Option[Set[String]] = after flatMap {
    case TypeEnvironment(bodySet, _) =>
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
        case Choice(children, otherwiseOption, _, _) =>
          otherwiseOption match {
            case Some(otherwise) => findChild(otherwise :: children)(func)
            case None => findChild(children)(func)
          }
        case When(_, children, _, _) =>
          findChild(children)(func)
        case Otherwise(children, _, _) =>
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

final case class Route(children: List[Processor], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipType = EipType.Route
}
final case class From(uri: Option[String], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor {
  val eipType: EipType = EipType.From
}
final case class To(uri: Option[String], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipType = EipType.To
}
final case class RemoveHeader(headerName: Option[String], reference: Reference, typeInformation: TypeInformation = NotInferred) extends Processor {
  val eipType: EipType = EipType.Translator
}
final case class SetBody(expression: Expression, reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipType = EipType.Translator
}
final case class SetHeader(headerName: Option[String], expression: Expression, reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipType = EipType.Translator
}
final case class Choice(whens: List[When], otherwiseOption: Option[Otherwise], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipType = EipType.Choice
  def getChildren: List[Processor] = {
    otherwiseOption match {
      case Some(otherwise) => whens :+ otherwise
      case None => whens
    }
  }
}
final case class When(expression: Expression, children: List[Processor], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipType = EipType.When
}
final case class Otherwise(children: List[Processor], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor{
  val eipType: EipType = EipType.Otherwise
}
final case class Bean(ref: Option[GenericAttributeValue[BlueprintBean]], method: Option[GenericAttributeValue[PsiMethod]],
                      reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor {
  val eipType: EipType = EipType.To
}
final case class Log(message: Option[String], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor {
  val eipType: EipType = EipType.Misc
}
final case class WireTap(uri: Option[String], reference:Reference, typeInformation: TypeInformation = NotInferred) extends Processor {
  val eipType: EipType = EipType.WireTap
}

/**
 * Represents the human readable defaults associated with attributes that were not
 * successfully set as expected.
 */
object DefaultAttributes {
  val NotValid = "[Empty]"
  val uri = "error:" + NotValid
  val EmptyHeaderName = NotValid
}
