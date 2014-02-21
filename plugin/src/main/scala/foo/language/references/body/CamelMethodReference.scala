package foo.language.references.body

import com.intellij.psi._
import com.intellij.openapi.util.TextRange
import foo.language.MethodConverter
import scala.Some
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns._
import scala.Some
import com.intellij.util.xml.ElementPresentationManager
import scala.util.Try
import foo.language.references.EipReference
import foo.traversal.MethodTraversal

/**
 * Represents a concrete implementation of a reference which is used within
 * the apache camel simple language, when accessing an object with methods.
 * @param element The parent element that this reference is bound to
 * @param range The text range within the parent element that this reference
 *              should be provided for
 */
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
    val publicMethods = MethodTraversal.getAllPublicMethods(bodyType)
    val availableVariants = createAvailableVariants(publicMethods)

    availableVariants
  }

  /**
   * Creates the list of available possible variants within the given list of
   * PsiMethods.
   * Note this method is which performs and takes into consideration the ability
   * to create the list of variants containing as-is method names, and
   * to create the list of OGNL variants, filtering any non 'getter' methods
   * @param methods The known list of psi methods to perform variants for
   * @return The list of known OGNL variants and method names
   */
  private def createAvailableVariants(methods: List[PsiMethod]): Array[(PsiMethod, String)] =
    methods.foldLeft(List[(PsiMethod, String)]())((acc, method) => {
      // Additionally unions an OGNL expression variant if it is applicable
      def unionOgnlGetter(list: List[(PsiMethod, String)]) = convertGetterName(method.getName) match {
        case None => list
        case Some(ognlGetterName) => (method, ognlGetterName) :: list
      }

      unionOgnlGetter((method, method.getName) :: acc)
    }).toArray

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
