package foo.dom.converters

import com.intellij.util.xml.{ElementPresentationManager, DomElement, ConvertContext, ResolvingConverter}
import java.util
import com.intellij.psi.PsiElement
import foo.eip.graph.EipGraphCreator
import scala.collection.JavaConverters._
import java.util.Collections
import foo.dom.DomFileAccessor
import foo.dom.Model.ProcessorDefinition


/**
 * A concrete implementation of a ResolvingConverter which provides variant contribution
 * to the required DOM element, IE ctrl+space support.
 *
 * Specifically this implementation will traverse the abstract EIP graph in order to obtain
 * the currently known semantic/type information about the given DomElement within the tree.
 */
class ClearHeaderResolvingConverter extends ResolvingConverter[ProcessorDefinition] {
  def getVariants(context: ConvertContext): util.Collection[_ <: ProcessorDefinition] = {
    val references = getAvailableHeaders(context)
    references.values.asJavaCollection
  }

  def fromString(s: String, context: ConvertContext): ProcessorDefinition = {
    val availableHeaders = getAvailableHeaders(context)
    availableHeaders.lift(s).getOrElse(null)
  }

  def toString(t: ProcessorDefinition, context: ConvertContext): String = ElementPresentationManager.getElementName(t)

  def getAvailableHeaders(context: ConvertContext): Map[String, ProcessorDefinition] = {
    val (project, virtualFile) = (context.getProject, context.getFile)

    val currentTag = context.getTag
    val domFile = DomFileAccessor.getBlueprintDomFile(project, virtualFile).get
    val graph = new EipGraphCreator().createEipGraph(domFile)
    val headers = graph.vertices.find(_.psiReference.getXmlTag == currentTag).map(_.semantics.headers)
    val availableHeaders = headers.getOrElse(Map())
    availableHeaders
  }


}
