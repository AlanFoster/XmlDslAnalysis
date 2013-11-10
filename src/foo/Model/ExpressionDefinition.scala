package foo.Model

import com.intellij.util.xml.{SubTagList, SubTagsList, DomElement}

trait ExpressionDefinition extends DomElement with ProcessorDefinition {
  @SubTagsList(Array("constant"))
  def getExpression: Expression

  @SubTagList("constant")
  def getConstant: ConstantExpression
}
