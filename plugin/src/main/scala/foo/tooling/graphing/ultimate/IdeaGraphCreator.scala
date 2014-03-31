package foo.tooling.graphing.ultimate

import com.intellij.openapi.graph.GraphManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.graph.view._
import com.intellij.openapi.graph.builder.{GraphBuilder, GraphBuilderFactory}
import com.intellij.openapi.actionSystem.{ActionPlaces, ActionManager, DefaultActionGroup}
import com.intellij.openapi.graph.builder.util.GraphViewUtil
import foo.FunctionalUtil._
import javax.swing.JPanel
import java.awt.BorderLayout
import foo.tooling.graphing.{GraphCreator, StaticGraphTypes}
import StaticGraphTypes.EipDAG
import foo.tooling.graphing.strategies.icons.EipIconLoader
import foo.tooling.graphing.strategies.tooltip.ToolTipStrategy

/**
 * A concrete implementation of the GraphCreator trait which interacts
 * with the Graph-API provided by IntelliJ ultimate.
 *
 * {@link foo.tooling.graphing.GraphCreator}
 *
 * @param iconLoader Provide access to an EIP Icon loader strategy implementation
 * @param tooltipStrategy Provide access to a tooltip strategy implementation
 */
class IdeaGraphCreator(iconLoader: EipIconLoader, tooltipStrategy: ToolTipStrategy) extends GraphCreator {
  /**
   * {@inheritdoc}
   */
  override val prettyName: String = "Eip Graph (yFiles)"

  /**
   * {@inheritdoc}
   */
  def createComponent(project: Project, file: VirtualFile, eipGraph: EipDAG) = {
    val panel = new JPanel()

    // createOverview
    val builder = createBuilder(project, file, eipGraph)
    val toolBar = createToolBar(builder)

    val createdPanel = mutate(panel)(panel => {
      panel.setLayout(new BorderLayout())
      panel.add(toolBar, BorderLayout.NORTH)
      panel.add(builder.getView.getJComponent, BorderLayout.CENTER)
    })

    builder.initialize()

    createdPanel
  }

  /**
   * Creates the Builder for the given project and file
   * @param project
   * @param file
   * @return A not-yet initialized builder for graph creation.
   *         IE It is expected that the client calls initialize on the returned instance.
   */
  private def createBuilder(project: Project, file: VirtualFile, eipGraph: EipDAG) = {
    // Create our raw graph, which contains the nodes
    val graph: Graph2D = GraphManager.getGraphManager.createGraph2D()
    // Create a view, which allows for the visualization of a graph
    val graphView: Graph2DView = GraphManager.getGraphManager.createGraph2DView()

    // Create our data model and presentation model
    val (edges, nodes) = (eipGraph.vertices, eipGraph.edges)
    val dataModel = new CamelGraphDataModel(edges, nodes)
    val presentationModel = new CamelGraphPresentationModel(graph, project, iconLoader, tooltipStrategy)

    // Create a builder
    val builder = GraphBuilderFactory.getInstance(project)
      .createGraphBuilder(graph, graphView, dataModel, presentationModel)

    builder
  }

  /**
   * Creates the toolbar associated with this graph. This toolbar contains
   * action buttons such as zooming etc
   * @param builder The graph builder currently being used
   * @return The associated toolbar
   */
  private def createToolBar(builder: GraphBuilder[_, _]) = {
    val actionGroup =
      mutate(new DefaultActionGroup())(
        _.add(GraphViewUtil.getBasicToolbar(builder))
      )

    val actionToolBar = ActionManager.getInstance().createActionToolbar(
      ActionPlaces.UNKNOWN,
      actionGroup,
      // isHorizontal
      true
    )

    actionToolBar.getComponent
  }
}
