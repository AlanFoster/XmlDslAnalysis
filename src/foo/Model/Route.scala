package foo.Model

import com.intellij.util.xml.{SubTag, SubTagsList, SubTagList, DomElement}


/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 03/11/13
 * Time: 02:02
 * To change this template use File | Settings | File Templates.
 */

trait Route extends DomElement {
  @SubTag("from")
  def getFrom: FromComponent

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
