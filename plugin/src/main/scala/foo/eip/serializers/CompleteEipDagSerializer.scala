package foo.eip.serializers

import foo.eip.graph.StaticGraphTypes._
import scala.xml.Elem

/**
 * Represents a concrete implementation implementation of an EipDagSerializer
 * which contains all informatoin, such as headers/body inferred type information/semantics
 * from the original model and edges/vertices etc.
 */
class CompleteEipDagSerializer extends EipDagSerializer {
  /**
   * Creates the XML for the given EipDag
   * @param eipDag The EipDag to convert
   * @return The XML Node element
   */
  def createXml(eipDag: EipDAG): Elem =
    <eipDag>
      <vertices>
        {eipDag.vertices.map(vertex => <vertex id={vertex.id} eipType={vertex.eipType.toString.toLowerCase} text={vertex.text} inferredType={vertex.processor.bodies.mkString("{", ", ", "}")} headers={vertex.processor.headers.get.keys.toList.sortBy(identity).mkString("{", ", ", "}")}/>)}
      </vertices>
      <edges>
        {eipDag.edges.map(edge => <edge source={edge.source.id} target={edge.target.id} edgeConnection={edge.edge}/>)}
      </edges>
    </eipDag>
}
