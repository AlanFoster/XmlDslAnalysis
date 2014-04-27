package foo.language.references

import com.intellij.psi.{JavaPsiFacade, PsiClass, PsiElement}
import com.intellij.openapi.module.Module
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlTag
import foo.intermediaterepresentation.model.types.{CamelType, TypeEnvironment}
import foo.language.Resolving

/**
 * Represents the trait of an EipReference, which provides access to required information
 * which can be inferred through the traversal of the Eip Graph.
 *
 * This trait is associated with a reference within the apache camel simple language
 */
trait EipSimpleReference {

  def resolveEip(typeEnvironment: TypeEnvironment): Set[CamelType]

  /**
   * Computes the currently inferred body type for the given psiElement
   * @param module The module to search within to resolve classes
   * @return The inferred body type. This may be null in the scenario of
   *         not being able to infer the body type. For instance if
   *         language injection is performed within the context of a Java DSL.
   */
  def getInferredBodyTypes(module: Module, bodyTypes: Set[String]):Set[PsiClass] = {
    val searchScope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module)
    def classResolver(className: String): Option[PsiClass] = {
      Option(JavaPsiFacade.getInstance(module.getProject).findClass(className, searchScope))
    }

    // Attempt to find the matching body types and resolve them
    // Ensuring that no unresolved classes leak through
    val resolvedBodies = bodyTypes.map(classResolver).flatten

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

  def getTypeEnvironment(psiElement: PsiElement): Option[TypeEnvironment] =
    Resolving.getTypeEnvironment(psiElement)

  def getTypeEnvironment(parentElement: XmlTag): Option[TypeEnvironment] =
    Resolving.getTypeEnvironment(parentElement)


  /**
   *
   * @param psiElement A simple language PsiElement
   * @return The topmost XmlTag which this element is injected into.
   *         Note this may be None in the case of Java DSL
   */
  def getParentXmlElement(psiElement: PsiElement): Option[XmlTag] =
    Resolving.getParentXmlElement(psiElement)
}

