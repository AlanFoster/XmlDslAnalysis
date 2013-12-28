package foo.Model

import com.intellij.util.xml.{SubTag, SubTagList, SubTagsList, DomElement}

trait ExpressionDefinition extends DomElement with ProcessorDefinition {
  // Implementation provided to get this working - Is there no annotation for this already?
  def getExpression: Expression

  @SubTag("constant")
  def getConstant: ConstantExpression

  @SubTag("simple")
  def getSimple: SimpleExpression
}
