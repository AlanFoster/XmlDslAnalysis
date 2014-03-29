package foo.graphing.ultimate

import com.intellij.openapi.graph.GraphManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.graph.view._
import com.intellij.openapi.graph.impl.view.PolyLineEdgeRealizerImpl
import com.intellij.openapi.graph.builder.{GraphBuilder, GraphBuilderFactory}
import com.intellij.openapi.actionSystem.{ActionPlaces, ActionManager, DefaultActionGroup}
import com.intellij.openapi.graph.builder.util.GraphViewUtil
import foo.FunctionalUtil._
import javax.swing.JPanel
import java.awt.BorderLayout
import foo.eip.graph.StaticGraphTypes.EipDAG

class IdeaGraphCreator {
  def createComponent(project: Project, file: VirtualFile, eipGraph: EipDAG) = {
    val panel = new JPanel()

    // createOverview
    val builder = createBuilder(project, file, eipGraph)
    val toolBar = createToolBar(builder)

    //val layouter = GraphSettingsProvider.getInstance(project).getSettings(builder.getGraph)

    val createdPanel = mutate(panel)(panel => {
      panel.setLayout(new BorderLayout())
      panel.add(toolBar, BorderLayout.NORTH)
      panel.add(builder.getView.getJComponent, BorderLayout.CENTER)
    })

    println("Successfully initialized graph builder")
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
    // val model = new CamelGraphDataModel()
    /*   val IHateIntelliJ = GenericNodeRealizer.Statics.getFactory
       val defaultConfiguration = IHateIntelliJ.createDefaultConfigurationMap()
       defaultConfiguration.put(classOf[GenericNodeRealizer.Painter], new ImageNodePainter {} {
       })*/
    // graph.setDefaultNodeRealizer(new GenericNodeRealizer())

    // Create our raw graph, which contains the nodes
    val graph: Graph2D = GraphManager.getGraphManager.createGraph2D()
    // Create a view, which allows for the visualization of a graph
    val graphView: Graph2DView = GraphManager.getGraphManager.createGraph2DView()

    // Create our data model and presentation model
    val (edges, nodes) = (eipGraph.vertices, eipGraph.edges)
    val dataModel = new CamelGraphDataModel(edges, nodes)
    val presentationModel = new CamelGraphPresentationModel(graph, project)

    // Create a builder
    val builder = GraphBuilderFactory.getInstance(project)
      .createGraphBuilder(graph, graphView, dataModel, presentationModel)

    builder
  }

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
