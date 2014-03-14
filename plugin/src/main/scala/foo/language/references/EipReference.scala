package foo.language.references

import com.intellij.psi.{JavaPsiFacade, PsiClass, PsiElement}
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil
import foo.dom.DomFileAccessor
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import foo.eip.graph.EipGraphCreator

/**
 * Represents the trait of an EipReference, which provides access to required information
 * which can be inferred through the traversal of the Eip Graph
 */
trait EipReference {
  /**
   * Computes the currently inferred body type for the given psiElement
   * @param element The Psi Element
   * @return The inferred body type. This may be null in the scenario of
   *         not being able to infer the body type. For instance if
   *         language injection is performed within the context of a Java DSL.
   */
  def getInferredBodyTypes(element: PsiElement):Set[PsiClass] = {
    // Define the class resolver and scope in which this body could possibly reference
    val module = ModuleUtilCore.findModuleForPsiElement(element)
    val searchScope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module)
    val classResolver =
      (className: String) => JavaPsiFacade
        .getInstance(element.getProject)
        .findClass(className, searchScope)

    // Attempt to find the matching body types and resolve them
    val resolvedBodies =
      getBodyTypeFqcns(element)
        .map(classResolver)

    resolvedBodies
  }

/*  def getMethodReturnType(psiMethod: PsiMethod): String = {
    psiMethod.getContainingClass //.getName
  }*/

  /**
   * Gets the available body classes which can be inferred under this context for
   * the given PsiElement
   *
   * @param element The PsiElement to provide type inference for
   * @return The set of known body types. This will never be null.
   */
  def getBodyTypeFqcns(element: PsiElement): Set[String] = {
    val project = element.getProject

    // Extract the relevant PsiFile in order to test if we are contained within an XmlFile
    val psiFile = InjectedLanguageUtil.getTopLevelFile(element)
    val domFileOption = DomFileAccessor.getBlueprintDomFile(project, psiFile)

    // If the XmlFile was found, create the EipGraph information to suggest the relevant information
    // Otherwise default to an empty map
    val availableBodyTypes: Set[String] = domFileOption match {
      case None => Set()
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
        val bodyTypes = graph.vertices.find(_.psiReference.getXmlTag == outterTag).map(_.semantics.possibleBodyTypes)
        bodyTypes.getOrElse(Set())
      }
    }

    availableBodyTypes
  }
}


