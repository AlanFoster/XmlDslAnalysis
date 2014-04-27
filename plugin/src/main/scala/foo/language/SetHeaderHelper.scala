package foo.language

import com.intellij.psi.xml.XmlTag
import scala.annotation.tailrec
import com.intellij.openapi.editor.{ScrollType, Editor}

/**
 * Represents a valid parent/child relationship which can be used for creating a new
 * setHeader element
 * @param validParent A valid parent, which is not equal to a choice element
 * @param validChild The appropriate child element
 */
case class ValidParentChild(validParent: XmlTag, validChild: XmlTag)


object SetHeaderHelper {
  /**
   * Attempts to get the topmost valid parent and child in which a new header element can
   * successfully be inserted. For instance, this function will not return a location
   * which is 'between' a choice element for instance, ie child name is equal to 'when'
   *
   * @param child The current XML tag to check
   * @return The topmost valid parent child relationship that the XmlTag can be inserted
   */
  @tailrec
  final def getValidParent(child: XmlTag): ValidParentChild = {
    val parent = child.getParentTag
    if(child.getLocalName == "when") getValidParent(parent)
    else ValidParentChild(parent, child)
  }

  def resetCaret(editor: Editor, newOffset: Int) = {
    editor.getSelectionModel.removeSelection()
    editor.getCaretModel.moveToOffset(newOffset)
    editor.getScrollingModel.scrollToCaret(ScrollType.CENTER)
  }
}
