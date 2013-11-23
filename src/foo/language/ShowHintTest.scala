package foo.language

import com.intellij.lang.parameterInfo._
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.PsiElement
import com.intellij.openapi.project.DumbAware
import com.intellij.util.ArrayUtil
import com.intellij.psi.util.PsiTreeUtil

/**
 * Method invocation Order
 *  - findElementForParameterInfo - Returns an element of type `ParameterOwner`
 *  - showParameterInfo - Called when the call to findElementForParameterInfo is non-null
 *   - upUI - if setItemsToShow is called
 */
class ShowHintTest extends ParameterInfoHandler[PsiElement, PsiElement] with DumbAware {
  def updateUI(p: PsiElement, context: ParameterInfoUIContext) {
    //context.setUIComponentEnabled(false)
    // TODO to use bold, <b>...</b>
    context.setupUIComponentPresentation("ummmm(<b>todo</b>, arg1)", 0, 0, false, false, false, context.getDefaultParameterColor)
  }

  def updateParameterInfo(o: PsiElement, context: UpdateParameterInfoContext) {
    // context.setCurrentParameter()
    print("hi")
  }

  def showParameterInfo(element: PsiElement, context: CreateParameterInfoContext) {
    context.setItemsToShow(Array(element))


    context.showHint(element, element.getTextRange.getStartOffset, this)
  }

  def couldShowInLookup: Boolean = true

  def getParametersForLookup(item: LookupElement, context: ParameterInfoContext): Array[AnyRef] = {
    Array("one", "two", "three")
  }

  def getParametersForDocumentation(p: PsiElement, context: ParameterInfoContext): Array[AnyRef] =
    ArrayUtil.EMPTY_OBJECT_ARRAY

  def findElementForParameterInfo(context: CreateParameterInfoContext): PsiElement = {
  // context.setItemsToShow(Array("One", "Two", "Three"))
    val file = context.getFile
    val offset = context.getEditor.getCaretModel.getOffset
    val element = file.findElementAt(offset)
    element
    //null
  }

  /**
   * Used to find the element when parameter info is being updated.
   * For instance in the scenario of the current cursor element no longer
   * being the original element which would be returend by  findElementForParameterInfo
   *
   * If null, causes the hint to be removed
   */
  def findElementForUpdatingParameterInfo(context: UpdateParameterInfoContext): PsiElement = {
  //  context.setItemsToShow(Array("One", "Two", "Three"))
    val file = context.getFile
    val offset = context.getEditor.getCaretModel.getOffset
    val element = file.findElementAt(offset)
    element
  }

  def getParameterCloseChars: String = "{},);\n"

  def tracksParameterIndex(): Boolean = true
}
