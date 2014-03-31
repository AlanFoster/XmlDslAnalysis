package foo.tooling.graphing.ultimate

import com.intellij.openapi.graph.builder.GraphDataModel
import java.util
import collection.JavaConverters._
import foo.tooling.graphing.ADT.Edge
import foo.tooling.graphing.EipProcessor

/**
 * Represents the Camel Diagram Data Model. Note this data source is considered immutable
 * and therefore does not implement any creating methods.
 */
class CamelGraphDataModel(nodes: List[EipProcessor], edges: List[Edge[EipProcessor, String]])
    extends GraphDataModel[EipProcessor, Edge[EipProcessor, String]]()  {
  /**
   * {@inheritdoc}
   */
  override def getNodes: util.Collection[EipProcessor] = nodes.asJavaCollection
  /**
   * {@inheritdoc}
   */
  override def getEdges: util.Collection[Edge[EipProcessor, String]] = edges.asJavaCollection

  /**
   * {@inheritdoc}
   */
  override def createEdge(source: EipProcessor, target: EipProcessor) = ???

  /**
   * {@inheritdoc}
   */
  override def getSourceNode(edge: Edge[EipProcessor, String]): EipProcessor = edge.source

  /**
   * {@inheritdoc}
   */
  override def getTargetNode(edge: Edge[EipProcessor, String]): EipProcessor = edge.target

  /**
   * {@inheritdoc}
   */
  override def getNodeName(node: EipProcessor): String = node.text

  /**
   * {@inheritdoc}
   */
  override def getEdgeName(edge: Edge[EipProcessor, String]): String = edge.edge

  /**
   * {@inheritdoc}
   */
  override def dispose(): Unit = {}
}
