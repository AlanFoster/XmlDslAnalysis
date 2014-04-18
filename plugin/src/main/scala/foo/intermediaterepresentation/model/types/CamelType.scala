package foo.intermediaterepresentation.model.types

import com.intellij.psi.{PsiMethod, PsiClass, PsiElement, PsiReference}
import foo.traversal.MethodTypeInference
import foo.intermediaterepresentation.model.CoreConstants

sealed trait CamelType

case class CamelReferenceType(simpleType: BaseType, element: PsiElement) extends CamelType
object CamelReferenceType {
  def apply(fqcn: String, element: PsiElement): CamelReferenceType = {
    CamelReferenceType(BaseType(fqcn), element)
  }

  def apply(element: PsiElement): CamelReferenceType = {
    val fqcn = (element match {
      case psiClass: PsiClass =>
        Some(psiClass.getQualifiedName)
      case psiMethod: PsiMethod =>
        MethodTypeInference.getReturnTypeClass(psiMethod).map(_.getQualifiedName)
      case _ => Some(CoreConstants.DEFAULT_INFERRED_TYPE)
    }).getOrElse(CoreConstants.DEFAULT_INFERRED_TYPE)

    CamelReferenceType(fqcn, element)
  }
}

case class BaseType(fqcn: String) extends CamelType
