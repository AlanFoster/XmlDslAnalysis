package foo.traversal

import com.intellij.psi._
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiTypesUtil
import scala.Some

object MethodTypeInference {

  /**
   * Attempts to extract the PsiClass information from the given Psimethod.
   * This method will take into consideration the AutoBoxing effect which is applied to types.
   * For instance int -> Integer.
   *
   * @param psiMethod The given PsiMethod to exract the return class from
   * @return The returned PsiClass, which takes into consideration boxing etc
   */
  def getReturnTypeClass(psiMethod: PsiMethod): Option[PsiClass] = {
    val (returnType, project) = (psiMethod.getReturnType, psiMethod.getProject)

    val boxedType = MethodTypeInference.tryBoxPrimitiveType(returnType, project)
    val psiClassOption = boxedType.flatMap(boxedType => Option(PsiTypesUtil.getPsiClass(boxedType)))
    psiClassOption
  }

  /**
   * Attempts to extract the PsiClassType information from the given PsiType.
   * This method will take into consideration the AutoBoxing effect which is applied to types.
   * For instance int -> Integer.
   *
   * @param psiType The PsiType instance, possibly a Primitive or PsiClassType instance
   * @param project The project instance
   * @return The associated PsiClassType information, otherwise None
   */
  private def tryBoxPrimitiveType(psiType: PsiType, project: Project): Option[PsiClassType] = psiType match {
    case primitive: PsiPrimitiveType if psiType != PsiType.VOID =>
      val someBoxedName = Option(primitive.getBoxedTypeName)
      // Create a new PsiClassType reference if the type successfully matches and is a boxed value
      someBoxedName
        .map(boxedName => {
          val elementFactory = JavaPsiFacade.getInstance(project).getElementFactory
          elementFactory.createTypeByFQClassName(boxedName)
        })
    case psiClassType: PsiClassType => Some(psiClassType)
    case psiArrayType:PsiArrayType =>
      tryBoxPrimitiveType(psiArrayType.getComponentType, project)
    case _ => None
  }


}
