package foo

import foo.language.psi.CamelFQCN
import com.intellij.psi.tree.IElementType
import foo.language.CamelTypes

trait CamelArgument {
  def argName: String
  def requiredElementType: IElementType
  def prettyType: String
  def prettyPrint: String = s"${argName}: ${prettyType}"
}

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
  def prettyPrint(argIndex: Int) = {
    val prettyArguments = arguments.zipWithIndex.map({
      case (argument, i) =>
        if (i == argIndex) startHighlighting + argument.prettyPrint + endHighlighting
        else argument.prettyPrint
    })

    s"${functionName}(${prettyArguments.mkString(", ")})"
  }
}

case class CamelString(override val argName: String) extends CamelArgument {
  def prettyType: String = "String"
  def requiredElementType = CamelTypes.STRING
}

case class CamelPackage(override val argName: String) extends CamelArgument {
  def prettyType: String = "Class"
  def requiredElementType = CamelTypes.FQCN
}

object CamelFunctions {
  val knownFunctions = List(
    CamelFunction("bodyAs", List(CamelPackage("type"))),
    CamelFunction("mandatoryBodyAs", List(CamelPackage("type"))),
    CamelFunction("headerAs", List(CamelString("key"), CamelPackage("type")))
  )
}
