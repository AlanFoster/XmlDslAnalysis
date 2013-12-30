package foo.features

import scala.collection.JavaConverters._

/**
 * Defines operations which are more easily expressed in a function programming language
 * which will be useful for use within step definitions
 */
object PackageSplitterHelper {
  def convertTupleToSplitPackageTuple(previous: List[(String, Int, Int)]): java.util.List[SplitPackageTuple] =
    previous.map(tuple => new SplitPackageTuple(tuple._1, tuple._2, tuple._3)).asJava
}


