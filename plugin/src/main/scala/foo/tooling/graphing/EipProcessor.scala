package foo.tooling.graphing

import foo.intermediaterepresentation.model.EipName._
import foo.intermediaterepresentation.model.processors.Processor
import foo.intermediaterepresentation.model.references.{Reference, DomReference, NoReference}

/**
 * Represents a wrapper of a processor, which contains the additional information associated
 * with a given eip node.
 * @param text The expression text associated with this eip processor
 * @param id A unique ID given to this processor within a DAG
 * @param eipType The EipType information
 * @param processor The processor that is associated with this object
 */
case class EipProcessor(text: String, id: String, eipType: EipName, processor: Processor)

/**
  * Created by a on 30/03/14.
  */
object EipProcessor {
  /**
   * Creates a new instance of an EipProcessor with the given information
   * @param text The text associated with the eip procesor
   * @param processor The encapsulated processor
   * @return A new instance of the EipProcessor
   */
   def apply(text: String, processor: Processor): EipProcessor = processor match {
     case processor@Processor(reference, _) =>
       EipProcessor(text, getId(reference), processor.eipType, processor)
   }

  /**
   * Extracts the ID assocaited with the given reference
   * @param reference The reference associated with the EipProcessor
   * @return The unique ID, otherwise `Not Inferred`
   */
   private def getId(reference: Reference) = reference match {
     case NoReference =>
       "Not Inferred"
     case DomReference(domReference) =>
       domReference.getId.getStringValue
   }
 }
