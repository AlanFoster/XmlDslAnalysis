package foo.intermediaterepresentation.model

import foo.intermediaterepresentation.model.types.{TypeEnvironment, NotInferred, Inferred, TypeInformation}
import foo.intermediaterepresentation.model.processors._

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

    case Choice(children, _, NotInferred) =>
      printWrapper("Choice", children, depth)

    case Choice(children, _, typeInformation:Inferred) =>
      printWrapper("Choice", children, depth, Some(typeInformation))

    case When(expression, children, _, NotInferred) =>
      printWrapper(s"When(${expression})", children, depth)

    case When(expression, children, _, typeInformation) =>
      printWrapper(s"When(${expression})", children, depth,  Some(typeInformation))

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
  private def printWrapper(rootName: String, children: List[Processor], depth: Int,
                           typeInformationWrapper: Option[TypeInformation] = None): String = {
    val parentWhitespace = fillWhitespace(depth)
    // Create the formatted children as expected
    val formattedChildren =
      children
      .map(child => print(child, depth + 1) + "\n")
      .mkString

    // Create the root element text, which may contain the semantic information
    val rootText = {
      parentWhitespace + rootName + {
        typeInformationWrapper match {
          case Some(Inferred(TypeEnvironment(bodyBefore, headersBefore), TypeEnvironment(bodyAfter, headersAfter))) =>
            val typeInformationWhitespace = fillWhitespace(depth + 1)
            "{ \n" +
              typeInformationWhitespace + s"(${bodyBefore}, ${headersBefore})\n" +
              typeInformationWhitespace + s"(${bodyAfter}, ${headersAfter})\n" +
              parentWhitespace + "}(\n"
          case _ => "(\n"
        }
      }
    }

    // Create the given which represents this wrapper
    rootText +
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