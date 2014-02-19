package foo.language.references

import com.intellij.psi._
import foo.language.generated.psi.{CamelFunctionArgs, CamelCamelFuncBody}
import com.intellij.util.ProcessingContext
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns._
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil
import foo.dom.DomFileAccessor
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import foo.eip.graph.EipGraphCreator
import java.util.Collections
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.openapi.module.ModuleUtilCore
import foo.language.psi.impl.ElementSplitter
import scala.Some
import foo.language.MethodConverter
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.xml.ElementPresentationManager
import scala.Some
import scala.util.Try

/**
 * Provides reference contribution for the apache camel language, using
 * the body accessor notation - ie ${body<caret>}
 */
class CamelBodyReferenceContributor extends PsiReferenceContributor {
  /**
   * Psi Patterns that this contribution applies to
   */
  val VARIABLE_ACCESS_PATTERN = {
    val element = psiElement(classOf[CamelCamelFuncBody])
    element
      .andNot(element.withChild(psiElement(classOf[CamelFunctionArgs])))
  }

  override def registerReferenceProviders(registrar: PsiReferenceRegistrar): Unit = {
    registrar.registerReferenceProvider(VARIABLE_ACCESS_PATTERN, new PsiReferenceProvider {
      override def getReferencesByElement(element: PsiElement, context: ProcessingContext): Array[PsiReference] = {
        val originalElement = element.getOriginalElement
        val text = originalElement.getText
        val splitSections = ElementSplitter.split(text).reverse

        // Only provide a reference if body is within our split text sections
        // Obviously this implementation is wrong, since more tests are required
        // to fail this implementation :)
        val validSections = splitSections
          .dropWhile(_._1 != "body");

        validSections
          .map({ case (string, start, end) =>
            val textRange = new TextRange(start, end)
            if(string == "body") {
              new CamelBodyReference(originalElement, textRange)
            } else {
              new CamelMethodReference(originalElement, textRange)
            }
        }).toArray
        }
    })
  }
}

trait EipReference {
  def getBodyType(element: PsiElement):PsiClass = {
    // Define the class resolver and scope in which this body could possibly reference
    val module = ModuleUtilCore.findModuleForPsiElement(element)
    val searchScope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module)
    val classResolver =
      (className: String) => JavaPsiFacade
        .getInstance(element.getProject)
        .findClass(className, searchScope)

    // Attempt to find the matching body type to resolve to
    val resolvedBody =
      getBodyTypes(element)
        .map(classResolver)
        .headOption
        .getOrElse(null)

    resolvedBody
  }

  /**
   * Gets the available body types within this context
   * @return The Map of available headers within the given context
   */
  def getBodyTypes(element: PsiElement) = {
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

class CamelMethodReference(element: PsiElement, range: TextRange)
  // Note this reference is a soft reference, ie if it doesn't resolve, it is *not* an error!
  extends PsiReferenceBase[PsiElement](element, range, false)
  with MethodConverter
  with EipReference {

    /**
     * No variants are possible within this reference
     */
    override def getVariants: Array[AnyRef] = {
      val availableVariants = foo()

      // Create lookups for method access *and* the OGNL getter notation
      val lookUpElements = createLookupElements(availableVariants)

      // Create the array of variants
      val providedVariants = lookUpElements.asInstanceOf[Array[AnyRef]]
      providedVariants
    }

  private def foo(): Array[(PsiMethod, String)] = {
    val bodyType = getBodyType(element)

    if(bodyType == null) return Array()

    // Access all public methods - minus constructors
    val publicMethods = getPublicMethods(bodyType)
    val availableVariants = createAvailableVariants(publicMethods)

    availableVariants
  }

  /**
   * Creates the list of available possible variants within the given list of
   * PsiMethods.
   * Note this method is which performs and takes into consideration the ability
   * to create the list of variants containing as-is method names, and
   * to create the list of OGNL variants, filtering any non 'getter' methods
   * @param methods
   * @return
   */
  private def createAvailableVariants(methods: Array[PsiMethod]): Array[(PsiMethod, String)] = {
    // TODO Rewrite with fold left
    {
      methods.map(method => (method, method.getName))
    }
    .union
    {
      methods.map(method => (method, convertGetterName(method.getName)))
        .filter(_._2.isDefined)
        .map(tuple => tuple.copy(_2 = tuple._2.get))
    }
  }

  /**
   * Populates the list of public methods, minus the constructor, which
   * is available within this psi class
   * @param psiClass The given Psi Class
   * @return The list of PsiMethods which hold true to thie predicate
   */
  private def getPublicMethods(psiClass: PsiClass): Array[PsiMethod] = {
    psiClass
      .getAllMethods
      // Public accessors only
      .filter(_.getModifierList.hasModifierProperty(PsiModifier.PUBLIC))
      // We shouldn't suggest constructors
      .filter(!_.isConstructor)
  }

  /**
   * Creates the lookup elements for the given variants
   * @param variants The tuple of given variants
   * @return The newly created look up elements
   */
    private def createLookupElements(variants: Array[(PsiMethod, String)]): Array[LookupElementBuilder] =
      variants
        .distinct
          .map({
          case (method, variantName) =>
            LookupElementBuilder.create(psiElement, variantName)
              .withIcon(ElementPresentationManager.getIcon(method))
        })

    /**
     * Resolves to the method which this text should align with
     * Note this also takes into consideration the OGNL expression support
     * @return The given reference is resolved, otherwise false
     */
    override def resolve(): PsiElement = {
      val methodName = Try(element.getText.substring(range.getStartOffset, range.getEndOffset)).toOption
      methodName match {
        case None => null
        case Some(value) =>
          foo()
            .find(_._2 == value)
            .map(_._1)
            .getOrElse(null)
      }
    }
}

class CamelBodyReference(element: PsiElement, range: TextRange)
  // Note this reference is a soft reference, ie if it doesn't resolve, it is *not* an error!
  extends PsiReferenceBase[PsiElement](element, range, false)
  with MethodConverter
  with EipReference {

  /**
   * No variants are possible within this reference
   */
  override def getVariants: Array[AnyRef] = {
    PsiReference.EMPTY_ARRAY.asInstanceOf[Array[AnyRef]]
  }

  /**
   * Attempts to resolve to the matching PsiClass body
   * @return The resolved PsiClass, otherwise null.
   */
  override def resolve(): PsiElement = {
    getBodyType(element)
  }
}

