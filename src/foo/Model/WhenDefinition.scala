package foo.Model

import com.intellij.util.xml.{SubTagList, SubTagsList, DomElement}

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 09/11/13
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
trait WhenDefinition extends DomElement {
  def getExpression: Expression

  @SubTagsList(Array("to", "inOut", "setBody", "choice"))
  def getComponents: java.util.List[Component]

  @SubTagList("to")
  def getTos: java.util.List[ToComponent]

  @SubTagList("inOut")
  def getInOuts: java.util.List[ToComponent]

  @SubTagList("setBody")
  def getBodies: java.util.List[SetBodyComponent]

  @SubTagList("choice")
  def getChoices: java.util.List[ChoiceComponent]


}
