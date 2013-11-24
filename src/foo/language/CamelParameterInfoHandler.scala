package foo.language

import com.intellij.lang.parameterInfo._
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.PsiElement
import com.intellij.openapi.project.DumbAware
import com.intellij.util.ArrayUtil
import com.intellij.psi.util.PsiTreeUtil
import foo.language.psi.{CamelFunctionArg, CamelFunctionCall}
import com.intellij.lang.ASTNode

/**
 * Method invocation Order
 *  - findElementForParameterInfo - Returns an element of type `ParameterOwner`
 *  - showParameterInfo - Called when the call to findElementForParameterInfo is non-null
 *   - updateUI - if setItemsToShow is called
 */
class CamelParameterInfoHandler extends ParameterInfoHandler[CamelFunctionCall, CamelFunctionCall] with DumbAware {
  // Used to represent the identifiers for a highlighted argument index
  val (startHighlighting, endHighlighting) = ("<b>", "</b>")

  trait CamelType[A] {
    def argName: String
    def prettyType: String
    def prettyPrint: String = s"${argName}: ${prettyType}"
  }
  case class CamelString(override val argName: String) extends CamelType[String] {
    def prettyType: String = "String"
  }
  case class CamelPackage(override val argName: String) extends CamelType[Class[_]]{
    def prettyType: String = "Class"
  }

  case class CamelFunction(functionName: String, arguments: CamelType[_]*) {
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
          if(i == argIndex) startHighlighting + argument.prettyPrint + endHighlighting
          else argument.prettyPrint
      })

      s"${functionName}(${prettyArguments.mkString(", ")})"
    }
  }

  def updateUI(p: CamelFunctionCall, context: ParameterInfoUIContext) {
    //context.setUIComponentEnabled(false)
    val funcName = p.getFunctionName.getText

    val index = context.getCurrentParameterIndex

    val knownFunctions = List(
        CamelFunction("bodyAs", CamelPackage("type")),
        CamelFunction("bodyAsMandatory", CamelPackage("type")),
        CamelFunction("headerAs", CamelString("key"), CamelPackage("type"))
    )

    for {
      func <- knownFunctions.find(_.functionName == funcName)
      prettyPrint = func.prettyPrint(index)
      (startBold, endBold) = (prettyPrint.indexOf(startHighlighting), prettyPrint.indexOf(endHighlighting))
      plainString = if(startBold != -1) func.prettyPrint(-1) else prettyPrint
    } {

      // Show the suggestion; The supplied number ranges will bold the pretty function text
      context.setupUIComponentPresentation(plainString,
        startBold, endBold - startHighlighting.size,  false, false, false, context.getDefaultParameterColor)
    }
  }

  /**
   * Analyses the current function and argument selected and updates the context with the
   * corresponding argument index.
   * Note, this index is zero baswed
   * @param camelFunction The current camel function being edited by the user
   * @param context The operation context
   */
  def updateParameterInfo(camelFunction: CamelFunctionCall, context: UpdateParameterInfoContext) {
    if(context.getParameterOwner != camelFunction) context.removeHint()

    val offset = context.getOffset
    val functionArgs = camelFunction.getFunctionArgs

    // Tail recursive function which counts the total ASTNodes which contain
    // a reference to the Comma Element, in order to calculate the currently
    // selected argument index
    def countCommas(parent: ASTNode, maximumOffset: Int, count: Int): Int = parent match {
      case astNode:ASTNode if astNode.getStartOffset < maximumOffset => {
        val newCount = if(astNode.getElementType == CamelTypes.COMMA) count + 1 else count
        countCommas(astNode.getTreeNext, maximumOffset, newCount)
      }
      case _ => count
    }

    // Create the zero based index of the currently selected argument within the function list
    val index =
      if(functionArgs == null) 0
      else countCommas(functionArgs.getNode.getFirstChildNode, offset, 0)

    context.setCurrentParameter(index)
  }

  def showParameterInfo(element: CamelFunctionCall, context: CreateParameterInfoContext) {
    context.setItemsToShow(Array(element))
    context.showHint(element, element.getTextRange.getStartOffset, this)
  }

  def couldShowInLookup: Boolean = true

  def getParametersForLookup(item: LookupElement, context: ParameterInfoContext): Array[AnyRef] =
    ArrayUtil.EMPTY_OBJECT_ARRAY

  def getParametersForDocumentation(p: CamelFunctionCall, context: ParameterInfoContext): Array[AnyRef] =
    ArrayUtil.EMPTY_OBJECT_ARRAY

  /**
   * Called first. This method is called initially to provide the element for ParameterInfo,
   * which will be used in the later functions
   *
   * @param context The context
   * @return The parameter info element used within this class
   */
  def findElementForParameterInfo(context: CreateParameterInfoContext): CamelFunctionCall = {
    findFunctionCall(context).getOrElse(null)
  }


  def findFunctionCall(context: ParameterInfoContext): Option[CamelFunctionCall] = {
    val file = context.getFile
    val offset = context.getOffset
    val element = file.findElementAt(offset)

    if(element == null) None
    else Option(PsiTreeUtil.getParentOfType(element, classOf[CamelFunctionCall]))
  }

  /**
   * Used to find the element when parameter info is being updated.
   * For instance in the scenario of the current cursor element no longer
   * being the original element which would be returend by  findElementForParameterInfo
   *
   * If null, causes the hint to be removed
   */
  def findElementForUpdatingParameterInfo(context: UpdateParameterInfoContext): CamelFunctionCall = {
    findFunctionCall(context).getOrElse(null)
  }

  def getParameterCloseChars: String = ",)"

  def tracksParameterIndex(): Boolean = true
}
