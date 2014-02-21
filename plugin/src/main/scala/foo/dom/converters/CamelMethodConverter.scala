package foo.dom.converters

import com.intellij.util.xml.{DomUtil, ConvertContext, ResolvingConverter}
import com.intellij.psi.PsiMethod
import java.util
import collection.JavaConverters._
import foo.dom.Model.BeanDefinition
import foo.traversal.MethodTraversal

/**
 * Converter for methods support within camel bean references.
 * Note this method provides variant support through the ResolvingConverter
 * implementation
 */
class CamelMethodConverter extends ResolvingConverter[PsiMethod] {
  /**
   * {@inheritdoc}
   */
  override def toString(t: PsiMethod, context: ConvertContext): String =
    t.getName

  /**
   * {@inheritdoc}
   */
  override def fromString(s: String, context: ConvertContext): PsiMethod = {
    val reference =
      getAllMethods(context)
      .find(_.getName == s)
      .getOrElse(null)

    reference
  }

  /**
   * Provides method contribution for all of the available methods within a given
   * reference. Note this also includes all super methods
   * @param context The convert context
   * @return All possible methods available within this context
   */
  override def getVariants(context: ConvertContext): util.Collection[PsiMethod] =
    getAllMethods(context).asJavaCollection

  /**
   * Provides method contribution for all of the available methods within a given
   * reference. Note this also includes all super methods
   * @param context The convert context
   * @return All possible methods available within this context
   */
  private def getAllMethods(context:ConvertContext): List[PsiMethod] = {
    val camelBean = DomUtil.findDomElement(context.getXmlElement, classOf[BeanDefinition], false)
    val allMethods: List[PsiMethod] = {
      for {
        blueprintBean <- Option(camelBean.getRef.getValue)
        psiClass <- Option(blueprintBean.getPsiClass.getValue)
        methods = MethodTraversal.getAllMethods(psiClass)
      } yield methods
    }.getOrElse(List[PsiMethod]())

    allMethods
  }
}
