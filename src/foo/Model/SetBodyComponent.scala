package foo.Model

import com.intellij.util.xml.DomElement

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 04/11/13
 * Time: 17:08
 * To change this template use File | Settings | File Templates.
 */
trait SetBodyComponent extends DomElement with Component {
  def getExpression: Expression
}
