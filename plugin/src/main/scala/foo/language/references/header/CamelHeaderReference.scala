package foo.language.references.header

import com.intellij.psi.{PsiReferenceBase, PsiElement}
import com.intellij.openapi.util.TextRange
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.xml.{DomTarget, ElementPresentationManager}
import foo.dom.Model.ProcessorDefinition
import foo.intermediaterepresentation.model.AbstractModelFacade
import foo.language.references.{CamelRenameFactory, EipSimpleReference}
import foo.intermediaterepresentation.model.types.{BaseType, CamelType, TypeEnvironment}
import foo.intermediaterepresentation.model.types.CamelStaticTypes.{ACSLFqcn, ACSLKey}
import com.intellij.pom.references.PomService

/**
 * A concrete implementation of a CamelHeaderReference
 * @param element The target PsiElement to be provided reference to
 * @param range The range within the parent element in which to provide a reference for
 */
class CamelHeaderReference(element: PsiElement, range: TextRange)
  // Note this reference is a hard reference, ie if it doesn't resolve, it is an error - however the
  // syntax highlighting may decide that this will not be FF0000 for instance.
  extends PsiReferenceBase[PsiElement](element, range, false)
  with EipSimpleReference {

  /**
   * Creates the list of known possible headers within the current position of the XmlDocument
   * @return The list of known sets, with duplicates filtered.
   */
  override def getVariants: Array[AnyRef] = {
    // Convert all available completions to a Lookup Builder that IJ understands
    getAvailableHeaders
      .map({
        case (headerName, (_, processor)) =>
          val psiElement = processor.getXmlElement
          LookupElementBuilder.create(psiElement, headerName)
            .withIcon(ElementPresentationManager.getIcon(processor))
      })
      .toArray
  }

  private def getAvailableHeaders: Map[ACSLKey, (ACSLFqcn, ProcessorDefinition)] = {
    val typeEnvironment = getTypeEnvironment(myElement)
    val headers = typeEnvironment.flatMap(AbstractModelFacade.getInferredHeaders)
    headers.getOrElse(Map())
  }

  /**
   * Resolves to the last definition of this header, if it exists.
   * @return The PsiElement which correlates to this value, null otherwise.
   */
  override def resolve(): PsiElement = {
    val headers = getAvailableHeaders
    resolveHeader(headers)
      .map({ case (_, (_, processorDefinition)) => PomService.convertToPsi(DomTarget.getTarget(processorDefinition))})
      .getOrElse(null)
  }

  private def resolveHeader(headers: Map[ACSLKey, (ACSLFqcn, ProcessorDefinition)]): Option[(ACSLKey, (ACSLFqcn, ProcessorDefinition))] = {
    headers
      .find({ case (headerName, processor) => headerName == element.getText })
  }

  /**
   * @inheritdoc
   */
  override def resolveEip(typeEnvironment: TypeEnvironment): Set[CamelType] = {
    val resolvedHeader =
      for {
        headers <- AbstractModelFacade.getInferredHeaders(typeEnvironment)
        resolvedHeader <- resolveHeader(headers)
      } yield resolvedHeader

    resolvedHeader match {
      case None => Set()
      case Some((_, (fqcn, processor))) => Set(BaseType(fqcn))
    }
  }


  /**
   * Handles a rename of this element. This element should be renamed when the DOM references it points to are renamed
   * also
   */
  override def handleElementRename(newElementName: String): PsiElement = {
    val replacementObject = CamelRenameFactory.getIdentifierRename(myElement, newElementName)
    myElement.replace(replacementObject)

    replacementObject
  }
}
