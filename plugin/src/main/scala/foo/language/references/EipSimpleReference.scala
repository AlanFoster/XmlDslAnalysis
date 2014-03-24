package foo.language.references

import com.intellij.psi.{JavaPsiFacade, PsiClass, PsiElement}
import com.intellij.openapi.module.Module
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil
import foo.dom.DomFileAccessor
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import foo.eip.model._

/**
 * Represents the trait of an EipReference, which provides access to required information
 * which can be inferred through the traversal of the Eip Graph.
 *
 * This trait is associated with a reference within the apache camel simple language
 */
trait EipSimpleReference {

  def resolveEip(typeEnvironment: TypeEnvironment): Set[PsiElement]

  /**
   * Computes the currently inferred body type for the given psiElement
   * @param module The module to search within to resolve classes
   * @return The inferred body type. This may be null in the scenario of
   *         not being able to infer the body type. For instance if
   *         language injection is performed within the context of a Java DSL.
   */
  def getInferredBodyTypes(module: Module, bodyTypes: Set[String]):Set[PsiClass] = {
    val searchScope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module)
    def classResolver(className: String) = {
      JavaPsiFacade.getInstance(module.getProject).findClass(className, searchScope)
    }

    // Attempt to find the matching body types and resolve them
    val resolvedBodies = bodyTypes.map(classResolver)

    resolvedBodies
  }

  /**
   * Gets the available body classes which can be inferred under this context for
   * the given PsiElement
   *
   * @param parentElement The PsiElement to provide type inference for
   * @return The set of known body types. This will never be null.
   */
  def getBodyTypeFqcns(parentElement: XmlTag): Set[String] = {
    getTypeEnvironment(parentElement).map(_.body).getOrElse(Set())
  }

  def getTypeEnvironment(parentElement: XmlTag): Option[TypeEnvironment] = {
    val (project, psiFile) = (parentElement.getProject, parentElement.getContainingFile)
    val domFileOption = DomFileAccessor.getBlueprintDomFile(project, psiFile)

    val typeEnvironment = domFileOption flatMap {
      case domFile =>
        val currentNode: Option[Processor] = AbstractModelManager.getCurrentNode(domFile, parentElement)
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


