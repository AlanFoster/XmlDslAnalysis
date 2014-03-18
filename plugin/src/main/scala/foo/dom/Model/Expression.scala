package foo.dom.Model

import com.intellij.util.xml.{TagValue, DomElement}

trait Expression extends DomElement {
  @TagValue
  def getValue: String
  @TagValue
  def setValue(s: String)
}
