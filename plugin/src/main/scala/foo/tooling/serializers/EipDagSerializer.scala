package foo.tooling.serializers

import foo.tooling.graphing.{EipProcessor, StaticGraphTypes}
import StaticGraphTypes.EipDAG
import scala.xml.{PrettyPrinter, Elem}

/**
 * Represents a concrete implementation of a serializer which can serialize an
 * EipDAG.
 *
 * This concrete implementation does not currently show any type information
 */
trait EipDagSerializer extends Serializer[EipDAG] {
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
  def createXml(eipDag: EipDAG): Elem

  /**
   * Pretty prints the given Xml Element
   * @param elem The root node to convert into a string
   * @return A pretty printed string of th egiven Xml Element
   */
  def prettyPrint(elem: Elem) = new PrettyPrinter(Integer.MAX_VALUE, 4).format(elem)

  /**
   * Extracts the EIP Type from the eipProcessor
   * @param eipProcessor the Eip Processor
   * @return The EipType
   */
  def getEipType(eipProcessor: EipProcessor) = eipProcessor.eipType.toString.toLowerCase

  /**
   * Extracts the inferredBody type
   * @param eipProcessor the Eip Processor
   * @return The inferred body type
   */
  def getInferredType(eipProcessor: EipProcessor) = eipProcessor.processor.outBodies.get.mkString("{", ", ", "}")

  /**
   * Extracts the inferredBody headers
   * @param eipProcessor the Eip Processor
   * @return The inferred headers
   */
  def getHeaders(eipProcessor: EipProcessor) = eipProcessor.processor.outHeaders.get.keys.toList.sortBy(identity).mkString("{", ", ", "}")

}
