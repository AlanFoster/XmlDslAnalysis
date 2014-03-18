package foo.eip.model

object AbstractModelPrinter {
  def print(root: Processor):String = {
    print(root, 0)
  }

  private def print(processor: Processor, depth: Int):String = processor match {
    case Route(children) =>
      printWrapper("Route", children, depth)

    case Choice(children) =>
      printWrapper("Choice", children, depth)

    case When(expression, children) =>
      printWrapper(s"When(${expression})", children, depth)

    case default =>
      fillWhitespace(depth) + default.toString
  }

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