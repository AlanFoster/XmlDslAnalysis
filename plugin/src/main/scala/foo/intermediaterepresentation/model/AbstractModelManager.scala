package foo.intermediaterepresentation.model

import foo.dom.Model.{ProcessorDefinition, Blueprint}
import com.intellij.psi.xml.XmlTag
import foo.intermediaterepresentation.model.processors.{Route, Processor}
import foo.intermediaterepresentation.model.types.CamelStaticTypes._
import foo.intermediaterepresentation.model.references.DomReference
import foo.intermediaterepresentation.model.types.TypeEnvironment
import foo.intermediaterepresentation.converter.{AbstractModelConverter, DomAbstractModelConverter}
import foo.intermediaterepresentation.typeInference.{AbstractModelTypeInference, TypePropagationTypeInference}

/**
 * A concrete implementation of the AbstractModelFacade, which provides a simple
 * interface for accessing common information within the given intermediate representation
 */
class AbstractModelManager(converter: AbstractModelConverter[Blueprint],
                           typeInference: AbstractModelTypeInference) extends AbstractModelFacade {
  /**
   * Extracts the current node from the given IR
   * @param domFile The blueprint dom file
   * @param currentTag the current tag to extract the IR from
   * @return The IR model of the given XmlTag
   */
  def getCurrentNode(domFile: Blueprint, currentTag: XmlTag): Option[Processor] = {
    val routeWithSemantics = createSemanticModel(domFile)

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
  def getInferredHeaders(domFile: Blueprint, currentTag: XmlTag): Option[Map[ACSLKey, (ACSLFqcn, ProcessorDefinition)]] = {
    // Perform route semantics
    val currentNode = getCurrentNode(domFile, currentTag)
    val availableHeaders = for {
      node <- currentNode
      before <- node.before
      inferredHeaders <- getInferredHeaders(before)
    } yield inferredHeaders

    availableHeaders
  }

  /**
   * Gets the current inferred header information from the given type environment
   * @param typeEnvironment The current type information available
   * @return The inferred headers associated with the current type environment
   */
  def getInferredHeaders(typeEnvironment: TypeEnvironment): Option[Map[ACSLKey, (ACSLFqcn, ProcessorDefinition)]] = typeEnvironment match {
    case TypeEnvironment(_, headerMap) =>
      val mappedValues = headerMap.collect({
        case (key, (inferredType, DomReference(processorDefinition))) => key ->(inferredType, processorDefinition)
      })
      Option(mappedValues)
    case _ => None
  }

  /**
   * Creates the intermediate representation and semantic information for the given blueprint file
   * @param blueprint The Blueprint Element
   * @return The intermediate representation and semantic information for the given blueprint file
   */
  def createSemanticModel(blueprint: Blueprint): Route = {
    // Apply the transformation
    // firstly creating the IR representation, then applying the more expensive type inference
    val route = converter.convert(blueprint)
    val routeWithSemantics = typeInference.performTypeInference(route)

    routeWithSemantics
  }
}