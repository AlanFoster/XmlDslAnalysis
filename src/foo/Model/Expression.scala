package foo.Model

import com.intellij.util.xml.{TagValue, Required, DomElement}

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 09/11/13
 * Time: 23:57
 * To change this template use File | Settings | File Templates.
 */
trait Expression extends DomElement {
  @TagValue
  def getValue: String
}
