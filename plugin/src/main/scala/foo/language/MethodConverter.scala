package foo.language

/**
 * Methods associated with the ability to convert methods into a given
 * return type, useful for OGNL expressions for instance.
 */
trait MethodConverter {
  private val GETTER_PREFIX = "get"

  /**
   * A functino which converts the given method into the getter name.
   * For instance `getFoo` will yield `foo`.
   * @param methodName An arbitrary method name
   * @return A some option of the getter name,
   *                   otherwise None as the method is not a getter
   */
  def convertGetterName(methodName: String): Option[String] = methodName match {
    case getter if methodName.startsWith(GETTER_PREFIX) =>
      Some(decapitalizeFirst(getter.substring(GETTER_PREFIX.length)))
    case _ => None
  }

  /**
   * De-capitalizes the first character of the given string
   * @param string The given string, may be empty.
   * @return A new string with the first letter de-capitalized
   */
  private def decapitalizeFirst(string: String): String =
    string
      .headOption
      .map(_.toLower + string.tail)
      .getOrElse(string)
}
