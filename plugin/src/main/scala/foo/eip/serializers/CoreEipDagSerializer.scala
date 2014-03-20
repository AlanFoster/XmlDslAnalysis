package foo.eip.serializers

import foo.eip.graph.StaticGraphTypes
import scala.xml.Elem

/**
 * Represents a concrete implementation of a 'Core' eip Dag serializer.
 * This implementation does not provide any type inference information etc,
 * instead it purely tests the construction of vertices and edges.
 */
class CoreEipDagSerializer extends EipDagSerializer {
  /**
   * Creates the XML for the given EipDag
   * @param eipDag The EipDag to convert
   * @return The XML Node element
   */
  def createXml(eipDag: StaticGraphTypes.EipDAG): Elem =
    <eipDag>
      <vertices>
        {eipDag.vertices.map(vertex => <vertex id={vertex.id} eipType={vertex.eipType.toString.toLowerCase} text={vertex.text}/>)}
      </vertices>
      <edges>
        {eipDag.edges.map(edge => <edge source={edge.source.id} target={edge.target.id} edgeConnection={edge.edge}/>)}
      </edges>
    </eipDag>
}
