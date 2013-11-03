package foo

/**
 * Contains functions which will be useful to remove code smells.
 */
object FunctionalUtil {

  /**
   * Helper to perform a mutation on T
   * @param f a function which takes an object of T and returns unit
   * @tparam T the type of object
   * @return the mutated object
   */
  def mutate[T](x: T)(f: T => Unit): T = {
    f(x)
    x
  }
}
