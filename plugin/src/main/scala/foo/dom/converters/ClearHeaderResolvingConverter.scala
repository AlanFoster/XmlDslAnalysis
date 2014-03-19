package foo.dom.converters

import com.intellij.util.xml.{ElementPresentationManager, ConvertContext, ResolvingConverter}
import java.util
import scala.collection.JavaConverters._

import foo.dom.DomFileAccessor
import foo.dom.Model.ProcessorDefinition
import foo.eip.converter.DomAbstractModelConverter
import foo.eip.typeInference.DataFlowTypeInference
import foo.eip.model._
import foo.eip.model.TypeEnvironment
import foo.eip.model.Inferred
import foo.eip.model.DomReference


/**
 * A concrete implementation of a ResolvingConverter which provides variant contribution
 * to the required DOM element, IE ctrl+space support.
 *
 * Specifically this implementation will traverse the abstract EIP graph in order to obtain
 * the currently known semantic/type information about the given DomElement within the tree.
 */
class ClearHeaderResolvingConverter extends ResolvingConverter[ProcessorDefinition] {
  /**
   * Provides a list of the known ProcessorDefinitions which have provided header information
   * to the EIP graph
   * @param context The context
   * @return A list of the known ProcessorDefinitions which have provided header information.
   *         Note, the list should always contain the last element in the EIP graph which
   *         set the header information.
   *         Likewise, the string contribution is provided by the @NameValue annotation of the
   *         given ProcessorDefinition
   */
  def getVariants(context: ConvertContext): util.Collection[_ <: ProcessorDefinition] = {
    val references = getAvailableHeaders(context)
    references.values.asJavaCollection
  }

  /**
   * Converts the given string value into the appropriate reference value
   * @param s The string s
   * @param context The context
   * @return The appropriate reference if it exists, otherwise null
   */
  def fromString(s: String, context: ConvertContext): ProcessorDefinition = {
    val availableHeaders = getAvailableHeaders(context)
    availableHeaders.lift(s).getOrElse(null)
  }

  /**
   * Converts the given reference into a string format, IE after variants are triggered and the user
   * invokes a specific element, this is the value which will be shown.
   * @param t The chosen reference
   * @param context The context
   * @return The string @NameValue representation of the chosen ProcessorDefinition
   */
  def toString(t: ProcessorDefinition, context: ConvertContext): String = ElementPresentationManager.getElementName(t)

  /**
   * Method which allows access to the map of known headers and processor definitions
   * @param context The context
   * @return All known headers within the given context
   */
  def getAvailableHeaders(context: ConvertContext): Map[String, ProcessorDefinition] = {
    val (project, virtualFile) = (context.getProject, context.getFile)

    val currentTag = context.getTag
    val domFile = DomFileAccessor.getBlueprintDomFile(project, virtualFile).get

    // Create and pretty print the produced Eip DAG for the given DOM file
    val route = new DomAbstractModelConverter().createAbstraction(domFile)
    val routeWithSemantics = new DataFlowTypeInference().performTypeInference(route)

    val currentNode = routeWithSemantics.collectFirst({
      case Processor(DomReference(reference), _) =>
        reference.getXmlTag == currentTag
    })

   val headers = currentNode.collect({
      case Processor(_, Inferred(TypeEnvironment(_, headerMap), _)) =>
        headerMap
    })

    val availableHeaders: Map[String, ProcessorDefinition] = headers
      // Extract the associated element which modified this variable
      .map(map => map.collect({
       case (key, (inferredType, DomReference(processorDefinition))) => (key, processorDefinition)
      }))
      .getOrElse(Map())

    availableHeaders
  }
}
