package foo.graphing.ultimate

import com.intellij.openapi.graph.builder.GraphDataModel
import java.util
import collection.JavaConverters._
import foo.eip.graph.EipProcessor
import foo.eip.graph.ADT.Edge

/**
 * Represents the Camel Diagram Data Model.
 */
class CamelGraphDataModel(nodes: List[EipProcessor], edges: List[Edge[EipProcessor, String]])
    extends GraphDataModel[EipProcessor, Edge[EipProcessor, String]]()  {
  override def getNodes: util.Collection[EipProcessor] = nodes.asJavaCollection
  override def getEdges: util.Collection[Edge[EipProcessor, String]] = edges.asJavaCollection

  override def createEdge(source: EipProcessor, target: EipProcessor) = ???
  override def getSourceNode(edge: Edge[EipProcessor, String]): EipProcessor = edge.source
  override def getTargetNode(edge: Edge[EipProcessor, String]): EipProcessor = edge.target

  override def getNodeName(node: EipProcessor): String = node.text
  override def getEdgeName(edge: Edge[EipProcessor, String]): String = edge.edge

  override def dispose(): Unit = {}
}
