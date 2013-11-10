package foo.graph

import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import edu.uci.ics.jung.graph.util.EdgeType
import javax.swing.{WindowConstants, JFrame}
import edu.uci.ics.jung.visualization.VisualizationViewer
import edu.uci.ics.jung.algorithms.layout.TreeLayout
import foo.graph.ADT.{Graph, EmptyDAG}


object Testing {
  def main(args: Array[String]) {
/*
    val one = new EipComponent("from")
    val two = new EipComponent("to")
    val three = new EipComponent("hi");

    val dag = EmptyDAG[EipComponent, String]()
      .addVertex(one)
      .addVertex(two)
      .addVertex(three)
      .addEdge("a", one, two)
      .addEdge("b", one, three)

    val newGraph = asJungGraph[EipComponent, String](dag)
    val component = asVisualGraph(newGraph)

    val jframe = new JFrame()
    jframe.setSize(500, 700)
    jframe.getContentPane.add(component)
    jframe.setVisible(true)
    jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)*/

  }
}

class EipComponent(val id: String, val eipType: String, val text: String)
object EipComponent {
  def apply(id: String, eipType: String, text: String) = new EipComponent(id, eipType, text)
}