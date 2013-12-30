package foo.dom.Model

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
  def getFrom: FromProcessorDefinition

  @SubTagsList(Array("to", "inOut", "setBody", "choice", "wireTap", "bean", "setHeader", "removeHeader"))
  def getComponents: java.util.List[ProcessorDefinition]

  @SubTagList("wireTap")
  def getWireTaps: java.util.List[WireTapDefinition]

  @SubTagList("bean")
  def getBeans: java.util.List[BeanDefinition]

  @SubTagList("to")
  def getTos: java.util.List[ToProcessorDefinition]

  @SubTagList("inOut")
  def getInOuts: java.util.List[ToProcessorDefinition]

  @SubTagList("setHeader")
  def getSetHeaders: java.util.List[SetHeaderProcessorDefinition]

  @SubTagList("setBody")
  def getBodies: java.util.List[SetBodyProcessorDefinition]

  @SubTagList("choice")
  def getChoices: java.util.List[ChoiceProcessorDefinition]

  @SubTagList("removeHeader")
  def getRemoveHeaders: java.util.List[RemoveHeaderProcessorDefinition]
}
