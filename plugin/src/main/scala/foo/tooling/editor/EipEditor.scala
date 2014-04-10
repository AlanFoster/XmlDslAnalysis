package foo.tooling.editor

import com.intellij.openapi.fileEditor._
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.project.Project
import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.codeHighlighting.BackgroundEditorHighlighter
import java.beans.PropertyChangeListener
import javax.swing._
import foo.FunctionalUtil._
import java.awt.{FlowLayout, BorderLayout, GridLayout}
import scala.collection.JavaConverters._
import com.intellij.openapi.ui.popup.{Balloon, JBPopupFactory}
import com.intellij.openapi.ui.MessageType
import com.intellij.ui.awt.RelativePoint
import foo.dom.DomFileAccessor
import foo.dom.Model.Blueprint
import foo.intermediaterepresentation.converter.DomAbstractModelConverter
import foo.intermediaterepresentation.typeInference.DataFlowTypeInference
import foo.tooling.graphing.{GraphCreator, EipGraphCreator}
import java.awt.event.{ActionEvent, ActionListener}
import com.intellij.ui.components.JBComboBoxLabel
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressManager

/**
 * Creates and visualises the given XML DSl as a graph.
 */
class EipEditor(project: Project, virtualFile: VirtualFile, graphCreators: List[GraphCreator]) extends UserDataHolderBase with FileEditor {

  var currentlySelectedGraphCreator = graphCreators.head

  /**
   * Represents the constant tab name, for improved user experience
   */
  val tabName = "Eip Editor"

  /**
   * Define the graph container
   */
  val graphContainer = mutate(new JPanel()) { _.setLayout(new GridLayout(0, 1))}

  /**
   * Define the entire window, consisting of the editable options + graph container
   */
  val entireContainer = {
    val container = new JPanel(new FlowLayout())
    container.setLayout(new BorderLayout())
    container.add(createOptions, BorderLayout.NORTH)
    container.add(graphContainer, BorderLayout.CENTER)
    container
  }

  /**
   * Disposes resources associated with this editor
   */
  def dispose() {
    // swing will automatically dispose of itself within the expected manner
    entireContainer.removeAll()
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
    generateGraph(currentlySelectedGraphCreator)
  }

  private def generateGraph(graphCreator: GraphCreator) {
      // Assume we are using a DOM representation of camel intially
      // Which allows for a EipDAG to be used within the VisualEipGraph, which could
      // be expended upon in the future to allow for Java DSL EIP representations etc
      val blueprintDomOption = DomFileAccessor.getBlueprintDomFile(project, virtualFile)
      if(!validateInput(blueprintDomOption)) return

      ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable {
      override def run(): Unit = {
        ProgressManager.getInstance().getProgressIndicator.setText("Initializing...")

        // Clear all existing state within the graphContainer
        graphContainer.removeAll()

        val blueprintDom = blueprintDomOption.get

        ProgressManager.getInstance().getProgressIndicator.setText("Creating Model...")

        // Instantiate the methods of creating an abstract dom model and semantic information for later DI
        val modelConverter = new DomAbstractModelConverter()
        val dataFlowInference = new DataFlowTypeInference()

        ProgressManager.getInstance().getProgressIndicator.setText(s"Creating ${graphCreator.prettyName}...")

        // Create and pretty print the produced Eip DAG for the given DOM file
        val eipGraph = new EipGraphCreator()
          .createEipGraph(modelConverter, dataFlowInference)(blueprintDom)

        // Create a new VisualEipGraph, with an IntellijIconLoader mixed in
        val graphComponent = currentlySelectedGraphCreator.createComponent(project, virtualFile, eipGraph)

        graphContainer.add(graphComponent)

        ProgressManager.getInstance().getProgressIndicator.setText("Finished.")
      }
    }, s"Generating ${graphCreator.prettyName}", false, project)
  }

  private def validateInput(blueprint: Option[Blueprint]) = blueprint match {
    case None => false
    case Some(blueprintDom) =>
      if(!isValid(blueprintDom)) {
        val message = "Note - At least one route, with one From definition is required."

        JBPopupFactory.getInstance()
          .createHtmlTextBalloonBuilder(message, MessageType.WARNING, null)
          .createBalloon()
          .show(RelativePoint.getSouthEastOf(graphContainer), Balloon.Position.above)
        false
      } else {
        true
      }
  }

  private def createOptions: JComponent = {
    val container = new JPanel(new FlowLayout())
    val label = new JLabel("Showing Graph :")

    val options = new JComboBox[GraphCreatorOption]()
    graphCreators.foreach(graphCreator => options.addItem(GraphCreatorOption(graphCreator)))

    // When an option is selected, let the graph be re-created as appropriate
    options.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        // </3 Swing - Have to type cast even though JComboBox is generically typed...
        val chosenItem = options.getSelectedItem.asInstanceOf[GraphCreatorOption]
        currentlySelectedGraphCreator = chosenItem.graphCreator
        generateGraph(currentlySelectedGraphCreator)
      }
    })

    container.add(label)
    container.add(options)

    container
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
  def getComponent: JComponent = {
    entireContainer
  }

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

 private case class GraphCreatorOption(graphCreator: GraphCreator) {
   override def toString: String = graphCreator.prettyName
 }
}
