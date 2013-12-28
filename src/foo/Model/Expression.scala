package foo.Model

import com.intellij.util.xml.{TagValue, Required, DomElement}

trait Expression extends DomElement {
  @TagValue
  def getValue: String
  @TagValue
  def setValue(s: String)
}
