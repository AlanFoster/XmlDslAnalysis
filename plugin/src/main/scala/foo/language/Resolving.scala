package foo.language

import com.intellij.psi.PsiElement
import foo.intermediaterepresentation.model.types.{Inferred, TypeEnvironment}
import com.intellij.psi.xml.XmlTag
import foo.dom.DomFileAccessor
import foo.intermediaterepresentation.model.processors.Processor
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.openapi.components.ServiceManager
import foo.intermediaterepresentation.model.AbstractModelFacade

object Resolving {
  /**
   * Extracts the type environment from the given PsiElement
   * @param psiElement The PSI element to extract the type environemtn from
   * @return The inferred type environment
   */
  def getTypeEnvironment(psiElement: PsiElement): Option[TypeEnvironment] = {
    getParentXmlElement(psiElement).flatMap(parent => getTypeEnvironment(parent))
  }

  /**
   * Extracts the type environment from the given XmlTag
   * @param parentElement The parent XML Tag of an expression
   * @return The inferred type environment
   */
  def getTypeEnvironment(parentElement: XmlTag): Option[TypeEnvironment] = {
    val (project, psiFile) = (parentElement.getProject, parentElement.getContainingFile)
    val domFileOption = DomFileAccessor.getBlueprintDomFile(project, psiFile)

    val typeEnvironment = domFileOption flatMap {
      case domFile =>
        val facade = ServiceManager.getService(classOf[AbstractModelFacade])
        val currentNode: Option[Processor] = facade.getCurrentNode(domFile, parentElement)
        currentNode.map(_.typeInformation).collect({
          case Inferred(before, _) => before
        })
    }

    typeEnvironment
  }

  /**
   *
   * @param psiElement A simple language PsiElement
   * @return The topmost XmlTag which this element is injected into.
   *         Note this may be None in the case of Java DSL
   */
  def getParentXmlElement(psiElement: PsiElement): Option[XmlTag] = {
    val project = psiElement.getProject

    // Extract the relevant PsiFile in order to test if we are contained within an XmlFile
    val psiFile = InjectedLanguageUtil.getTopLevelFile(psiElement)
    val domFileOption = DomFileAccessor.getBlueprintDomFile(project, psiFile)

    // If the XmlFile was found, create the EipGraph information to suggest the relevant information
    // Otherwise default to an empty map
    val xmlElement = domFileOption.collect({
      case _ =>
        // Extract the injected host xml element using the InjectedLanguageManager
        val hostXmlText = InjectedLanguageManager.getInstance(psiFile.getProject).getInjectionHost(psiElement)

        // Extract the outter tag
        val getParentTag = (psiElement: PsiElement) => PsiTreeUtil.getParentOfType(psiElement, classOf[XmlTag], true)
        val simpleTag = getParentTag(hostXmlText)
        val outterTag = getParentTag(simpleTag)
        outterTag
    })
    xmlElement
  }
}