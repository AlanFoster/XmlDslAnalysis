package foo.Model

import com.intellij.util.xml.{SubTagList, SubTagsList, DomElement}

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 10/11/13
 * Time: 19:56
 * To change this template use File | Settings | File Templates.
 */
trait ExpressionDefinition extends DomElement with ProcessorDefinition {
  @SubTagsList(Array("constant"))
  def getExpression: Expression

  @SubTagList("constant")
  def getConstant: ConstantExpression
}
