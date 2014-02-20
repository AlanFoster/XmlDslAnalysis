package foo.dom.Model

import com.intellij.util.xml._
import org.jetbrains.annotations.NotNull
import com.intellij.psi.PsiClass

trait BlueprintBean extends DomElement {
  @NotNull
  @Attribute("id")
  @NameValue(unique = false)
  @Required(value = true, nonEmpty = true, identifier = true)
  def getId: GenericAttributeValue[String]

  @NotNull
  @Attribute("class")
  @Required(value = true, nonEmpty = true, identifier = false)
  def getPsiClass: GenericAttributeValue[PsiClass]
}
