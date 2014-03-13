package foo.language.references.header

import com.intellij.psi.{PsiReferenceBase, PsiElement}
import com.intellij.openapi.util.TextRange
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.xml.ElementPresentationManager
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil
import foo.dom.DomFileAccessor
import foo.dom.Model.ProcessorDefinition
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import foo.eip.graph.EipGraphCreator

/**
 * A concrete implementation of a CamelHeaderReference
 * @param element The target PsiElement to be provided reference to
 * @param range The range within the parent element in which to provide a reference for
 */
class CamelHeaderReference(element: PsiElement, range: TextRange)
  // Note this reference is a hard reference, ie if it doesn't resolve, it is an error - however the
  // syntax highlighting may decide that this will not be FF0000 for instance.
  extends PsiReferenceBase[PsiElement](element, range, false) {

  /**
   * Creates the list of known possible headers within the current position of the XmlDocument
   * @return The list of known sets, with duplicates filtered.
   */
  override def getVariants: Array[AnyRef] = {
    // Convert all available completions to a Lookup Builder that IJ understands
    getAvailableHeaders
      .map({
      case (headerName, processor) =>
        val psiElement = processor.getXmlElement
        LookupElementBuilder.create(psiElement, headerName)
          .withIcon(ElementPresentationManager.getIcon(processor))
    })
      .toArray
  }

  /**
   * Resolves to the last definition of this header, if it exists.
   * @return The PsiElement which correlates to this value, null otherwise.
   */
  override def resolve(): PsiElement = {
    val resolvedHeader =
      getAvailableHeaders
        .find({ case (headerName, processor) => headerName == element.getText })
        .map(_._2.getXmlElement)
        .getOrElse(null)

    resolvedHeader
  }

  /**
   * Gets the available headers within this context
   * @return The Map of available headers within the given context
   */
  def getAvailableHeaders = {
    val project = element.getProject

    // Extract the relevent PsiFile in order to test if we are contained within an XmlFile
    val psiFile = InjectedLanguageUtil.getTopLevelFile(element)
    val domFileOption = DomFileAccessor.getBlueprintDomFile(project, psiFile)

    // If the XmlFile was found, create the EipGraph information to suggest the relevant information
    // Otherwise default to an empty map
    val availableHeaders: Map[String, ProcessorDefinition] = domFileOption match {
      case None => Map()
      case Some(domFile) => {
        // Extract the injected host xml element using the InjectedLanguageManager
        val hostXmlText = InjectedLanguageManager.getInstance(psiFile.getProject)
          .getInjectionHost(element)

        // Extract the outter tag
        val getParentTag = (psiElement: PsiElement) => PsiTreeUtil.getParentOfType(psiElement, classOf[XmlTag], true)
        val simpleTag = getParentTag(hostXmlText)
        val outterTag = getParentTag(simpleTag)

        // Calculate graph and headers
        val graph = new EipGraphCreator().createEipGraph(domFile)
        val headers = graph.vertices.find(_.psiReference.getXmlTag == outterTag).map(_.semantics.headers)
        headers.getOrElse(Map())
      }
    }

    availableHeaders
  }
}
