package foo.eip.editor

import com.intellij.openapi.fileEditor._
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.project.Project
import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.codeHighlighting.BackgroundEditorHighlighter
import java.beans.PropertyChangeListener
import javax.swing.{JPanel, JComponent}
import foo.eip.graph.{EipGraphCreator, VisualEipGraph}
import foo.eip.graph.loaders.IntellijIconLoader
import foo.FunctionalUtil._
import java.awt.{Color, GridLayout}
import scala.collection.JavaConverters._
import com.intellij.openapi.ui.popup.{Balloon, JBPopupFactory}
import com.intellij.openapi.ui.MessageType
import com.intellij.ui.awt.RelativePoint
import foo.dom.DomFileAccessor
import foo.dom.Model.Blueprint
import com.intellij.notification.Notifications
import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import org.jdom.Element
import com.intellij.openapi.fileEditor.impl.EditorWindow
import com.intellij.openapi.wm.WindowManager

/**
 * Creates and visualises the given XML DSl as a graph.
 */
class EipEditor(project: Project, virtualFile: VirtualFile) extends UserDataHolderBase with FileEditor {

  /**
   * Represents the constant tab name, for improved user experience
   */
  val tabName = "Eip Editor"

  /**
   * Define the graph container
   */
  val graphContainer =
    mutate(new JPanel()) { _.setLayout(new GridLayout(0, 1))}

  /**
   * Disposes resources associated with this editor
   */
  def dispose() {
    // swing will automatically dispose of itself within the expected manner
    graphContainer.removeAll()
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
   * Clears all state within the given graph container
   * */
  def deselectNotify() {
    // Clear all existing state within the graphContainer
    graphContainer.removeAll()
  }

  /**
   * @inheritdoc
   * Creates the given EIPGraph, and modifies the graphContainer
   */
  def selectNotify() {
    // Clear all existing state within the graphContainer
    graphContainer.removeAll()

    // Assume we are using a DOM representation of camel intially
    // Which allows for a EipDAG to be used within the VisualEipGraph, which could
    // be expended upon in the future to allow for Java DSL EIP representations etc
    val blueprintDom = DomFileAccessor.getBlueprintDomFile(project, virtualFile).get

    if(!isValid(blueprintDom)) {
      val message = "Note - At least one route, with one From definition is required."

/*  There doesn't seem a way to get the current editor tab ? :/
      val foo =
        WindowManager.getInstance()
          .getIdeFrame(project).asInstanceOf[IdeFrameImpl]
          .getRootPane.asInstanceOf[IdeRootPane]
          .getToolWindowsPane
      foo.getTool

      val ids = ToolWindowManager.getInstance(project).getToolWindowIds
      */

     // val statusBar = WindowManager.getInstance().getStatusBar(project).getComponent

/*
      val editorManager = FileEditorManagerEx.getInstanceEx(project)
      val splitters = editorManager.getSplitters

    val windows = editorManager.getWindows
      val first  = windows.head
      val foo = first.getTabbedPane.getTabs.getTabAt(0).getComponent
      foo.setBackground(Color.red)
*/

      JBPopupFactory.getInstance()
        .createHtmlTextBalloonBuilder(message, MessageType.WARNING, null)
        .createBalloon()
        .show(RelativePoint.getSouthEastOf(graphContainer), Balloon.Position.above)




/*      Notifications.Bus.notify(new Notification(
        "groupId",
        "TopHeading", "Bottom Heading",
        NotificationType.WARNING), project)*/


    }

    // Create the EipGraph if 'all is well'
    val eipGraph = new EipGraphCreator().createEipGraph(blueprintDom)

    // Create a new VisualEipGraph, with an IntellijIconLoader mixed in
    val visualEipGraph = (new VisualEipGraph(eipGraph) with IntellijIconLoader).createScrollableViewer

    graphContainer.add(visualEipGraph)
  }

  /**
   * Performs initial validation for the given blueprint xml file
   *
   * <em>Note</em>: we could potentially hook into the local inspection service etc
   *
   * @param blueprint the given blueprint dom element
   */
  def isValid(blueprint:Blueprint):Boolean =  {
    val routes = blueprint.getCamelContext.getRoutes.asScala
    !routes.isEmpty && routes.head.getFrom.exists()
  }


  /**
   *  The EIP editor is stateless. As such, this is noop.
   * @param state
   */
  def setState(state: FileEditorState) {
  }

  /**
   * Represents the component to show within the editor region
   * @return the EIP graph component container
   */
  def getComponent: JComponent = graphContainer

  /**
   * Places focus on the component when the tab is opened
   *
   * @return The EIP graph component
   */
  def getPreferredFocusedComponent: JComponent = null

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
   * Get the background highlighter for this editor
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