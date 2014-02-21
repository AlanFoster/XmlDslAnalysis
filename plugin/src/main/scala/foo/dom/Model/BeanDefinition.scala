package foo.dom.Model

import com.intellij.util.xml._
import org.jetbrains.annotations.NotNull
import foo.dom.converters.{CamelMethodConverter, BlueprintBeanConverter}
import com.intellij.psi.PsiMethod

trait BeanDefinition extends DomElement with ProcessorDefinition {
  @SubTag("ref")
  @Required(value = true, nonEmpty = true, identifier = false)
  @Convert(value = classOf[BlueprintBeanConverter], soft = false)
  def getRef: GenericAttributeValue[BlueprintBean]

  @NotNull
  @Attribute("method")
  @Convert(value = classOf[CamelMethodConverter], soft = false)
  def getMethod: GenericAttributeValue[PsiMethod]
}
