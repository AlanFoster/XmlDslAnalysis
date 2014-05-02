package foo.intermediaterepresentation

import foo.dom.Model.{ProcessorDefinition, Blueprint}
import com.intellij.psi.xml.XmlTag
import foo.intermediaterepresentation.model.processors.{Route, Processor}
import foo.intermediaterepresentation.model.types.TypeEnvironment
import foo.intermediaterepresentation.model.types.CamelStaticTypes.{ACSLFqcn, ACSLKey}

/**
 * Represents the singleton instance of an AbstractModelManager, which provides a simple
 * interface for accessing common information within the given intermediate representation
 */
trait AbstractModelFacade {
  /**
   * Extracts the current node from the given IR
   * @param domFile The blueprint dom file
   * @param currentTag the current tag to extract the IR from
   * @return The IR model of the given XmlTag
   */
  def getCurrentNode(domFile: Blueprint, currentTag: XmlTag): Option[Processor]

  /**
   * Gets the current inferred header information from the given IR
   * @param domFile The blueprint dom file
   * @param currentTag the current tag to extract the IR from
   * @return The inferred headers associated with the current processor
   */
  def getInferredHeaders(domFile: Blueprint, currentTag: XmlTag): Option[Map[ACSLKey, (ACSLFqcn, ProcessorDefinition)]]


  /**
   * Gets the current inferred header information from the given type environment
   * @param typeEnvironment The current type information available
   * @return The inferred headers associated with the current type environment
   */
  def getInferredHeaders(typeEnvironment: TypeEnvironment): Option[Map[ACSLKey, (ACSLFqcn, ProcessorDefinition)]]

  /**
   * Creates the intermediate representation and semantic information for the given blueprint file
   * @param blueprint The Blueprint Element
   * @return The intermediate representation and semantic information for the given blueprint file
   */
  def createSemanticModel(blueprint: Blueprint): Route
}
