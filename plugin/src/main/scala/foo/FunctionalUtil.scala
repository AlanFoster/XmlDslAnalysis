package foo

/**
 * Contains functions which will be useful to remove code smells.
 */
object FunctionalUtil {

  /**
   * Helper to perform a mutation on T
   * @param fs The functions which take an object of T and returns unit
   * @tparam T the type of object
   * @return the mutated object
   */
  def mutate[T](x: T)(fs: (T => Unit)*): T = {
    fs.foreach(_(x))
    x
  }
}
