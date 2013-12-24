package foo.Model

import com.intellij.util.xml._
import org.jetbrains.annotations.NotNull

trait WhenDefinition extends DomElement with ExpressionDefinition{
  @NotNull
  @Attribute("id")
  def getId: GenericAttributeValue[String]

  @SubTagsList(Array("to", "inOut", "setBody", "choice", "wireTap", "bean"))
  def getComponents: java.util.List[ProcessorDefinition]

  @SubTagList("wireTap")
  def getWireTaps: java.util.List[WireTapDefinition]

  @SubTagList("bean")
  def getBeans: java.util.List[BeanDefinition]

  @SubTagList("to")
  def getTos: java.util.List[ToProcessorDefinition]

  @SubTagList("inOut")
  def getInOuts: java.util.List[ToProcessorDefinition]

  @SubTagList("setBody")
  def getBodies: java.util.List[SetBodyProcessorDefinition]

  @SubTagList("choice")
  def getChoices: java.util.List[ChoiceProcessorDefinition]
}
