package foo.eip.serializers

import foo.eip.graph.StaticGraphTypes.EipDAG
import scala.xml.{PrettyPrinter, Elem}

/**
 * Represents a concrete implementation of a serializer which can serialize an
 * EipDAG.
 *
 * This concrete implementation does not currently show any type information
 */
// TODO Investigate implicits :)
class EipDagSerializer extends Serializer[EipDAG] {
  /**
   * Serializes the given EipDag into a human-readable format
   * @param eipDag The given eipDag which contains the EIP abstraction
   * @return A, pretty printed, serialized XML version of the given EipDag
   */
  def serialize(eipDag: EipDAG): String = (createXml _ andThen prettyPrint)(eipDag)

  /**
   * Creates the XML for the given EipDag
   * @param eipDag The EipDag to convert
   * @return The XML Node element
   */
  def createXml(eipDag: EipDAG) = {
    <eipDag>
      <vertices>
        {eipDag.vertices.map(vertex => <vertex id={vertex.id} eipType={vertex.eipType} text={vertex.text}/>)}
      </vertices>
      <edges>
        {eipDag.edges.map(edge => <edge source={edge.source.id} target={edge.target.id} edgeConnection={edge.edge}/>)}
      </edges>
    </eipDag>
  }

  /**
   * Pretty prints the given Xml Element
   * @param elem The root node to convert into a string
   * @return A pretty printed string of th egiven Xml Element
   */
  def prettyPrint(elem: Elem) = new PrettyPrinter(Integer.MAX_VALUE, 4).format(elem)
}
