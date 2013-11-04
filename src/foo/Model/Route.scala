package foo.Model

import com.intellij.util.xml.{SubTagsList, SubTagList, DomElement}


/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 03/11/13
 * Time: 02:02
 * To change this template use File | Settings | File Templates.
 */

trait Route extends DomElement {
  @SubTagsList(Array("to", "inOut"))
  def getComponents: java.util.List[Component]

  @SubTagList("to")
  def getTos: java.util.List[ToComponent]

  @SubTagList("inOut")
  def getInOuts: java.util.List[ToComponent]

}
