package foo.language.actions

import com.intellij.refactoring.RefactoringActionHandler
import com.intellij.openapi.project.Project
import com.intellij.openapi.editor.{SelectionModel, Editor}
import com.intellij.psi.{PsiElement, PsiFile}
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.refactoring.util.CommonRefactoringUtil
import com.intellij.psi.xml.XmlTag
import foo.language.Core.CamelPsiFile
import com.intellij.psi.util.PsiTreeUtil
import foo.language.generated.psi.{CamelExpression, CamelLiteral, CamelCamelFunction}
import foo.language.{SetHeaderHelper, ValidParentChild, Resolving}
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import foo.language.references.CamelRenameFactory
import com.intellij.psi.codeStyle.CodeStyleManager

/**
 * Provides support for extracting sub-expressions from the ACSL into the surrounding
 * apache camel DSL.
 *
 * For instance `<selection>LHS Expression</selection> == RHS Expression` should introduce
 * an intermediate header
 *
 * @see foo.language.actions.CamelRefactoringSupportProvider
 */
class CamelIntroduceExpressionVariable extends RefactoringActionHandler {


  /**
   * @inheritdoc
   *
   * Invokes the current Refactoring Action Handler support to extract a common subexpression into
   * a header within the surrounding DSL.
   */
  override def invoke(project: Project, editor: Editor, file: PsiFile, dataContext: DataContext): Unit = {
    val validationErrors = (
      validateSelection(editor, file)
      ++ validatePsiFile(editor, file)
    )

    val errorCreator = createError(project, editor)(_)
    if(validationErrors.nonEmpty) {
      validationErrors.foreach(errorCreator)
      return
    }

    println("Refactoring for :: " + file)
    val workErrors = doWork(project, editor, file.asInstanceOf[CamelPsiFile])
    // Show any additional errors to the user
    workErrors.foreach(errorCreator)
  }

  private def doWork(project: Project, camelEditor: Editor, camelFile: CamelPsiFile): List[String] = {
    val maximalExpressionOption = getMaximalExpression(camelEditor, camelFile)

    if(maximalExpressionOption.isEmpty) {
      return List("Invalid selection - Full expressions must be selected")
    }

    val maximalExpression = maximalExpressionOption.get

    println("Maximal expression :: " + maximalExpression + " ... " + maximalExpression.getText)

    val parentTag = getParentXmlTag(camelEditor, camelFile)

    parentTag match {
      case Some(tag) =>

        // Write actions must be done under readers/writers cycle
        ApplicationManager.getApplication.runWriteAction(new Runnable {
          override def run(): Unit = {

            // Register undo-support
            CommandProcessor.getInstance().executeCommand(project, new Runnable {
              override def run(): Unit = {
                val headerName: String = "id"
                val validParentChild =  insertHeader(headerName, maximalExpression.getText, tag)

                val newExpression: String = "${headers." + headerName + "}"

                // Replace the camel reference with a new Psi Element tree
                val replacement = CamelRenameFactory.createFresh(maximalExpression, maximalExpression.getTextRange, newExpression)
                maximalExpression.replace(replacement)

                // Format the element that was succesfully created
                CodeStyleManager.getInstance(project).reformat(validParentChild.validParent)

                // Update the caret to start after the newly created expression
                val updatedLocation = camelEditor.getSelectionModel.getSelectionStart + newExpression.length
                SetHeaderHelper.resetCaret(camelEditor, updatedLocation)
              }
            }, "Refactor Camel Expression", project)
          }
        })

        List()
      case None =>
        List("Refactoring only provided for XML references")
    }
  }

  private def getMaximalExpression(editor: Editor, camelFile: CamelPsiFile): Option[PsiElement] = {
    val model: SelectionModel = editor.getSelectionModel
    val (start, end) = (model.getSelectionStart, model.getSelectionEnd)

    val classesToTry = List(classOf[CamelExpression], classOf[CamelCamelFunction], classOf[CamelLiteral])

    val maximalExpression =
      classesToTry.foldLeft(Option.empty[PsiElement])((opt, clazz) => {
        if(opt.isEmpty) Option(PsiTreeUtil.findElementOfClassAtRange(camelFile, start, end, clazz))
        else opt
      })

    maximalExpression
  }

  private def getParentXmlTag(camelEditor: Editor, camelFile: CamelPsiFile): Option[XmlTag] = {
    Resolving.getParentXmlElement(camelFile)
  }

  /**
   * Creates a user error
   * @param project The current project
   * @param editor The current editor
   * @param error The error message to show to the user
   * @return Unit
   */
  private def createError(project: Project, editor: Editor)
                         (error: String) {
    CommonRefactoringUtil.showErrorHint(project, editor, error, "Error", null)
  }

  /**
   * Ensures that the currently selected text is valid as expected.
   * There must be currently selected text for this refactoring to occur
   *
   * @param editor The current editor
   * @param file The current file
   * @return The list of errors assocaited with this validation logic.
   *         If this list is empty, then validation has passed as expected
   */
  private def validateSelection(editor: Editor, file: PsiFile): List[String] = {
    val selectionModel = editor.getSelectionModel
    if(!selectionModel.hasSelection) List("No elements currently selected")
    else Nil
  }

  private def validatePsiFile(editor:Editor, file: PsiFile): List[String] = {
   if(!file.isInstanceOf[CamelPsiFile]) List("Refactoring support not provided for this file")
    else Nil
  }

  /**
   * Inserts a new header at the given location.
   * Note this method attempts to place the header appropriately
   *
   * @param headerName
   * @param currentLocation
   */
  private def insertHeader(headerName: String, expressionText: String, currentLocation: XmlTag):ValidParentChild ={
    val validParentChild = SetHeaderHelper.getValidParent(currentLocation)

    validParentChild match {
      case ValidParentChild(parent, child) =>
        val headerTag = createHeaderTag(headerName, expressionText, parent)
        parent.addBefore(headerTag, child)
        validParentChild
      case _ =>
        // TODO Add error to say that there is no valid refactoring - Shouldn't happen?
        ???
    }
  }


  /**
   * Creates a new instance of a setHeader XmlTag. Note this is element will not be attached to the
   * parent element, it must be done explicictly with parent.addBefore(...) etc
   *
   * @param headerName The value of the header name attribute
   * @param expressionText The expression text to use
   * @param parent The parent to be associated with.
   * @return A new XmlTag instance. This instance will additionally preserve namespace mappings
   */
  private def createHeaderTag(headerName: String, expressionText: String, parent: XmlTag): XmlTag = {
    val setHeaderTag = parent.createChildTag("setHeader", parent.getNamespace, null, false)
    setHeaderTag.setAttribute("headerName", headerName)
    val expressionTag = setHeaderTag.createChildTag("simple", setHeaderTag.getNamespace, expressionText, false)
    setHeaderTag.addSubTag(expressionTag, true)

    setHeaderTag
  }


  /**
   * @inheritdoc
   * Not implemented, refactoring support only provided based on user selection.
   */
  override def invoke(project: Project, elements: Array[PsiElement], dataContext: DataContext): Unit = ???
}
