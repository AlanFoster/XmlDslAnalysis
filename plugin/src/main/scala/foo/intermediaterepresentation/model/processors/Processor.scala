package foo.intermediaterepresentation.model.processors

import foo.intermediaterepresentation.model.types.{TypeEnvironment, Inferred, TypeInformation}
import foo.intermediaterepresentation.model.references.Reference
import foo.intermediaterepresentation.model.EipName.EipName

/**
 * Created by a on 30/03/14.
 */
object Processor {
  def unapply(processor: Processor): Option[(Reference, TypeInformation)] = {
    Some(processor.reference, processor.typeInformation)
  }
}

trait Processor extends Mappable[Processor] {
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