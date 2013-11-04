package foo.editor

import com.intellij.openapi.fileEditor.{FileEditorStateLevel, FileEditorState, FileEditorLocation, FileEditor}
import com.intellij.openapi.util.{IconLoader, UserDataHolderBase}
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.project.Project
import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.codeHighlighting.BackgroundEditorHighlighter
import java.beans.PropertyChangeListener
import javax.swing.{JLabel, JPanel, JComponent}
import foo.DomFileAccessor._
import foo.graph.{EipGraph}
import foo.graph.loaders.IntellijIconLoader

/**
 * Creates and visualises the given XML DSl as a graph.
 */
class EipEditor(project: Project, virtualFile: VirtualFile) extends UserDataHolderBase with FileEditor {

  /**
   * Represents the constant tab name, for improved user experience
   */
  val tabName = "Eip Editor"

  /**
   * Represents the graph component within this tab
   */
  val graph = (new EipGraph(getBlueprintDomFile(project, virtualFile).get) with IntellijIconLoader).createScrollableViewer

  /**
   * Disposes resources associated with this editor
   */
  def dispose() {
    // noop - swing will automatically dispose of itself when de-referenced
  }

  /**
   * Removes the specified listener
   * @param listener The listener
   */
  def removePropertyChangeListener(listener: PropertyChangeListener) {
    // noop
  }

  /**
   * Adds the specified listener
   * @param listener The listener
   */
  def addPropertyChangeListener(listener: PropertyChangeListener) {
    // noop
  }

  /**
   * @inheritdoc
   */
  def deselectNotify() {
    // noop
  }

  /**
   * @inheritdoc
   */
  def selectNotify() {
    // noop
  }

  /**
   *  The EIP editor is stateless. As such, this is noop.
   * @param state
   */
  def setState(state: FileEditorState) {

  }

  /**
   * Represents the component to show within the editor region
   * @return the EIP graph component
   */
  def getComponent: JComponent = graph

  /**
   * Places focus on the component when the tab is opened
   *
   * @return The EIP graph component
   */
  def getPreferredFocusedComponent: JComponent = graph

  /**
   * Associates a name with the created editor tab
   *
   * @return A constant string for a better user experience
   */
  def getName: String = tabName

  /**
   * The EIP editor is stateless. As such, this is noop.
   * @param level
   * @return The default FileEditorState instance
   */
  def getState(level: FileEditorStateLevel): FileEditorState = FileEditorState.INSTANCE

  /**
   * The EIP diagram is readonly.
   * @return always false
   */
  def isModified: Boolean = false

  /**
   * The EIP Diagram is readonly.
   *
   * @return true
   */
  def isValid: Boolean = true

  /**
   * Get the background highlightor for this editor
   *
   * @return Noop, no highlighter is required for the EIP diagram.
   */
  def getBackgroundHighlighter: BackgroundEditorHighlighter = null

  /**
   * Represents the user's location within the editor
   * @return null, as the editor is not text based
   */
  def getCurrentLocation: FileEditorLocation = null

  /**
   * Get the structure view builder
   *
   * @return null, as the EIP does not have a structured view, as the EIP represents a tree already.
   */
  def getStructureViewBuilder: StructureViewBuilder = null
}
