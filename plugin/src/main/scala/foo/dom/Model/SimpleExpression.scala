package foo.dom.Model

import com.intellij.util.xml.{GenericAttributeValue, Convert, Attribute, DomElement}
import org.jetbrains.annotations.NotNull
import com.intellij.psi.PsiClass

trait SimpleExpression extends DomElement with Expression {
  @NotNull
  @Attribute("resultType")
  def getResultType: GenericAttributeValue[PsiClass]
}
