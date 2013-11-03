package foo.Model

import com.intellij.util.xml.{SubTagsList, DomElement}

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 03/11/13
 * Time: 02:02
 * To change this template use File | Settings | File Templates.
 */
trait Route extends DomElement {
  def getFrom: FromComponent

  @SubTagsList(Array("to"))
  def getOutputs: java.util.List[Component]

}
