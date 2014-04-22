package foo.language.references.body

import com.intellij.psi._
import com.intellij.openapi.util.TextRange
import foo.language.MethodConverter
import foo.language.references.{CamelRenameFactory, EipSimpleReference}

import com.intellij.openapi.module.{ModuleUtil, ModuleUtilCore}
import foo.intermediaterepresentation.model.types.{CamelReferenceType, CamelType, TypeEnvironment}

/**
 * Represents a CamelBodyReference, IE the element within ${body}.
 * This implementation provides no variant completion and should purely
 * resolve to the inferred body type within the EIP graph.
 *
 * Note this class is a PsiPolyVariantReference implementation as it is possible
 * that the body type is more than one inferred type, ie in the case of
 * performing a choice statement
 *
 * @param element The parent element, presumably func body
 * @param range The text range within the parent element to provide references for
 */
class CamelBodyReference(element: PsiElement, range: TextRange)
// Note this reference is a soft reference, ie if it doesn't resolve, it is *not* an error!
  extends PsiReferenceBase[PsiElement](element, range, false)
  with PsiPolyVariantReference
  with MethodConverter
  with EipSimpleReference {

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
    val bodyTypes = getBodyTypesForElement
    if(bodyTypes.size == 1) bodyTypes.head
    else null
  }

  /**
   * Returns all possible body type references applicable
   */
  override def multiResolve(incompleteCode: Boolean): Array[ResolveResult] = {
    val bodyTypes = getBodyTypesForElement

      bodyTypes
        .map(new PsiElementResolveResult(_))
        .toArray
  }

  def getBodyTypesForElement = {
    val bodyTypes = getParentXmlElement(myElement)
      .map(xmlTag => getBodyTypeFqcns(xmlTag))
      .map(bodies => resolveBody(bodies))
      .getOrElse(Set())
    bodyTypes
  }

  private def resolveBody(bodies: Set[String]): Set[PsiClass] = {
    val module = ModuleUtilCore.findModuleForPsiElement(myElement)
    getInferredBodyTypes(module, bodies)
  }

  override def resolveEip(typeEnvironment: TypeEnvironment): Set[CamelType] = {
    val possibleReferences = resolveBody(typeEnvironment.body)
    // set is invariant on A, so we must perform a map of types
    possibleReferences.map(s => CamelReferenceType(s))
  }

  /**
   * Handles a rename of this element. This element should be renamed when the DOM references it points to are renamed
   * also
   */
  override def handleElementRename(newElementName: String): PsiElement = {
    myElement
  }
}

