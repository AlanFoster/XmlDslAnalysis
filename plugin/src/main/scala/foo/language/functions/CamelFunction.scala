package foo.language.functions

/**
 * A case class which represents an available camel function within the Apache
 * Camel Simple language
 *
 * @param functionName The associated function name
 * @param arguments The arguments associated with this function
 */
case class CamelFunction(functionName: String, arguments: List[CamelArgument], documentation: String) {
  // Used to represent the identifiers for a highlighted argument index
  val (startHighlighting, endHighlighting) = ("<b>", "</b>")

  /**
   * Pretty prints the current camel function.
   * @param argIndex The currently selected index. Useful in the case of ParameterInfoHandler class.
   *                 Note, this index is zero based.
   * @return A string representation of the method and the available arguments. If the argIndex is
   *         valid, then the relevant argument will be bolded
   */
  def prettyPrint(argIndex: Int = -1) = {
    val prettyArguments = arguments.zipWithIndex.map({
      case (argument, i) =>
        if (i == argIndex) startHighlighting + argument.prettyPrint + endHighlighting
        else argument.prettyPrint
    })

    s"${functionName}(${prettyArguments.mkString(", ")})"
  }
}