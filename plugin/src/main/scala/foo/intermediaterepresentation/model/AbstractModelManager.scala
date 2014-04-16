package foo.intermediaterepresentation.model

import foo.dom.Model.{ProcessorDefinition, Blueprint}
import com.intellij.psi.xml.XmlTag
import foo.intermediaterepresentation.converter.DomAbstractModelConverter
import foo.intermediaterepresentation.typeInference.DataFlowTypeInference
import foo.intermediaterepresentation.model.references.{Reference, DomReference}
import foo.intermediaterepresentation.model.processors.{Route, Processor}

/**
 * Represents the singleton instance of an AbstractModelManager, which simply provides
 * helper methods for accessing common information within the given intermediate representation
 */
object AbstractModelManager {
  /**
   * Extracts the current node from the given IR
   * @param domFile The blueprint dom file
   * @param currentTag the current tag to extract the IR from
   * @return The IR model of the given XmlTag
   */
  def getCurrentNode(domFile: Blueprint, currentTag: XmlTag): Option[Processor] = {
    val route = new DomAbstractModelConverter().convert(domFile)
    val routeWithSemantics = new DataFlowTypeInference().performTypeInference(route)

    val currentNode = routeWithSemantics.collectFirst({
      case Processor(DomReference(reference), _) =>
        reference.getXmlTag == currentTag
    })
    currentNode
  }

  /**
   * Gets the current inferred header information from the given IR
   * @param domFile The blueprint dom file
   * @param currentTag the current tag to extract the IR from
   * @return The inferred headers associated with the current processor
   */
  def getInferredHeaders(domFile: Blueprint, currentTag: XmlTag): Option[Map[String, ProcessorDefinition]] =  {
    // Perform route semantics
    val currentNode = getCurrentNode(domFile, currentTag)
    val headers: Option[Map[String, (String, Reference)]] = currentNode.flatMap(_.headers)

    // Additionally map the headers to their real reference types
    val availableHeaders = headers
      // Extract the associated element which modified this variable
      .map(map => map.collect({
      case (key, (inferredType, DomReference(processorDefinition))) => (key, processorDefinition)
    }))

    availableHeaders
  }

  /**
   * Creates the intermediate representation and semantic information for the given blueprint file
   * @param blueprint The Blueprint Element
   * @return The intermediate representation and semantic information for the given blueprint file
   */
  def createSemanticIntermediateRepresentation(blueprint: Blueprint): Route = {
    // Instantiate the methods of creating an abstract dom model and semantic information for later DI
    // IE, this converts to an IR and then performs type inference on the structure
    val modelConverter = new DomAbstractModelConverter()
    val dataFlowInference = new DataFlowTypeInference()

    // Apply the transformation
    // firstly creating the IR representation, then applying the more expensive type inference
    val route = modelConverter.convert(blueprint)
    val routeWithSemantics = dataFlowInference.performTypeInference(route)

    routeWithSemantics
  }
}