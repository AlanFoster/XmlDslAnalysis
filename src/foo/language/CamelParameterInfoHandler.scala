package foo.language

import com.intellij.lang.parameterInfo._
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.PsiElement
import com.intellij.openapi.project.DumbAware
import com.intellij.util.ArrayUtil
import com.intellij.psi.util.PsiTreeUtil
import foo.language.psi.{CamelFunctionArg, CamelFunctionCall}
import com.intellij.lang.ASTNode
import foo.CamelFunctions

/**
 * Method invocation Order
 * - findElementForParameterInfo - Returns an element of type `ParameterOwner`
 * - showParameterInfo - Called when the call to findElementForParameterInfo is non-null
 * - updateUI - if setItemsToShow is called
 */
class CamelParameterInfoHandler extends ParameterInfoHandler[CamelFunctionCall, CamelFunctionCall] with DumbAware {
  // Used to represent the identifiers for a highlighted argument index
  val (startHighlighting, endHighlighting) = ("<b>", "</b>")

  def updateUI(p: CamelFunctionCall, context: ParameterInfoUIContext) {
    //context.setUIComponentEnabled(false)
    val funcName = p.getFunctionName.getText

    val index = context.getCurrentParameterIndex

    for {
      func <- CamelFunctions.knownFunctions.find(_.functionName == funcName)
      prettyPrint = func.prettyPrint(index)
      (startBold, endBold) = (prettyPrint.indexOf(startHighlighting), prettyPrint.indexOf(endHighlighting))
      plainString = if (startBold != -1) func.prettyPrint(-1) else prettyPrint
    } {

      // Show the suggestion; The supplied number ranges will bold the pretty function text
      context.setupUIComponentPresentation(plainString,
        startBold, endBold - startHighlighting.size, false, false, false, context.getDefaultParameterColor)
    }
  }

  /**
   * Analyses the current function and argument selected and updates the context with the
   * corresponding argument index.
   * Note, this index is zero based
   * @param camelFunction The current camel function being edited by the user
   * @param context The operation context
   */
  def updateParameterInfo(camelFunction: CamelFunctionCall, context: UpdateParameterInfoContext) {
    if (context.getParameterOwner != camelFunction) context.removeHint()

    val offset = context.getOffset
    val functionArgs = camelFunction.getFunctionArgs

    // Tail recursive function which counts the total ASTNodes which contain
    // a reference to the Comma Element, in order to calculate the currently
    // selected argument index
    def countCommas(parent: ASTNode, maximumOffset: Int, count: Int): Int = parent match {
      case astNode: ASTNode if astNode.getStartOffset < maximumOffset => {
        val newCount = if (astNode.getElementType == CamelTypes.COMMA) count + 1 else count
        countCommas(astNode.getTreeNext, maximumOffset, newCount)
      }
      case _ => count
    }

    // Create the zero based index of the currently selected argument within the function list
    val index =
      if (functionArgs == null) 0
      else countCommas(functionArgs.getNode.getFirstChildNode, offset, 0)

    context.setCurrentParameter(index)
  }

  /**
   * Expected to call setItemsToShow and showHint.
   * If setItemsToShow contains the list of items to show, then the updateUI method will be
   * called with all of the relevant items returned from the list
   *
   * @param element The element of ParameterOwner
   * @param context The context
   */
  def showParameterInfo(element: CamelFunctionCall, context: CreateParameterInfoContext) {
    // Camel's language is not complex enough for overloading/multi params
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

    if (element == null) None
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
