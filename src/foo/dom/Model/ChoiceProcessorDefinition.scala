package foo.dom.Model

import com.intellij.util.xml.{SubTagList, SubTagsList, DomElement}

/**
 * Created with IntelliJ IDEA.
 * User: alan
 * Date: 09/11/13
 * Time: 23:53
 * To change this template use File | Settings | File Templates.
 */
trait ChoiceProcessorDefinition extends DomElement with ProcessorDefinition {
  @SubTagsList(Array("when"))
  def getWhenClauses: java.util.List[WhenDefinition]

  @SubTagList("when")
  def getWhens: java.util.List[WhenDefinition]
}
