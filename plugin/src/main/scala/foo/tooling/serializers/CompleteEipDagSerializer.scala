package foo.tooling.serializers

import foo.tooling.graphing.{EipProcessor, StaticGraphTypes}
import StaticGraphTypes._
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
        {eipDag.vertices.map(vertex => <vertex id={vertex.id} eipType={getEipType(vertex)} text={vertex.text} inferredType={getInferredType(vertex)} headers={getHeaders(vertex)}/>)}
      </vertices>
      <edges>
        {eipDag.edges.map(edge => <edge source={edge.source.id} target={edge.target.id} edgeConnection={edge.edge}/>)}
      </edges>
    </eipDag>
}
