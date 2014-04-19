package foo.dom.Model

import com.intellij.util.xml.{SubTagList, SubTagsList, DomElement}

trait ChoiceProcessorDefinition extends DomElement with ProcessorDefinition {
  @SubTagsList(Array("when"))
  def getWhenClauses: java.util.List[WhenDefinition]

  @SubTagList("when")
  def getWhens: java.util.List[WhenDefinition]
}
