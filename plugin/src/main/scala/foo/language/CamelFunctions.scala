package foo.language

import com.intellij.psi.tree.IElementType
import foo.language.generated.CamelTypes
import foo.intermediaterepresentation.model.types.CamelStaticTypes.ACSLFqcn
import com.intellij.psi.CommonClassNames

/**
 * Represents a CamelArgument which can be used within the context of a camel function
 */
trait CamelArgument {
  /**
   * The name associated with the camel argument
   * @return The name associated with teh camel argument
   */
  def argName: String

  /**
   * The type definition associated with this argument
   * @return The required element type information
   */
  def requiredElementType: IElementType

  /**
   * Optionally the required FQCN type
   * @return The required FQCN Type
   */
  def requiredFQCN: ACSLFqcn

  /**
   * Computes the prettified version of this argument's type information
   * @return A human-readible version of this argument type information
   */
  def prettyType: String

  /**
   * Computes the overall argument name and type information
   * for this argument name
   * @return The human readible type information for this argument
   */
  def prettyPrint: String = s"${argName}: ${prettyType}"
}

/**
 * A case class which represents an available camel function within the Apache
 * Camel Simple language
 *
 * @param functionName The associated function name
 * @param arguments The arguments associated with this function
 */
case class CamelFunction(functionName: String, arguments: List[CamelArgument]) {
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

/**
 * A case class implementation of a CamelArgument which represents a String
 * which should be used within a camel function
 * @param argName The argument name associated with this Argument
 */
case class CamelString(override val argName: String) extends CamelArgument {
  /**
   * {@inheritdoc}
   */
  def prettyType: String = "String"
  /**
   * {@inheritdoc}
   */
  def requiredElementType = CamelTypes.STRING

  /**
   * Optionally the required FQCN type
   * @return The required FQCN Type
   */
  override def requiredFQCN: ACSLFqcn = CommonClassNames.JAVA_LANG_STRING
}

/**
 * A case class implementation of a CamelArgument, which represents a FQCN which
 * should be used as a camel argument within the context of a camel function
 *
 * @param argName The argument name associated with this Argument
 */
case class CamelPackage(override val argName: String) extends CamelArgument {
  /**
   * {@inheritdoc}
   */
  def prettyType: String = "Class"

  /**
   * {@inheritdoc}
   */
  def requiredElementType = CamelTypes.FQCN

  /**
   * {@inheritdoc}
   */
  override def requiredFQCN: ACSLFqcn = ""
}

/**
 * Defines the currently known list of functions, and associated type information
 * for argument types that apache camel allows
 */
object CamelFunctions {
  val knownFunctions = List(
    CamelFunction("bodyAs", List(CamelPackage("type"))),
    CamelFunction("mandatoryBodyAs", List(CamelPackage("type"))),
    CamelFunction("headerAs", List(CamelString("key"), CamelPackage("type")))
  )
}
