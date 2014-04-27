package foo.language.references.body

import com.intellij.psi._
import com.intellij.openapi.util.TextRange
import foo.language.MethodConverter
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns._
import scala.Some
import com.intellij.util.xml.ElementPresentationManager
import scala.util.Try
import foo.language.references.{CamelRenameFactory, EipSimpleReference}
import foo.traversal.{MethodTypeInference, MethodTraversal}
import com.intellij.openapi.module.ModuleUtilCore
import foo.intermediaterepresentation.model.types.{CamelReferenceType, CamelType, TypeEnvironment}

/**
 * Represents a concrete implementation of a reference which is used within
 * the apache camel simple language, when accessing an object with methods.
 *
 * @param element The parent element that this reference is bound to
 * @param range The text range within the parent element that this reference
 *              should be provided for
 */
class CamelMethodReference(element: PsiElement, range: TextRange, previousReference: Option[EipSimpleReference])
  // Note this reference is a soft reference, ie if it doesn't resolve, it is *not* an error!
  extends PsiReferenceBase[PsiElement](element, range, false)
  with MethodConverter
  with EipSimpleReference {

  /**
   * No variants are possible within this reference
   */
  override def getVariants: Array[AnyRef] = {
    val typeEnvironment = getParentXmlElement(myElement).flatMap(parent => getTypeEnvironment(parent))
    val availableVariants = typeEnvironment.map(typeEnvironment => contributeAvailableVariants(typeEnvironment)).getOrElse(Set())

    // Create lookups for method access *and* the OGNL getter notation
    val lookUpElements = availableVariants.map(createLookupElements)

    // Create the array of variants
    val providedVariants = lookUpElements.flatten.toArray[AnyRef]
    providedVariants
  }

  /**
   * Computes the available variants available under the given method context
   * @return The Set of possible variants available
   */
  private def contributeAvailableVariants(typeEnvironment: TypeEnvironment): Set[Array[(PsiMethod, String)]] = {
    val module = ModuleUtilCore.findModuleForPsiElement(myElement)
    val psiClasses =
      // If we do not have a previous reference, then we are simply the first method call
      // And we should compute the inferred type information from the EIP graph
      if(previousReference.isEmpty) getInferredBodyTypes(module, typeEnvironment.body)
      // otherwise we must resolve the reference, and provide the contributions from that
      else resolveMethod(previousReference.get, typeEnvironment)

    val unionAvailableVariants = for {
      psiClass <- psiClasses
    } yield {
      // Access all public methods - minus constructors
      val publicMethods = MethodTraversal.getAllPublicMethods(psiClass)
      val availableVariants = createAvailableVariants(publicMethods)
      availableVariants
    }

    unionAvailableVariants
  }

  /**
   * Attempts to resolve the associated reference, which should return a PsiMethod
   * with the relevent type information to infer the available PsiClasses which can
   * be suggested ot the user
   * @param reference the PsiReference that this CamelMethodReference depends on
   * @return The set of possible PsiClasses that can be inferred currently
   */
  def resolveMethod(reference: EipSimpleReference, typeEnvironment: TypeEnvironment): Set[PsiClass] = {
    // TODO Note we currently only resolve to the first head element
    val someResolved: Option[PsiElement] =
      Option(reference.resolveEip(typeEnvironment)
        .collect({ case CamelReferenceType(_, p) => p}))
        .flatMap(_.headOption)

    // Attempt to resolve the current class, handling null scenarios with monads
    val resolvedClass = for {
      resolved <- someResolved
      psiMethod <- Try(resolved.asInstanceOf[PsiMethod]).toOption
      psiClass <- MethodTypeInference.getReturnTypeClass(psiMethod)
    } yield psiClass

    // Handle the scenario in which we have not resolved successfully
    resolvedClass match {
      case Some(psiClass) => Set(psiClass)
      case None => Set[PsiClass]()
    }
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
          .withTypeText(method.getContainingClass.getName)
    })

  /**
   * Resolves to the method which this text should align with
   * Note this also takes into consideration the OGNL expression support
   * @return The given reference is resolved, otherwise false
   */
  override def resolve(): PsiElement = {
    val typeEnvironment = getTypeEnvironment(myElement)
    val availableVariants = typeEnvironment.map(typeEnvironment => contributeAvailableVariants(typeEnvironment)).getOrElse(Set())

    resolveForIj(availableVariants).getOrElse(null)
  }

  override def resolveEip(typeEnvironment: TypeEnvironment): Set[CamelType] = {
    val resolved = resolveForIj(contributeAvailableVariants(typeEnvironment))
        .map(s => CamelReferenceType(s): CamelType)
        .map(resolved => Set(resolved))

    resolved.getOrElse(Set())
  }

  private def resolveForIj(availableVariants: Set[Array[(PsiMethod, String)]]): Option[PsiMethod] = {
    val methodName = Try(element.getText.substring(range.getStartOffset, range.getEndOffset)).toOption
    methodName match {
      case None => None
      case Some(value) =>
              // TODO Multiple resolve
              availableVariants
                  .flatten
                  .find(_._2 == value)
                  .map(_._1)
    }
  }

  /**
   * Handles a method rename
   */
  override def handleElementRename(newElementName: String): PsiElement = {
    val replacementObject = CamelRenameFactory.getMethodRename(myElement, getRangeInElement, newElementName)
    myElement.replace(replacementObject)
    replacementObject
  }
}
