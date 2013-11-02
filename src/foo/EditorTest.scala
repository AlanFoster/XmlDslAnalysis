package foo

import com.intellij.openapi.util.{Disposer, UserDataHolderBase}
import com.intellij.openapi.fileEditor._
import com.intellij.openapi.project.{DumbAware, Project}
import org.jdom.Element
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.codeHighlighting.BackgroundEditorHighlighter
import java.beans.PropertyChangeListener
import javax.swing.{JLabel, JPanel, JEditorPane, JComponent}
import com.intellij.psi.PsiFile

class EditorTest extends FileEditorProvider with DumbAware {
  val editorID = "EipEditor"

  def writeState(state: FileEditorState, project: Project, targetElement: Element) {

  }

  def disposeEditor(editor: FileEditor) {
  }

  def accept(project: Project, file: VirtualFile): Boolean = true

  def createEditor(project: Project, file: VirtualFile): FileEditor = new EditorFileEditor(project, file)

  def readState(sourceElement: Element, project: Project, file: VirtualFile): FileEditorState = FileEditorState.INSTANCE

  def getEditorTypeId: String = editorID

  def getPolicy: FileEditorPolicy = FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR
}

class EditorFileEditor(project: Project, file: VirtualFile) extends UserDataHolderBase with FileEditor {
  val name = "EIP Diagram"

  val panel = new JPanel()
  panel.add(new JLabel("Testing EIP Editor"))

  def dispose(){

  }

  def removePropertyChangeListener(listener: PropertyChangeListener){}

  def addPropertyChangeListener(listener: PropertyChangeListener){}

  def deselectNotify(){}

  def selectNotify(){}

  def setState(state: FileEditorState) {}

  def getComponent: JComponent = panel

  def getPreferredFocusedComponent: JComponent = {
    panel
  }

  def getName: String = name

  def getState(level: FileEditorStateLevel): FileEditorState = FileEditorState.INSTANCE

  def isModified: Boolean = false

  def isValid: Boolean = true

  def getBackgroundHighlighter: BackgroundEditorHighlighter = null

  def getCurrentLocation: FileEditorLocation = null

  def getStructureViewBuilder: StructureViewBuilder = null
}