package foo.eip.model

/**
 * Provides a concrete implementation for printing the abstract model processor classes
 * in a human readable way
 */
object AbstractModelPrinter {
  /**
   * Prints the given processor definition in a human-readable way
   * @param root The root node
   * @return A human readable representation of the processor and children
   */
  def print(root: Processor):String = {
    print(root, 0)
  }

  /**
   * Prints the given processor definition in a human-readable way
   * @param processor The current processor node
   * @param depth The level of traversal with the tree
   * @return A human readable representation of the processor and children
   */
  private def print(processor: Processor, depth: Int):String = processor match {
    case Route(children, _, _) =>
      printWrapper("Route", children, depth)

    case Choice(children, _, _) =>
      printWrapper("Choice", children, depth)

    case When(expression, children, _, _) =>
      printWrapper(s"When(${expression})", children, depth)

    case default =>
      fillWhitespace(depth) + default.toString
  }

  /**
   * Creates a topmost node output within the tree
   * @param rootName The root node to use
   * @param children The children processors
   * @param depth The level of traversal with the tree
   * @return The formatted string with the rootName wrapper as the starting text
   */
  private def printWrapper(rootName: String, children: List[Processor], depth: Int): String = {
    val parentWhitespace = fillWhitespace(depth)
    // Create the formatted children as expected
    val formattedChildren =
      children
      .map(child => print(child, depth + 1) + "\n")
      .mkString

    // Create the end string
    parentWhitespace + rootName + "(\n" +
        formattedChildren +
        parentWhitespace +
    ")"
  }

  /**
   * Creates the separator for the given depth
   * @param depth The level of traversal with the tree
   * @return A string with the appropriate spacing for the given tree traversal depth
   */
  private def fillWhitespace(depth: Int): String =
    List.fill(depth)("     ").mkString
}