package foo.language.annotators

import com.intellij.lang.annotation.{AnnotationHolder, Annotator}
import com.intellij.psi.{PsiFile, PsiElement}
import foo.language.references.header.CamelHeaderReference
import foo.language.Core.{CamelPsiFile, CamelFileType}
import foo.language.annotators.AnnotatorHelper._
import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.editor.Editor
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil
import com.intellij.codeInsight.template.{Template, TemplateEditingAdapter, TemplateEditingListener, TemplateManager}
import foo.language.templates.CamelTemplateManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import foo.language.{SetHeaderHelper, Resolving}

/**
 * Provides the ability to quick fix headers within ACSL which do not resolve successfully.
 * This will attempt to add a set header element to the surrounding DSL to fix this.
 */
class CreateHeaderAnnotator extends Annotator {
  override def annotate(element: PsiElement, holder: AnnotationHolder): Unit = {
    // Only consider elements which match our Camel Language
    if(!(element.getContainingFile.getFileType == CamelFileType)) return

    val unresolvedHeaderReferences = element.getReferences.collect({
      case reference: CamelHeaderReference =>
        reference
    }).filterNot(isResolved)

    unresolvedHeaderReferences.foreach(reference => {
      createQuickfixWarning(element, reference, holder)
    })
  }

  private def createQuickfixWarning(element: PsiElement, psiReference: CamelHeaderReference, holder: AnnotationHolder) = {
    val headerName = element.getText
    holder
      .createWeakWarningAnnotation(element, null)
      .registerFix(new CreateCamelHeaderQuickFix(element, headerName))
  }
}

private class CreateCamelHeaderQuickFix(element: PsiElement, headerName: String) extends BaseIntentionAction {
  private val FAMILY_NAME = "Camel Quickfix"
  private val TEXT = "Introduce Header %s"

  override def invoke(project: Project, editor: Editor, file: PsiFile): Unit = {

    // Write actions must be done under readers/writers cycle
    ApplicationManager.getApplication.runWriteAction(new Runnable {
      override def run(): Unit = {

        // Register undo-support
        CommandProcessor.getInstance().executeCommand(project, new Runnable {
          override def run(): Unit = {
            insertHeader(project, editor, file.asInstanceOf[CamelPsiFile])
          }
        }, "Insert Camel Header", project)
      }
    })
  }

  private def insertHeader(project: Project, camelEditor: Editor, camelFile: CamelPsiFile) {
    val parentTag = Resolving.getParentXmlElement(camelFile)

    parentTag match {
      case Some(tag) =>
        val validParentChild = SetHeaderHelper.getValidParent(tag)

        val (currentCaret, tempInsertCaret) = (camelEditor.getCaretModel.getOffset, validParentChild.validChild.getTextOffset)

        // Templates are inserted at the caret position, so we must move the caret to the new insert location
        val xmlEditor = InjectedLanguageUtil.getTopLevelEditor(camelEditor)
        SetHeaderHelper.resetCaret(xmlEditor, tempInsertCaret)

        val templateManager = TemplateManager.getInstance(project)
        val template = CamelTemplateManager.createSetHeader(Some(headerName), None, None)

        // Provide a listener so when the template editing is complete, the caret will be placed appropriately
        templateManager.startTemplate(xmlEditor, template, new TemplateEditingCaretListener(camelEditor, currentCaret))
      case _ =>
        // TODO Show a user warning that insert header support only provided for XML Files
    }
  }


  /**
   * Always true, as this is an internal quick fix
   */
  override def isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean =
    true

  /****************************************************************************
    * Associated constants which will be used within the quick fix editor dialog
    ****************************************************************************/
  /**
   * @inheritdoc
   */
  override def getFamilyName: String = FAMILY_NAME

  /**
   * @inheritdoc
   */
  override def getText: String = TEXT.format(headerName)
}

/**
 * Provides a template editing listener that places the caret back to its orignial target
 * when the template editing has completed
 *
 * @param editor The current editor
 * @param newCaret The old target caret position
 */
class TemplateEditingCaretListener(editor: Editor, newCaret: Int) extends TemplateEditingAdapter {
  override def templateFinished(template: Template, brokenOff: Boolean): Unit =
    correctCaret()

  override def templateCancelled(template: Template): Unit =
    correctCaret()

  // Correct the cursor back to original place after the new header has been created
  def correctCaret() ={
    SetHeaderHelper.resetCaret(editor, newCaret)
  }
}