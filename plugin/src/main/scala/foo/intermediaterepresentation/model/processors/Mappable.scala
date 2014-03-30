package foo.intermediaterepresentation.model.processors

/*******************************************************************
 * Processors
 *******************************************************************/

trait Mappable[T] {
  //def map[U](f: T => U): U
  def collectFirst(func: PartialFunction[T, Boolean]): Option[T]
}
