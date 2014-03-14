package foo.features

import scala.collection.JavaConverters._
import foo.language.psi.impl.SplitTextRange

/**
 * Defines operations which are more easily expressed in a function programming language
 * which will be useful for use within step definitions
 */
object PackageSplitterHelper {
  def convertTupleToSplitPackageTuple(previous: List[SplitTextRange]): java.util.List[SplitPackageTuple] =
    previous.map(tuple => new SplitPackageTuple(tuple.text, tuple.start, tuple.end)).asJava
}


